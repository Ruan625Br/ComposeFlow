package io.composeflow.model.state

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import io.composeflow.ViewModelConstant
import io.composeflow.asVariableName
import io.composeflow.kotlinpoet.ClassHolder
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.datatype.DataTypeId
import io.composeflow.model.datatype.EmptyDataType
import io.composeflow.model.parameter.wrapper.InstantWrapper
import io.composeflow.model.project.CanvasEditable
import io.composeflow.model.project.Project
import io.composeflow.model.project.findComposeNodeOrNull
import io.composeflow.model.project.findDataTypeOrThrow
import io.composeflow.model.project.findLocalStateOrNull
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.model.type.convertCodeFromType
import io.composeflow.serializer.FallbackInstantSerializer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

typealias StateId = String

@Serializable
sealed interface ReadableState {
    val id: StateId
    var name: String
    val isList: Boolean
    fun generateVariableInitializationBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock

    fun generateReadBlock(project: Project, context: GenerationContext, dryRun: Boolean): CodeBlock
    fun getReadVariableName(project: Project): String = name.asVariableName()
    fun getFlowName(): String = name.asVariableName() + "Flow"

    /**
     * [asNonList] If set to true, returns the result type with [ComposeFlowType.isList] = false regardless of the
     * actual [ComposeFlowType.isList] property
     */
    fun valueType(project: Project, asNonList: Boolean = false): ComposeFlowType
    fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> = emptyList()
}

@Serializable
sealed interface WriteableState : ReadableState {
    /**
     * Represents if the state is writable in the Set state action.
     * For example, some states are not intended to be writable from the Set state action,
     * instead, it should be changed through a specific action such as "Open date picker" action.
     */
    val userWritable: Boolean
    fun generateWriteBlock(
        project: Project,
        canvasEditable: CanvasEditable,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock

    fun getUpdateMethodName(): String
    fun generateUpdateStateMethodToViewModel(): FunSpec
    fun generateUpdateStateCodeToViewModel(
        project: Project,
        context: GenerationContext,
        readProperty: AssignableProperty,
        dryRun: Boolean,
    ): CodeBlock

    fun generateClearStateCodeToViewModel(): CodeBlock
    override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec>
    fun getValidateResultName(): String = name.asVariableName() + "ValidateResult"
}

@Serializable
sealed interface State<T> : ReadableState, WriteableState {
    override val id: StateId
    override var name: String
    override val isList: Boolean
    override fun generateVariableInitializationBlock(
        project: Project, context: GenerationContext, dryRun: Boolean,
    ): CodeBlock

    override fun generateReadBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock

    override val userWritable: Boolean
    override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType
    override fun getReadVariableName(project: Project): String = name
    override fun generateWriteBlock(
        project: Project,
        canvasEditable: CanvasEditable,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock

    override fun getValidateResultName(): String = name + "ValidateResult"
    override fun getFlowName(): String = name + "Flow"
    override fun getUpdateMethodName(): String = "on${name.capitalize(Locale.current)}Updated"
    override fun generateUpdateStateMethodToViewModel(): FunSpec
    override fun generateUpdateStateCodeToViewModel(
        project: Project,
        context: GenerationContext,
        readProperty: AssignableProperty,
        dryRun: Boolean,
    ): CodeBlock

    override fun generateClearStateCodeToViewModel(): CodeBlock
    override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec>

    val defaultValue: T?
}

sealed interface BooleanState : State<Boolean> {
    fun generateToggleStateCodeToViewModel(): CodeBlock
}

sealed interface ListAppState {

    fun generateStateProperty(
        propertyName: String,
        stateName: String,
        typeClassName: ClassName,
    ): PropertySpec {
        return PropertySpec.builder(
            propertyName,
            ClassHolder.Coroutines.Flow.StateFlow.parameterizedBy(
                ClassHolder.Collections.List.parameterizedBy(typeClassName),
            ),
        )
            .initializer(
                """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$stateName", 
                         defaultValue = "[]",
                       ).%M {
                            ${ViewModelConstant.jsonSerializer.name}.decodeFromString<%T<%T>>(it)
                       }
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = emptyList(), 
                       )
                    """,
                MemberHolder.Settings.getStringFlow,
                MemberHolder.Coroutines.Flow.map,
                ClassHolder.Collections.List,
                typeClassName,
                MemberHolder.Coroutines.Flow.stateIn,
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.Flow.SharingStarted,
            )
            .build()
    }

    fun generateClearStateCode(stateName: String): CodeBlock {
        return CodeBlock.of(
            """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putString("$stateName", "[]")
                }""",
            MemberHolder.PreCompose.viewModelScope,
            MemberHolder.Coroutines.launch,
        )
    }

    fun generateAddValueToStateCode(
        project: Project,
        context: GenerationContext,
        readProperty: AssignableProperty,
        dryRun: Boolean,
    ): CodeBlock

    fun generateRemoveFirstValueCode(
        project: Project,
        context: GenerationContext,
        writeState: WriteableState,
    ): CodeBlock {
        return CodeBlock.of(
            """%M.%M {
                val list = ${writeState.getFlowName()}.value.toMutableList()
                if (list.isNotEmpty()) {
                    list.removeFirst()
                }
                ${ViewModelConstant.flowSettings.name}.putString("${writeState.name}", ${ViewModelConstant.jsonSerializer.name}.%M(list))
            }""",
            MemberHolder.PreCompose.viewModelScope,
            MemberHolder.Coroutines.launch,
            MemberHolder.Serialization.encodeToString,
        )
    }

    fun generateRemoveLastValueCode(
        project: Project,
        context: GenerationContext,
        writeState: WriteableState,
    ): CodeBlock {
        return CodeBlock.of(
            """%M.%M {
                val list = ${writeState.getFlowName()}.value.toMutableList()
                if (list.isNotEmpty()) {
                    list.removeLast()
                }
                ${ViewModelConstant.flowSettings.name}.putString("${writeState.name}", ${ViewModelConstant.jsonSerializer.name}.%M(list))
            }""",
            MemberHolder.PreCompose.viewModelScope,
            MemberHolder.Coroutines.launch,
            MemberHolder.Serialization.encodeToString,
        )
    }

    fun generateRemoveValueAtIndexFun(
        project: Project,
        context: GenerationContext,
        functionName: String,
        writeState: WriteableState,
    ): FunSpec {
        val indexToRemove = "indexToRemove"
        return FunSpec.builder(functionName)
            .addParameter(
                ParameterSpec.builder(name = indexToRemove, Int::class).build(),
            )
            .addCode(
                """
                val list = ${writeState.getFlowName()}.value.toMutableList()
                if ($indexToRemove < 0 || $indexToRemove >= list.size) return     
                %M.%M {
                    if (list.isNotEmpty()) {
                        list.removeAt($indexToRemove)
                    }
                    ${ViewModelConstant.flowSettings.name}.putString("${writeState.name}", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
                MemberHolder.Serialization.encodeToString,
            )
            .build()
    }

    fun generateUpdateValueAtIndexFunBuilder(
        project: Project,
        context: GenerationContext,
        functionName: String,
        readCodeBlock: CodeBlock,
        writeState: WriteableState,
    ): FunSpec.Builder {
        val indexToUpdate = "indexToUpdate"
        return FunSpec.builder(functionName)
            .addParameter(
                ParameterSpec.builder(name = indexToUpdate, Int::class).build(),
            )
            .addCode(
                """
                val list = ${writeState.getFlowName()}.value.toMutableList()
                if ($indexToUpdate < 0 || $indexToUpdate >= list.size) return     
                %M.%M {
                    list.set($indexToUpdate, $readCodeBlock)
                    ${ViewModelConstant.flowSettings.name}.putString("${writeState.name}", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
                MemberHolder.Serialization.encodeToString,
            )
    }
}

@Serializable
sealed interface AppStateWithDataTypeId {

    val dataTypeId: DataTypeId
}

@Serializable
sealed interface ScreenState<T> : State<T> {

    /**
     * Get the Id of the companion node where the states
     */
    val companionNodeId: String?

    override fun generateVariableInitializationBlock(
        project: Project, context: GenerationContext, dryRun: Boolean,
    ): CodeBlock {
        val builder = CodeBlock.builder()
        builder.addStatement(
            "val ${getReadVariableName(project)} by ${context.currentEditable.viewModelName}.${getFlowName()}.%M()",
            MemberHolder.AndroidX.Runtime.collectAsState,
        )
        return builder.build()
    }

    fun generateValidatorInitializationBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock? {
        val companionNode = companionNodeId?.let {
            project.findComposeNodeOrNull(it)
        } ?: return null

        val result = companionNode.trait.value.getStateValidator()?.let { stateValidator ->
            CodeBlock.of(
                """val ${getValidateResultName()} by %M(${getReadVariableName(project)}) { %M(
                    ${stateValidator.asCodeBlock(project, context)}.validate(${
                    getReadVariableName(
                        project
                    )
                })
                ) }""",
                MemberHolder.AndroidX.Runtime.remember,
                MemberHolder.AndroidX.Runtime.mutableStateOf,
            )
        }
        return result
    }

    @Serializable
    @SerialName("StringScreenState")
    data class StringScreenState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: String = "",
        override val userWritable: Boolean = true,
        override val companionNodeId: String? = null,
    ) : ScreenState<String> {

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            val flowName = getFlowName()
            return FunSpec.builder(getUpdateMethodName())
                .addParameter("newValue", String::class)
                .addStatement("_$flowName.value = newValue")
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.StringType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """
                    _${getFlowName()}.value = $expression 
                """,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = "$defaultValue"
                """,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val backingProperty = PropertySpec.builder(
                "_${getFlowName()}",
                MutableStateFlow::class.parameterizedBy(String::class),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer("""MutableStateFlow("$defaultValue")""")
                .build()
            val property = PropertySpec.builder(
                getFlowName(),
                StateFlow::class.parameterizedBy(String::class),
            )
                .initializer("""_${getFlowName()}""")
                .build()
            return listOf(
                backingProperty,
                property
            )
        }

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(
                "${canvasEditable.viewModelName}.${getUpdateMethodName()}(it)",
            )
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean) =
            ComposeFlowType.StringType(isList = false)

        override val isList: Boolean = false
    }

    @Serializable
    @SerialName("FloatScreenState")
    data class FloatScreenState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: Float = 0f,
        override val userWritable: Boolean = true,
        override val companionNodeId: String? = null,
    ) : ScreenState<Float> {

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            val flowName = getFlowName()
            return FunSpec.builder(getUpdateMethodName())
                .addParameter("newValue", Float::class)
                .addStatement("_$flowName.value = newValue")
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.FloatType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """
                    _${getFlowName()}.value = $expression 
                """,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = "${defaultValue}f"
                """,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val backingProperty = PropertySpec.builder(
                "_${getFlowName()}",
                MutableStateFlow::class.parameterizedBy(Float::class),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer("""MutableStateFlow(${defaultValue}f)""")
                .build()
            val property = PropertySpec.builder(
                getFlowName(),
                StateFlow::class.parameterizedBy(Float::class),
            )
                .initializer("""_${getFlowName()}""")
                .build()
            return listOf(
                backingProperty,
                property
            )
        }

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(
                "${canvasEditable.viewModelName}.${getUpdateMethodName()}(it)",
            )
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean) =
            ComposeFlowType.FloatType(isList = false)

        override val isList: Boolean = false
    }

    @Serializable
    @SerialName("BooleanScreenState")
    data class BooleanScreenState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: Boolean = false,
        override val userWritable: Boolean = true,
        override val companionNodeId: String? = null,
    ) : ScreenState<Boolean>, BooleanState {

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            val flowName = getFlowName()
            return FunSpec.builder(getUpdateMethodName())
                .addParameter("newValue", Boolean::class)
                .addStatement("_$flowName.value = newValue")
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.BooleanType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """
                    _${getFlowName()}.value = $expression 
                """,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = $defaultValue
                """,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val backingProperty = PropertySpec.builder(
                "_${getFlowName()}",
                MutableStateFlow::class.parameterizedBy(Boolean::class),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer("""MutableStateFlow($defaultValue)""")
                .build()
            val property = PropertySpec.builder(
                getFlowName(),
                StateFlow::class.parameterizedBy(Boolean::class),
            )
                .initializer("""_${getFlowName()}""")
                .build()
            return listOf(
                backingProperty,
                property
            )
        }

        override fun generateToggleStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = !${getFlowName()}.value
                """,
            )
        }

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(
                "${canvasEditable.viewModelName}.${getUpdateMethodName()}(it)",
            )
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.BooleanType(isList = false)

        override val isList: Boolean = false
    }

    @Serializable
    @SerialName("InstantScreenState")
    data class InstantScreenState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        @Serializable(FallbackInstantSerializer::class)
        override val defaultValue: Instant = Clock.System.now(),
        override val userWritable: Boolean = true,
        override val companionNodeId: String? = null,
    ) : ScreenState<Instant> {

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            val flowName = getFlowName()
            return FunSpec.builder(getUpdateMethodName())
                .addParameter("newValue", Instant::class)
                .addStatement("_$flowName.value = newValue")
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.InstantType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """
                    _${getFlowName()}.value = $expression 
                """,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = $defaultValue
                """,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val backingProperty = PropertySpec.builder(
                "_${getFlowName()}",
                ClassHolder.Coroutines.Flow.MutableStateFlow.parameterizedBy(Instant::class.asTypeName()),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer(
                    "%T(%T.System.now())",
                    ClassHolder.Coroutines.Flow.MutableStateFlow,
                    Clock::class.asTypeName()
                )
                .build()
            val property = PropertySpec.builder(
                getFlowName(),
                ClassHolder.Coroutines.Flow.StateFlow.parameterizedBy(Instant::class.asTypeName()),
            )
                .initializer("""_${getFlowName()}""")
                .build()
            return listOf(
                backingProperty,
                property
            )
        }

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.InstantType(isList = false)

        override val isList: Boolean = false
    }

    @Serializable
    @SerialName("StringListScreenState")
    data class StringListScreenState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<String> = listOf(""),
        override val isList: Boolean = true,
        // If set to true, this list only contains a single element.
        // For example, selectedItems in a ChipGroup, this can be set to true when multiselect
        // property is set to false
        val singleValueOnly: Boolean = false,
        override val userWritable: Boolean = true,
        override val companionNodeId: String? = null,
    ) : ScreenState<List<String>> {

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            val flowName = getFlowName()
            return FunSpec.builder(getUpdateMethodName())
                .addParameter("newValue", List::class.parameterizedBy(String::class))
                .addStatement("_$flowName.value = newValue")
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.StringType(isList = true).convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """
                    _${getFlowName()}.value = $expression 
                """,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """
                    _${getFlowName()}.value = emptyList()
                """,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val backingProperty = PropertySpec.builder(
                "_${getFlowName()}",
                ClassHolder.Coroutines.Flow.MutableStateFlow.parameterizedBy(
                    List::class.parameterizedBy(
                        String::class
                    )
                ),
            )
                .addModifiers(KModifier.PRIVATE)
                .initializer("""MutableStateFlow(List(0) { "" })""")
                .build()
            val property = PropertySpec.builder(
                getFlowName(),
                ClassHolder.Coroutines.Flow.StateFlow.parameterizedBy(
                    List::class.parameterizedBy(
                        String::class
                    )
                ),
            )
                .initializer("""_${getFlowName()}""")
                .build()
            return listOf(
                backingProperty,
                property
            )
        }

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            val composeContext = context.getCurrentComposableContext()
            val newItems = composeContext.addComposeFileVariable("newItems", dryRun = dryRun)
            if (singleValueOnly) {
                builder.add(
                    CodeBlock.of(
                        """
                    val $newItems = ${getReadVariableName(project)}.%M() 
                    if (${newItems}.%M()) {
                        ${newItems}.clear()
                        ${newItems}.add(it)
                    } else {
                        ${newItems}.add(it)
                    }
                    ${canvasEditable.viewModelName}.${getUpdateMethodName()}($newItems)
                """,
                        MemberHolder.Kotlin.Collection.toMutableList,
                        MemberHolder.Kotlin.Collection.isNotEmpty
                    ),
                )
            } else {
                builder.add(
                    CodeBlock.of(
                        """
                    val $newItems = ${getReadVariableName(project)}.%M() 
                    if ($newItems.contains(it)) {
                        ${newItems}.remove(it)
                    } else {
                        ${newItems}.add(it)
                    }
                    ${canvasEditable.viewModelName}.${getUpdateMethodName()}($newItems)
                """, MemberHolder.Kotlin.Collection.toMutableList
                    ),
                )
            }
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType(isList = true)
    }

    /**
     * Checks equality of the other state.
     *
     * The reason to define this method instead of the built-in equals is that when the state
     * is deep copied, id and name needs to be different from the original instance to make sure
     * the uniqueness of the values within the context.
     */
    fun contentEquals(
        other: ScreenState<*>,
        excludeId: Boolean = true,
        excludeName: Boolean = true,
    ): Boolean {
        val idEquality = if (excludeId) {
            true
        } else {
            id == other.id
        }
        val nameEquality = if (excludeName) {
            true
        } else {
            name == other.name
        }
        return idEquality && nameEquality
    }
}

@Serializable
sealed interface AppState<T> : State<T> {

    @Serializable
    data class StringAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: String = "",
        override val isList: Boolean = false,
        override val userWritable: Boolean = true,
    ) : AppState<String> {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(
                "${canvasEditable.viewModelName}.${getUpdateMethodName()}(it)",
            )
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType(isList = false)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName())
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.StringType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        ${ViewModelConstant.flowSettings.name}.putString("$name", $expression)
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putString("$name", "$defaultValue")
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                PropertySpec.builder(
                    getFlowName(),
                    StateFlow::class.parameterizedBy(String::class),
                )
                    .initializer(
                        """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = "$defaultValue"
                       )
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = "$defaultValue"
                       )
                    """,
                        MemberHolder.Settings.getStringFlow,
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                    )
                    .build()
            )
        }
    }

    @Serializable
    @SerialName("IntAppState")
    data class IntAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: Int = 0,
        override val isList: Boolean = false,
        override val userWritable: Boolean = true,
    ) : AppState<Int> {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.IntType(isList = false)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.IntType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        ${ViewModelConstant.flowSettings.name}.putInt("$name", $expression)
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putInt("$name", $defaultValue)
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                PropertySpec.builder(getFlowName(), StateFlow::class.parameterizedBy(Int::class))
                    .initializer(
                        """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = $defaultValue,
                       )
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = $defaultValue,
                       )
                    """,
                        MemberHolder.Settings.getIntFlow,
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                    )
                    .build()
            )
        }
    }

    @Serializable
    @SerialName("FloatAppState")
    data class FloatAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: Float = 0f,
        override val isList: Boolean = false,
        override val userWritable: Boolean = true,
    ) : AppState<Float> {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.FloatType(isList = false)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.FloatType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        ${ViewModelConstant.flowSettings.name}.putFloat("$name", $expression)
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putFloat("$name", ${defaultValue}f)
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                PropertySpec.builder(getFlowName(), StateFlow::class.parameterizedBy(Float::class))
                    .initializer(
                        """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = ${defaultValue}f,
                       )
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = ${defaultValue}f,
                       )
                    """,
                        MemberHolder.Settings.getFloatFlow,
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                    )
                    .build()
            )
        }
    }

    @Serializable
    @SerialName("BooleanAppState")
    data class BooleanAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: Boolean = false,
        override val isList: Boolean = false,
        override val userWritable: Boolean = true,
    ) : AppState<Boolean>, BooleanState {
        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.BooleanType(isList = false)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.BooleanType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        ${ViewModelConstant.flowSettings.name}.putBoolean("$name", $expression)
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putBoolean("$name", $defaultValue)
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateToggleStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putBoolean("$name", !${getFlowName()}.value)
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                PropertySpec.builder(
                    getFlowName(),
                    StateFlow::class.parameterizedBy(Boolean::class),
                )
                    .initializer(
                        """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = $defaultValue,
                       )
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = $defaultValue,
                       )
                    """,
                        MemberHolder.Settings.getBooleanFlow,
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                    )
                    .build()
            )
        }
    }

    @Serializable
    @SerialName("InstantAppState")
    data class InstantAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: InstantWrapper = InstantWrapper(null),
        override val isList: Boolean = false,
        override val userWritable: Boolean = true,
    ) : AppState<InstantWrapper> {
        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.InstantType(isList = false)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val expression = ComposeFlowType.InstantType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        ${ViewModelConstant.flowSettings.name}.putLong("$name", ${expression}.toEpochMilliseconds())
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                    )
                }
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putLong("$name", ${defaultValue.generateCode()}.toEpochMilliseconds())
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                PropertySpec.builder(
                    getFlowName(),
                    StateFlow::class.parameterizedBy(Instant::class),
                )
                    .initializer(
                        CodeBlock.of(
                            """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = ${defaultValue.generateCode()}.toEpochMilliseconds(),
                       )
                       .%M { %M.fromEpochMilliseconds(it) }
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = ${defaultValue.generateCode()},
                       )
                    """,
                            MemberHolder.Settings.getLongFlow,
                            MemberHolder.Coroutines.Flow.map,
                            MemberHolder.DateTime.Instant,
                            MemberHolder.Coroutines.Flow.stateIn,
                            MemberHolder.PreCompose.viewModelScope,
                            MemberHolder.Coroutines.Flow.SharingStarted,
                        )
                    )
                    .build()
            )
        }
    }

    @Serializable
    @SerialName("StringListAppState")
    data class StringListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<String> = listOf(""),
        override val isList: Boolean = true,
        override val userWritable: Boolean = true,
    ) : AppState<List<String>>, ListAppState {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType(
                isList = !asNonList,
            )

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = ClassHolder.Kotlin.String,
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val state = project.findLocalStateOrNull(id) ?: return builder.build()
                    val writeState = state as? WriteableState ?: return builder.build()
                    val expression = ComposeFlowType.StringType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        val list = ${writeState.getFlowName()}.value.toMutableList().apply {
                            add($expression)
                        }
                        ${ViewModelConstant.flowSettings.name}.putString("$name", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                        MemberHolder.Serialization.encodeToString,
                    )
                }
            return builder.build()
        }
    }

    @Serializable
    @SerialName("IntListAppState")
    data class IntListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<Int> = listOf(0),
        override val isList: Boolean = true,
        override val userWritable: Boolean = true,
    ) : AppState<List<Int>>, ListAppState {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.IntType(isList = !asNonList)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = ClassHolder.Kotlin.Int,
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val writeState = project.findLocalStateOrNull(id)
                    if (writeState != null && writeState is WriteableState) {
                        val expression = ComposeFlowType.IntType().convertCodeFromType(
                            inputType = readProperty.valueType(project),
                            codeBlock = readExpression,
                        )
                        builder.addStatement(
                            """%M.%M {
                        val list = ${writeState.getFlowName()}.value.toMutableList().apply {
                            add($expression)
                        }
                        ${ViewModelConstant.flowSettings.name}.putString("$name", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                    }""",
                            MemberHolder.PreCompose.viewModelScope,
                            MemberHolder.Coroutines.launch,
                            MemberHolder.Serialization.encodeToString,
                        )
                    }
                }
            return builder.build()
        }
    }

    @Serializable
    @SerialName("FloatListAppState")
    data class FloatListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<Float> = listOf(0f),
        override val isList: Boolean = true,
        override val userWritable: Boolean = true,
    ) : AppState<List<Float>>, ListAppState {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.FloatType(isList = !asNonList)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = ClassHolder.Kotlin.Float,
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val writeState = project.findLocalStateOrNull(id)
                    if (writeState != null && writeState is WriteableState) {
                        val expression = ComposeFlowType.FloatType().convertCodeFromType(
                            inputType = readProperty.valueType(project),
                            codeBlock = readExpression,
                        )
                        builder.addStatement(
                            """%M.%M {
                        val list = ${writeState.getFlowName()}.value.toMutableList().apply {
                            add($expression)
                        }
                        ${ViewModelConstant.flowSettings.name}.putString("$name", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                    }""",
                            MemberHolder.PreCompose.viewModelScope,
                            MemberHolder.Coroutines.launch,
                            MemberHolder.Serialization.encodeToString,
                        )
                    }
                }
            return builder.build()
        }
    }

    @Serializable
    @SerialName("BooleanListAppState")
    data class BooleanListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<Boolean> = listOf(false),
        override val isList: Boolean = true,
        override val userWritable: Boolean = true,
    ) : AppState<List<Boolean>>, ListAppState {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.BooleanType(isList = !asNonList)

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = ClassHolder.Kotlin.Boolean,
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val writeState = project.findLocalStateOrNull(id)
                    if (writeState != null && writeState is WriteableState) {
                        val expression = ComposeFlowType.BooleanType().convertCodeFromType(
                            inputType = readProperty.valueType(project),
                            codeBlock = readExpression,
                        )
                        builder.addStatement(
                            """%M.%M {
                        val list = ${writeState.getFlowName()}.value.toMutableList().apply {
                            add($expression)
                        }
                        ${ViewModelConstant.flowSettings.name}.putString("$name", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                    }""",
                            MemberHolder.PreCompose.viewModelScope,
                            MemberHolder.Coroutines.launch,
                            MemberHolder.Serialization.encodeToString,
                        )
                    }
                }
            return builder.build()
        }
    }

    @Serializable
    @SerialName("InstantListAppState")
    data class InstantListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<InstantWrapper> = listOf(InstantWrapper(null)),
        override val isList: Boolean = true,
        override val userWritable: Boolean = true,
    ) : AppState<List<InstantWrapper>>, ListAppState {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType(
                isList = !asNonList,
            )

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = ClassHolder.Kotlin.Long,
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            readProperty.transformedCodeBlock(project, context, dryRun = dryRun)
                .let { readExpression ->
                    val state = project.findLocalStateOrNull(id) ?: return builder.build()
                    val writeState = state as? WriteableState ?: return builder.build()
                    val expression = ComposeFlowType.StringType().convertCodeFromType(
                        inputType = readProperty.valueType(project),
                        codeBlock = readExpression,
                    )
                    builder.addStatement(
                        """%M.%M {
                        val list = ${writeState.getFlowName()}.value.toMutableList().apply {
                            add($expression)
                        }
                        ${ViewModelConstant.flowSettings.name}.putString("$name", ${ViewModelConstant.jsonSerializer.name}.%M(list))
                    }""",
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.launch,
                        MemberHolder.Serialization.encodeToString,
                    )
                }
            return builder.build()
        }
    }

    @Serializable
    @SerialName("CustomDataTypeListAppState")
    data class CustomDataTypeListAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: List<DataTypeDefaultValue> = emptyList(),
        override val isList: Boolean = true,
        override val dataTypeId: DataTypeId,
        override val userWritable: Boolean = true,
    ) : AppState<List<DataTypeDefaultValue>>, ListAppState, AppStateWithDataTypeId {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.CustomDataType(
                isList = !asNonList,
                dataTypeId = dataTypeId,
            )

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName()).build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            return CodeBlock.of("")
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return super.generateClearStateCode(stateName = name)
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val dataType = project.findDataTypeOrThrow(dataTypeId)
            return listOf(
                super.generateStateProperty(
                    propertyName = getFlowName(),
                    stateName = name,
                    typeClassName = dataType.asKotlinPoetClassName(project),
                )
            )
        }

        override fun generateAddValueToStateCode(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            return builder.build()
        }
    }

    @Serializable
    @SerialName("CustomDataTypeAppState")
    data class CustomDataTypeAppState(
        override val id: StateId = Uuid.random().toString(),
        override var name: String,
        override val defaultValue: DataType? = null,
        override val isList: Boolean = false,
        override val dataTypeId: DataTypeId = defaultValue?.id ?: EmptyDataType.id,
        override val userWritable: Boolean = true,
    ) : AppState<DataType>, AppStateWithDataTypeId {

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(getReadVariableName(project))
            return builder.build()
        }

        override fun generateWriteBlock(
            project: Project,
            canvasEditable: CanvasEditable,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            builder.addStatement(
                "${canvasEditable.viewModelName}.${getUpdateMethodName()}(it)",
            )
            return builder.build()
        }

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.CustomDataType(
                isList = false,
                dataTypeId = dataTypeId,
            )

        override fun generateUpdateStateMethodToViewModel(): FunSpec {
            return FunSpec.builder(getUpdateMethodName())
                .build()
        }

        override fun generateUpdateStateCodeToViewModel(
            project: Project,
            context: GenerationContext,
            readProperty: AssignableProperty,
            dryRun: Boolean,
        ): CodeBlock {
            val builder = CodeBlock.builder()
            return builder.build()
        }

        override fun generateClearStateCodeToViewModel(): CodeBlock {
            return CodeBlock.of(
                """%M.%M {
                    ${ViewModelConstant.flowSettings.name}.putString("$name", "{}")
                }""",
                MemberHolder.PreCompose.viewModelScope,
                MemberHolder.Coroutines.launch,
            )
        }

        override fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
            val dataType = project.findDataTypeOrThrow(dataTypeId)
            return listOf(
                PropertySpec.builder(
                    getFlowName(),
                    ClassHolder.Coroutines.Flow.StateFlow.parameterizedBy(
                        dataType.asKotlinPoetClassName(
                            project
                        )
                    ),
                )
                    .initializer(
                        """
                       ${ViewModelConstant.flowSettings.name}.%M(
                         "$name", 
                         defaultValue = "{}"
                       ).%M {
                            ${ViewModelConstant.jsonSerializer.name}.decodeFromString<%T>(it)
                       }
                       .%M(
                         scope = %M,
                         started = %M.WhileSubscribed(5_000),
                         initialValue = %T(), 
                       )
                    """,
                        MemberHolder.Settings.getStringFlow,
                        MemberHolder.Coroutines.Flow.map,
                        dataType.asKotlinPoetClassName(project),
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                        dataType.asKotlinPoetClassName(project),
                    )
                    .build()
            )
        }
    }

    override fun generateVariableInitializationBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        return CodeBlock.of(
            "val ${getReadVariableName(project)} by ${context.currentEditable.viewModelName}.${getFlowName()}.%M()",
            MemberHolder.AndroidX.Runtime.collectAsState,
        )
    }

    companion object {
        fun entries(): List<AppState<*>> = listOf(
            StringAppState(name = ""),
            IntAppState(name = ""),
            FloatAppState(name = ""),
            BooleanAppState(name = ""),
            InstantAppState(name = ""),
            CustomDataTypeAppState(name = ""),
        )

        fun indexOf(appState: AppState<*>): Int {
            return when (appState) {
                is StringAppState -> 0
                is IntAppState -> 1
                is FloatAppState -> 2
                is BooleanAppState -> 3
                is InstantAppState -> 4
                is CustomDataTypeAppState -> 5
                else -> throw IllegalArgumentException("Invalid appState $appState")
            }
        }

        fun fromOrdinal(ordinal: Int): AppState<*> {
            return when (ordinal) {
                0 -> StringAppState(name = "")
                1 -> IntAppState(name = "")
                2 -> FloatAppState(name = "")
                3 -> BooleanAppState(name = "")
                4 -> InstantAppState(name = "")
                5 -> CustomDataTypeAppState(name = "")
                else -> throw IllegalArgumentException("Invalid ordinal : $ordinal")
            }
        }
    }
}

fun <T> AppState<T>.copy(
    id: String? = null,
    name: String,
    isList: Boolean = this.isList,
    argDataTypeId: DataTypeId? = null,
): AppState<*> {
    val newId = id ?: this.id

    fun copyStringState(
        newId: StateId,
        name: String,
        defaultValue: String,
        isList: Boolean,
    ): AppState<*> {
        return if (isList) {
            AppState.StringListAppState(
                id = newId,
                name = name,
                defaultValue = listOf(defaultValue),
            )
        } else {
            AppState.StringAppState(
                id = newId,
                name = name,
                defaultValue = defaultValue,
            )
        }
    }

    fun copyIntState(
        newId: StateId,
        name: String,
        defaultValue: Int,
        isList: Boolean,
    ): AppState<*> {
        return if (isList) {
            AppState.IntListAppState(
                id = newId,
                name = name,
                defaultValue = listOf(defaultValue),
            )
        } else {
            AppState.IntAppState(
                id = newId,
                name = name,
                defaultValue = defaultValue,
            )
        }
    }

    fun copyFloatState(
        newId: StateId,
        name: String,
        defaultValue: Float,
        isList: Boolean,
    ): AppState<*> {
        return if (isList) {
            AppState.FloatListAppState(
                id = newId,
                name = name,
                defaultValue = listOf(defaultValue),
            )
        } else {
            AppState.FloatAppState(
                id = newId,
                name = name,
                defaultValue = defaultValue,
            )
        }
    }

    fun copyBooleanState(
        newId: StateId,
        name: String,
        defaultValue: Boolean,
        isList: Boolean,
    ): AppState<*> {
        return if (isList) {
            AppState.BooleanListAppState(
                id = newId,
                name = name,
                defaultValue = listOf(defaultValue),
            )
        } else {
            AppState.BooleanAppState(
                id = newId,
                name = name,
                defaultValue = defaultValue,
            )
        }
    }

    fun copyInstantState(
        newId: StateId,
        name: String,
        defaultValue: InstantWrapper,
        isList: Boolean,
    ): AppState<*> {
        return if (isList) {
            AppState.InstantListAppState(
                id = newId,
                name = name,
                defaultValue = listOf(defaultValue),
            )
        } else {
            AppState.InstantAppState(
                id = newId,
                name = name,
                defaultValue = defaultValue,
            )
        }
    }

    fun copyCustomDataTypeState(
        newId: StateId,
        name: String,
        isList: Boolean,
        dataTypeId: DataTypeId,
    ): AppState<*> {
        return if (isList) {
            AppState.CustomDataTypeListAppState(
                id = newId,
                name = name,
                dataTypeId = dataTypeId,
            )
        } else {
            AppState.CustomDataTypeAppState(
                id = newId,
                name = name,
                dataTypeId = dataTypeId,
            )
        }
    }

    return when (this) {
        is AppState.BooleanAppState -> {
            copyBooleanState(
                newId = newId,
                name = name,
                defaultValue = defaultValue,
                isList = isList,
            )
        }

        is AppState.BooleanListAppState -> {
            copyBooleanState(
                newId = newId,
                name = name,
                defaultValue = if (defaultValue.isNotEmpty()) defaultValue[0] else true,
                isList = isList,
            )
        }

        is AppState.IntAppState -> {
            copyIntState(
                newId = newId,
                name = name,
                defaultValue = defaultValue,
                isList = isList,
            )
        }

        is AppState.IntListAppState -> {
            copyIntState(
                newId = newId,
                name = name,
                defaultValue = if (defaultValue.isNotEmpty()) defaultValue[0] else 0,
                isList = isList,
            )
        }

        is AppState.FloatAppState -> {
            copyFloatState(
                newId = newId,
                name = name,
                defaultValue = defaultValue,
                isList = isList,
            )
        }

        is AppState.FloatListAppState -> {
            copyFloatState(
                newId = newId,
                name = name,
                defaultValue = if (defaultValue.isNotEmpty()) defaultValue[0] else 0.0f,
                isList = isList,
            )
        }

        is AppState.StringAppState -> {
            copyStringState(
                newId = newId,
                name = name,
                defaultValue = defaultValue,
                isList = isList,
            )
        }

        is AppState.StringListAppState -> {
            copyStringState(
                newId = newId,
                name = name,
                defaultValue = if (defaultValue.isNotEmpty()) defaultValue[0] else "",
                isList = isList,
            )
        }

        is AppState.CustomDataTypeAppState -> {
            copyCustomDataTypeState(
                newId = newId,
                name = name,
                isList = isList,
                dataTypeId = argDataTypeId ?: this.dataTypeId,
            )
        }

        is AppState.CustomDataTypeListAppState -> {
            copyCustomDataTypeState(
                newId = newId,
                name = name,
                isList = isList,
                dataTypeId = argDataTypeId ?: this.dataTypeId,
            )
        }

        is AppState.InstantAppState -> {
            copyInstantState(
                newId = newId,
                name = name,
                defaultValue = defaultValue,
                isList = isList,
            )
        }

        is AppState.InstantListAppState -> {
            copyInstantState(
                newId = newId,
                name = name,
                defaultValue = if (defaultValue.isNotEmpty()) defaultValue[0] else InstantWrapper(),
                isList = isList,
            )
        }
    }
}

private const val authenticatedUser = "authenticatedUser"

@Serializable
sealed interface AuthenticatedUserState : ReadableState {

    override fun generateVariableInitializationBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        context.getCurrentComposableContext().addCompositionLocalVariableEntryIfNotPresent(
            "authenticatedUser", MemberHolder.ComposeFlow.LocalAuthenticatedUser
        )
        // Initialize the authenticatedUser once in the compose file instead of initializing it in
        // every state that has a reference
        return CodeBlock.of("")
    }

    @Serializable
    @SerialName("IsSignedIn")
    data object IsSignedIn : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Is signed in"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""$authenticatedUser != null""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.BooleanType()
    }

    @Serializable
    @SerialName("DisplayName")
    data object DisplayName : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Display name"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""(${authenticatedUser}?.displayName ?: "Invalid display name")""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType()
    }

    @Serializable
    @SerialName("Email")
    data object Email : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Email"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""(${authenticatedUser}?.email ?: "Invalid email")""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType()
    }

    @Serializable
    @SerialName("PhoneNumber")
    data object PhoneNumber : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Phone number"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""(${authenticatedUser}?.phoneNumber ?: "Invalid phone number")""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType()
    }

    @Serializable
    @SerialName("PhotoUrl")
    data object PhotoUrl : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Photo URL"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""(${authenticatedUser}?.photoURL ?: "Invalid photo url")""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.StringType()
    }

    @Serializable
    @SerialName("IsAnonymous")
    data object IsAnonymous : AuthenticatedUserState {
        override val id: StateId = Uuid.random().toString()
        override var name: String = "Is anonymous"
        override val isList: Boolean = false

        override fun generateReadBlock(
            project: Project,
            context: GenerationContext,
            dryRun: Boolean,
        ): CodeBlock =
            CodeBlock.of("""(${authenticatedUser}?.isAnonymous ?: false)""")

        override fun valueType(project: Project, asNonList: Boolean): ComposeFlowType =
            ComposeFlowType.BooleanType()
    }

    companion object {
        fun entries(): List<AuthenticatedUserState> = listOf(
            IsSignedIn,
            DisplayName,
            Email,
            PhoneNumber,
            PhotoUrl,
            IsAnonymous,
        )
    }
}