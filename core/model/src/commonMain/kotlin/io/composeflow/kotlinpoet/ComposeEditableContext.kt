package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.composeflow.ViewModelConstant
import io.composeflow.model.project.CanvasEditable
import io.composeflow.util.generateUniqueName

/**
 * Context that holds the related information to generate the pair of ViewModel and the Compose
 * screen.
 */
class ComposeEditableContext(
    private val typeSpecBuilder: TypeSpec.Builder,
    val canvasEditable: CanvasEditable,
) {
    private val funSpecs: MutableSet<FunSpec> = mutableSetOf()
    private val propertySpecs: MutableSet<PropertySpec> = mutableSetOf()
    private val prioritizedPropertySpecs: MutableSet<PropertySpec> = mutableSetOf()
    private val _dependencies: MutableSet<ViewModelConstant> = mutableSetOf()
    private val constructorFunSpecBuilder = FunSpec.constructorBuilder()
    var isCoroutineScopeUsed: Boolean = false
    val dependencies: Set<ViewModelConstant> = _dependencies

    fun allProperties(): List<PropertySpec> = prioritizedPropertySpecs.toList() + propertySpecs.toList()

    /**
     * LaunchedEffect block for Composable screen.
     */
    private val _launchedEffectBlock: MutableSet<CodeBlock> = mutableSetOf()
    val launchedEffectBlock: Set<CodeBlock> = _launchedEffectBlock

    /**
     * Holds the information about the map of the ID to identifier (such as variable name within
     * the pair of the Composable and the ViewModel) to avoid the conflicting variable names.
     */
    private val identifierMap: MutableMap<String, String> = mutableMapOf()

    /**
     * Holds the pairs of a variable name and corresponding MemberName used for retrieving a value in
     * CompositionLocal.
     * e.g. `val uriHandler = LocalUriHandler.current`, then following pair will be added to the map
     *  key : "uriHandler"
     *  value : MemberName("androidx.compose.ui.platform", "LocalUriHandler")
     */
    private val _compositionLocalVariables: MutableMap<String, MemberName> = mutableMapOf()
    val compositionLocalVariables: Map<String, MemberName> = _compositionLocalVariables

    fun addFunction(
        funSpec: FunSpec,
        dryRun: Boolean,
    ) {
        if (dryRun) return
        if (funSpecs.any { it.name == funSpec.name }) return
        val newName =
            generateUniqueName(
                funSpec.name,
                funSpecs.map { it.name }.toSet(),
            )
        funSpecs.add(funSpec.toBuilder(newName).build())
    }

    fun addFunctionInConstructor(
        funSpec: FunSpec,
        dryRun: Boolean,
    ) {
        if (dryRun) return
        constructorFunSpecBuilder.addCode(funSpec.body)
    }

    fun addProperty(
        propertySpec: PropertySpec,
        dryRun: Boolean,
    ) {
        if (dryRun) return
        if (propertySpecs.any { it.name == propertySpec.name }) return
        val newName = generateUniquePropertyName(initial = propertySpec.name)
        propertySpecs.add(propertySpec.toBuilder(newName).build())
    }

    fun addDependency(viewModelConstant: ViewModelConstant) {
        _dependencies.add(viewModelConstant)
    }

    /**
     * Add property that needs to be defined before the normal properties. Since some properties needs
     * to be defined before other properties.
     * (e.g. FlowSettings property needs to be defined before each property that depends on FlowSettings)
     */
    fun addPrioritizedProperty(
        propertySpec: PropertySpec,
        dryRun: Boolean,
    ) {
        if (dryRun) return
        if (prioritizedPropertySpecs.any { it.name == propertySpec.name }) return
        val newName = generateUniquePropertyName(initial = propertySpec.name)
        prioritizedPropertySpecs.add(propertySpec.toBuilder(newName).build())
    }

    /**
     * Get the identifier or create a unique identifier within the pair of Composable file and the
     * ViewModel
     *
     * @param id the ID of the identifier. For example id of [State] or [ComposeNode], [Action]
     * @param initialIdentifier initial identifier for the identifier
     */
    fun getOrAddIdentifier(
        id: String,
        initialIdentifier: String,
    ): String =
        identifierMap.getOrPut(id) {
            generateUniqueName(
                initialIdentifier,
                identifierMap.values.toSet(),
            )
        }

    /**
     * LaunchedEffect code block in the Compose code.
     */
    fun addLaunchedEffectBlock(
        codeBlock: CodeBlock,
        dryRun: Boolean,
    ) {
        if (dryRun) return
        _launchedEffectBlock.add(codeBlock)
    }

    fun addCompositionLocalVariableEntryIfNotPresent(
        id: String,
        initialIdentifier: String,
        compositionLocalMember: MemberName,
    ): String {
        val newName = getOrAddIdentifier(id, initialIdentifier)
        if (!_compositionLocalVariables.contains(newName)) {
            _compositionLocalVariables[newName] = compositionLocalMember
        }
        return newName
    }

    /**
     * Add the variable name to the Compose file. Returns the generated unique name
     */
    fun addComposeFileVariable(
        id: String,
        initialIdentifier: String,
        dryRun: Boolean,
    ): String {
        if (dryRun) return initialIdentifier
        val newName = getOrAddIdentifier(id, initialIdentifier)
        return newName
    }

    fun buildTypeSpec(): TypeSpec {
        prioritizedPropertySpecs.forEach {
            typeSpecBuilder.addProperty(it)
        }
        propertySpecs.forEach {
            typeSpecBuilder.addProperty(it)
        }
        funSpecs.forEach {
            typeSpecBuilder.addFunction(it)
        }
        typeSpecBuilder.primaryConstructor(constructorFunSpecBuilder.build())
        return typeSpecBuilder.build()
    }

    private fun generateUniquePropertyName(initial: String): String =
        generateUniqueName(
            initial,
            propertySpecs.plus(prioritizedPropertySpecs).map { it.name }.toSet(),
        )

    fun generateUniqueFunName(
        id: String,
        initial: String,
    ): String = getOrAddIdentifier(id = id, initialIdentifier = initial)
}
