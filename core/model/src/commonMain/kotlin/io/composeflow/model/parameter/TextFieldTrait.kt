package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.Res
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.materialicons.ImageVectorHolder
import io.composeflow.model.action.ActionType
import io.composeflow.model.enumwrapper.TextFieldColorsWrapper
import io.composeflow.model.modifier.generateModifierCode
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.parameter.wrapper.ShapeWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.findCanvasEditableHavingNodeOrNull
import io.composeflow.model.project.findLocalStateOrNull
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.property.StringProperty
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.property.asBooleanValue
import io.composeflow.model.state.ScreenState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.WriteableState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.model.validator.ComposeStateValidator
import io.composeflow.model.validator.TextFieldValidator
import io.composeflow.serializer.FallbackEnumSerializer
import io.composeflow.tooltip_textfield_trait
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
@SerialName("TextFieldTrait")
data class TextFieldTrait(
    val value: AssignableProperty = StringProperty.StringIntrinsicValue(),
    val enabled: AssignableProperty? = null,
    val readOnly: AssignableProperty? = null,
    val label: AssignableProperty? = null,
    val placeholder: AssignableProperty? = null,
    val leadingIcon: ImageVectorHolder? = null,
    val trailingIcon: ImageVectorHolder? = null,
    val singleLine: Boolean? = true,
    val maxLines: Int? = null,
    val shapeWrapper: ShapeWrapper? = null,
    val textFieldType: TextFieldType = TextFieldType.Default,
    val transparentIndicator: Boolean? = null,
    val enableValidator: Boolean? = false,
    val textFieldValidator: TextFieldValidator? = TextFieldValidator.StringValidator(),
    val passwordField: Boolean? = null,
) : ComposeTrait {

    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Value", value, ComposeFlowType.StringType()),
            PropertyContainer("Enabled", enabled, ComposeFlowType.BooleanType()),
            PropertyContainer("Read only", readOnly, ComposeFlowType.BooleanType()),
            PropertyContainer("Label", label, ComposeFlowType.StringType()),
            PropertyContainer("Placeholder", placeholder, ComposeFlowType.StringType()),
        )
    }

    private fun generateParamsCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.add("value = ")
        codeBlockBuilder.add(
            value.transformedCodeBlock(
                project,
                context,
                ComposeFlowType.StringType(),
                dryRun = dryRun
            )
        )
        codeBlockBuilder.addStatement(",")

        val canvasEditable = project.findCanvasEditableHavingNodeOrNull(node)
        val writeState = when (value) {
            is ValueFromState -> {
                canvasEditable?.findStateOrNull(project, value.readFromStateId)
            }

            else -> null
        }
        if (writeState != null &&
            canvasEditable != null &&
            writeState is WriteableState &&
            node.companionStateId == writeState.id
        ) {
            codeBlockBuilder.addStatement("onValueChange = {")
            codeBlockBuilder.add(
                writeState.generateWriteBlock(
                    project,
                    canvasEditable,
                    context,
                    dryRun = dryRun
                )
            )

            if (node.actionsMap[ActionType.OnChange]?.isNotEmpty() == true) {
                if (enableValidator == true) {
                    codeBlockBuilder.add(
                        CodeBlock.of(
                            "if (${writeState.getValidateResultName()}.%M()) {",
                            MemberHolder.ComposeFlow.isSuccess,
                        )
                    )
                }
                node.actionsMap[ActionType.OnChange]?.forEach {
                    codeBlockBuilder.add(it.generateCodeBlock(project, context, dryRun = dryRun))
                }
                if (enableValidator == true) {
                    codeBlockBuilder.add("}") // Close if expression for checking validator result
                }
            }

            codeBlockBuilder.addStatement("},")
        } else {
            codeBlockBuilder.addStatement("onValueChange = {},")
        }
        enabled?.let {
            codeBlockBuilder.add("enabled = ")
            codeBlockBuilder.add(it.transformedCodeBlock(project, context, dryRun = dryRun))
            codeBlockBuilder.addStatement(",")
        }
        readOnly?.let {
            codeBlockBuilder.add("readOnly = ")
            codeBlockBuilder.add(it.transformedCodeBlock(project, context, dryRun = dryRun))
            codeBlockBuilder.addStatement(",")
        }
        val text = MemberName("androidx.compose.material3", "Text")
        val modifier = MemberName("androidx.compose.ui", "Modifier")
        val alpha = MemberName("androidx.compose.ui.draw", "alpha")
        label?.let {
            codeBlockBuilder.addStatement(
                """label = { 
                %M(""",
                text,
            )
            codeBlockBuilder.add(
                it.transformedCodeBlock(
                    project,
                    context,
                    ComposeFlowType.StringType(),
                    dryRun = dryRun
                )
            )
            codeBlockBuilder.addStatement(
                """, 
                  modifier = %M.%M(0.7f)) 
                },""",
                modifier,
                alpha,
            )
        }
        placeholder?.let {
            codeBlockBuilder.addStatement(
                """placeholder = { 
                %M(""",
                text,
            )
            codeBlockBuilder.add(
                it.transformedCodeBlock(
                    project,
                    context,
                    ComposeFlowType.StringType(),
                    dryRun = dryRun
                )
            )
            codeBlockBuilder.addStatement(
                """, 
                  modifier = %M.%M(0.7f)) 
                },""",
                modifier,
                alpha,
            )
        }
        leadingIcon?.let {
            val iconMember = MemberName("androidx.compose.material3", "Icon")
            val iconsMember = MemberName("androidx.compose.material.icons", "Icons")
            val imageVectorMember =
                MemberName("androidx.compose.material.icons.${it.packageDescriptor}", it.name)
            codeBlockBuilder.addStatement(
                """leadingIcon = { %M(imageVector = %M.${it.memberDescriptor}.%M, contentDescription = null) },""",
                iconMember,
                iconsMember,
                imageVectorMember,
            )
        }
        trailingIcon?.let {
            val iconMember = MemberName("androidx.compose.material3", "Icon")
            val iconsMember = MemberName("androidx.compose.material.icons", "Icons")
            val imageVectorMember =
                MemberName("androidx.compose.material.icons.${it.packageDescriptor}", it.name)
            codeBlockBuilder.addStatement(
                """trailingIcon = { %M(imageVector = %M.${it.memberDescriptor}.%M, contentDescription = null) },""",
                iconMember,
                iconsMember,
                imageVectorMember,
            )
        }
        singleLine?.let {
            codeBlockBuilder.addStatement("singleLine = $it,")
        }
        maxLines?.let {
            codeBlockBuilder.addStatement("maxLines = $it,")
        }
        shapeWrapper?.generateCode(codeBlockBuilder)
        if (shouldUseTransparentIndicator()) {
            codeBlockBuilder.add(
                CodeBlock.of(
                    """colors = %M.colors(
                    focusedIndicatorColor = %M.Transparent,
                    unfocusedIndicatorColor = %M.Transparent,
                    disabledIndicatorColor = %M.Transparent, 
                    errorIndicatorColor = %M.Transparent, 
                    ),""",
                    TextFieldColorsWrapper.Default.toMemberName(),
                    MemberHolder.AndroidX.Ui.Color,
                    MemberHolder.AndroidX.Ui.Color,
                    MemberHolder.AndroidX.Ui.Color,
                    MemberHolder.AndroidX.Ui.Color,
                )
            )
        }
        if (node.actionsMap[ActionType.OnSubmit]?.isNotEmpty() == true) {
            codeBlockBuilder.addStatement(
                """keyboardOptions = %T.Default.copy(
                imeAction = %T.Done
            ),""",
                ClassName("androidx.compose.foundation.text", "KeyboardOptions"),
                ClassName("androidx.compose.ui.text.input", "ImeAction")
            )

            codeBlockBuilder.add(
                "keyboardActions = %T(",
                ClassName("androidx.compose.foundation.text", "KeyboardActions")
            )
            codeBlockBuilder.add("onDone = {")

            if (enableValidator == true && writeState != null && writeState is WriteableState) {
                codeBlockBuilder.add(
                    CodeBlock.of(
                        "if (${writeState.getValidateResultName()}.%M()) {",
                        MemberHolder.ComposeFlow.isSuccess,
                    )
                )
            }
            node.actionsMap[ActionType.OnSubmit]?.forEach {
                codeBlockBuilder.add(it.generateCodeBlock(project, context, dryRun = dryRun))
            }
            if (enableValidator == true && writeState != null) {
                codeBlockBuilder.add("}") // Close if statement for checking the validator result
            }

            codeBlockBuilder.add("}")
            codeBlockBuilder.addStatement("),")
        }
        if (enableValidator == true && writeState != null && writeState is WriteableState) {
            codeBlockBuilder.add(
                "isError = ${writeState.getValidateResultName()} is %M.Failure,",
                MemberHolder.ComposeFlow.ValidateResult
            )
            codeBlockBuilder.add(
                CodeBlock.of(
                    """
                supportingText = ${writeState.getValidateResultName()}.%M()?.let {
                    {
                        %M(it)
                    }
                },
            """,
                    MemberHolder.ComposeFlow.asErrorMessage,
                    MemberHolder.Material3.Text,
                )
            )
        }
        passwordField?.let {
            codeBlockBuilder.addStatement(
                """visualTransformation = %T(),
                keyboardOptions = %T(
                        keyboardType = %T.Password,
                        imeAction = %T.Done,
                    ),""",
                ClassName("androidx.compose.ui.text.input", "PasswordVisualTransformation"),
                ClassName("androidx.compose.foundation.text", "KeyboardOptions"),
                ClassName("androidx.compose.ui.text.input", "KeyboardType"),
                ClassName("androidx.compose.ui.text.input", "ImeAction")
            )
        }
        return codeBlockBuilder.build()
    }

    override fun defaultComposeNode(project: Project): ComposeNode {
        return ComposeNode(
            modifierList = defaultModifierList(),
            trait = mutableStateOf(TextFieldTrait(value = StringProperty.StringIntrinsicValue(""))),
        )
    }

    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName = stateHolder.createUniqueName(project = project, initial = "textField")
        val screenState =
            ScreenState.StringScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)
        node.companionStateId = screenState.id
        node.trait.value = this.copy(value = ValueFromState(screenState.id))
        node.label.value = stateName
    }

    override fun icon(): ImageVector = Icons.Outlined.EditNote
    override fun iconText(): String = "TextField"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)
    override fun tooltipResource(): StringResource = Res.string.tooltip_textfield_trait

    override fun actionTypes(): List<ActionType> = listOf(
        ActionType.OnSubmit,
        ActionType.OnChange,
        ActionType.OnFocused,
        ActionType.OnUnfocused,
    )

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        zoomableContainerStateHolder: ZoomableContainerStateHolder,
        modifier: Modifier,
    ) {
        val colors = if (shouldUseTransparentIndicator()) {
            TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            )
        } else {
            when (textFieldType) {
                TextFieldType.Default -> {
                    TextFieldDefaults.colors()
                }

                TextFieldType.Outlined -> {
                    OutlinedTextFieldDefaults.colors()
                }
            }
        }
        when (textFieldType) {
            TextFieldType.Default -> {
                TextField(
                    value = "",
                    onValueChange = {},
                    enabled = enabled.asBooleanValue(),
                    // Always true to not show the caret inside so that it can be deleted with delete key
                    readOnly = true,
                    label = label?.let {
                        {
                            Text(
                                text = it.transformedValueExpression(project),
                                modifier = Modifier.alpha(0.7f),
                            )
                        }
                    },
                    placeholder = placeholder?.let {
                        {
                            Text(
                                it.transformedValueExpression(project),
                                modifier = Modifier.alpha(0.7f),
                            )
                        }
                    },
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = null,
                            )
                        }
                    },
                    trailingIcon = trailingIcon?.let {
                        {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = singleLine ?: false,
                    maxLines = maxLines ?: Integer.MAX_VALUE,
                    shape = shapeWrapper?.toShape() ?: TextFieldDefaults.shape,
                    colors = colors,
                    modifier = modifier.then(
                        node.modifierChainForCanvas()
                            .modifierForCanvas(
                                project = project,
                                node = node,
                                canvasNodeCallbacks = canvasNodeCallbacks,
                                paletteRenderParams = paletteRenderParams,
                                zoomableContainerStateHolder = zoomableContainerStateHolder,
                            ),
                    ),
                )
            }

            TextFieldType.Outlined -> {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    enabled = enabled.asBooleanValue(),
                    // Always true to not show the caret inside so that it can be deleted with delete key
                    readOnly = true,
                    label = label?.let {
                        {
                            Text(
                                text = it.transformedValueExpression(project),
                                modifier = Modifier.alpha(0.7f),
                            )
                        }
                    },
                    placeholder = placeholder?.let {
                        {
                            Text(
                                it.transformedValueExpression(project),
                                modifier = Modifier.alpha(0.7f),
                            )
                        }
                    },
                    leadingIcon = leadingIcon?.let {
                        {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = null,
                            )
                        }
                    },
                    trailingIcon = trailingIcon?.let {
                        {
                            Icon(
                                imageVector = it.imageVector,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = singleLine ?: false,
                    maxLines = maxLines ?: Integer.MAX_VALUE,
                    shape = shapeWrapper?.toShape() ?: TextFieldDefaults.shape,
                    colors = colors,
                    modifier = modifier.then(
                        node.modifierChainForCanvas()
                            .modifierForCanvas(
                                project = project,
                                node = node,
                                canvasNodeCallbacks = canvasNodeCallbacks,
                                paletteRenderParams = paletteRenderParams,
                                zoomableContainerStateHolder = zoomableContainerStateHolder,
                            ),
                    ),
                )
            }
        }
    }

    override fun generateCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement("%M(", textFieldType.toMemberName())
        codeBlockBuilder.add(
            generateParamsCode(
                project = project,
                node = node,
                context = context,
                dryRun = dryRun,
            )
        )
        val hasFocusedActions = node.actionsMap[ActionType.OnFocused]?.isNotEmpty() == true
        val hasUnfocusedActions = node.actionsMap[ActionType.OnUnfocused]?.isNotEmpty() == true
        val focusModifierCode = if (hasFocusedActions || hasUnfocusedActions) {
            val additionalCodeBuilder = CodeBlock.builder()
            additionalCodeBuilder.addStatement(
                ".%M {",
                MemberName("androidx.compose.ui.focus", "onFocusChanged")
            )
            if (hasFocusedActions) {
                additionalCodeBuilder.addStatement("if (it.isFocused) {")
                node.actionsMap[ActionType.OnFocused]?.forEach {
                    additionalCodeBuilder.add(
                        it.generateCodeBlock(
                            project,
                            context,
                            dryRun = dryRun
                        )
                    )
                }
                additionalCodeBuilder.addStatement("}")
            }
            if (hasUnfocusedActions) {
                additionalCodeBuilder.addStatement("if (!it.isFocused) {")
                node.actionsMap[ActionType.OnUnfocused]?.forEach {
                    additionalCodeBuilder.add(
                        it.generateCodeBlock(
                            project,
                            context,
                            dryRun = dryRun
                        )
                    )
                }
                additionalCodeBuilder.addStatement("}")
            }
            additionalCodeBuilder.addStatement("}")
            additionalCodeBuilder.build()
        } else {
            null
        }
        codeBlockBuilder.add(
            node.generateModifierCode(
                project,
                context,
                additionalCode = focusModifierCode,
                dryRun = dryRun,
            )
        )
        codeBlockBuilder.addStatement(")")

        if (node.actionsMap[ActionType.OnChange]?.isNotEmpty() == true) {
            node.companionStateId?.let { companionStateId ->
                val companionState = project.findLocalStateOrNull(companionStateId)

                if (companionState != null && companionState is WriteableState) {
                    val launchedEffectBuilder = CodeBlock.builder()
                    launchedEffectBuilder.add(
                        """
                    %M(Unit) {
                        ${context.currentEditable.viewModelName}.${companionState.getFlowName()}.%M {
                """,
                        MemberHolder.AndroidX.Runtime.LaunchedEffect,
                        MemberHolder.Coroutines.Flow.collectLatest,
                    )
                    node.actionsMap[ActionType.OnChange]?.forEach {
                        launchedEffectBuilder.add(
                            it.generateCodeBlock(
                                project,
                                context,
                                dryRun = dryRun
                            )
                        )
                    }
                    launchedEffectBuilder.addStatement("}") // End of collectLatest
                    launchedEffectBuilder.addStatement("}") // End of LaunchedEffect

                    context.addLaunchedEffectBlock(
                        launchedEffectBuilder.build(),
                        dryRun = dryRun
                    )
                }
            }
        }

        return codeBlockBuilder.build()
    }

    private fun shouldUseTransparentIndicator(): Boolean =
        transparentIndicator == true && textFieldType == TextFieldType.Default

    override fun getStateValidator(): ComposeStateValidator? {
        return if (enableValidator == true) {
            textFieldValidator
        } else {
            null
        }
    }
}

object TextFieldTypeSerializer : FallbackEnumSerializer<TextFieldType>(
    TextFieldType::class
)

@Serializable(TextFieldTypeSerializer::class)
enum class TextFieldType {
    Default {
        override fun toMemberName(): MemberName =
            MemberName("androidx.compose.material3", "TextField")
    },
    Outlined {
        override fun toMemberName(): MemberName =
            MemberName("androidx.compose.material3", "OutlinedTextField")
    },
    ;

    abstract fun toMemberName(): MemberName
}