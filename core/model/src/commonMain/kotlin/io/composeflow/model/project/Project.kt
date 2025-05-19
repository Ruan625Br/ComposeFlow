package io.composeflow.model.project

import com.squareup.kotlinpoet.FileSpec
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.apieditor.ApiId
import io.composeflow.model.datatype.DataType
import io.composeflow.model.project.api.ApiHolder
import io.composeflow.model.project.appscreen.ScreenHolder
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.asset.AssetHolder
import io.composeflow.model.project.component.Component
import io.composeflow.model.project.component.ComponentHolder
import io.composeflow.model.project.component.ComponentId
import io.composeflow.model.project.custom_enum.CustomEnum
import io.composeflow.model.project.custom_enum.CustomEnumHolder
import io.composeflow.model.project.datatype.DataTypeHolder
import io.composeflow.model.project.firebase.CollectionId
import io.composeflow.model.project.firebase.FirebaseAppInfoHolder
import io.composeflow.model.project.firebase.FirestoreCollection
import io.composeflow.model.project.issue.TrackableIssue
import io.composeflow.model.project.theme.ThemeHolder
import io.composeflow.model.state.ReadableState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.StateHolderImpl
import io.composeflow.model.state.StateId
import io.composeflow.serializer.yamlSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.uuid.Uuid

const val COMPOSEFLOW_PACKAGE = "io.composeflow"

@Serializable
@SerialName("Project")
data class Project(
    val id: String = Uuid.random().toString(),
    val name: String = "project",
    val packageName: String = "com.example",
    val screenHolder: ScreenHolder = ScreenHolder(),
    val apiHolder: ApiHolder = ApiHolder(),
    val dataTypeHolder: DataTypeHolder = DataTypeHolder(),
    val customEnumHolder: CustomEnumHolder = CustomEnumHolder(),
    val componentHolder: ComponentHolder = ComponentHolder(),
    val themeHolder: ThemeHolder = ThemeHolder(),
    val assetHolder: AssetHolder = AssetHolder(),
    val firebaseAppInfoHolder: FirebaseAppInfoHolder = FirebaseAppInfoHolder(),
    val globalStateHolder: StateHolderImpl = StateHolderImpl(),
) : StateHolder by globalStateHolder {

    @Transient
    var lastModified: Instant? = Clock.System.now()

    @Transient
    val bundleId: String = "$packageName.$name"

    fun generateCode(): List<FileSpec?> {
        // Memorize the count of how many components are used to generate the viewModelKey
        val context = GenerationContext()
        screenHolder.generateComposeLauncherFile()

        return screenHolder.screens.flatMap { it.generateCode(this, context = context) } +
                screenHolder.generateComposeLauncherFile() +
                screenHolder.generateAppNavHostFile(this, context = context) +
                screenHolder.generateKoinViewModelModule(this) +
                screenHolder.generateAppViewModel(this) +
                screenHolder.generateScreenRouteFileSpec(this) +
//                apiHolder.generatePagingSourcesFileSpec(this) +
                dataTypeHolder.generateDataTypeFiles(this) +
                customEnumHolder.generateEnumFiles(this) +
                themeHolder.generateThemeFiles() +
                componentHolder.generateKoinViewModelModule(this) +
                componentHolder.components.flatMap {
                    it.generateCode(this, context = context)
                }
    }

    /**
     * Generate the instructions to copy specified resources into the destination directory.
     * For example, to ensure font files are included in the generated app.
     *
     * The from path is expected to be loaded from resources folder obtained like
     * `object {}.javaClass.getResourceAsStream("/composeResources/io.composeflow/font/Caveat-Bold.ttf")`
     *
     * The destination path is a relative path from the app's root directory e.g.:
     * `"composeApp/src/commonMain/composeResources/font/Caveat-Bold.ttf"`
     */
    fun generateCopyInstructions(): Map<String, String> {
        return themeHolder.fontHolder.generateCopyFileInstructions()
    }

    fun generateCopyLocalFileInstructions(userId: String): Map<String, String> {
        return assetHolder.generateCopyLocalFileInstructions(
            userId = userId,
            projectId = id.toString(),
        )
    }

    fun getAllCanvasEditable(): List<CanvasEditable> {
        return componentHolder.components + screenHolder.screens
    }

    fun getAllComposeNodes(): List<ComposeNode> {
        return (screenHolder.screens + componentHolder.components).flatMap {
            it.getAllComposeNodes()
        }
    }

    fun generateTrackableIssues(): List<TrackableIssue> {
        return (screenHolder.screens + componentHolder.components).flatMap { it.getAllComposeNodes() }
            .flatMap { node ->
                node.generateTrackableIssues(this)
            } + apiHolder.generateTrackableIssues()
    }

    companion object {

        fun deserializeFromString(yaml: String): Project {
            val project = yamlSerializer.decodeFromString<Project>(yaml)
            project.screenHolder.updateChildParentRelationships()
            return project
        }
    }
}

fun Project.serialize(): String = yamlSerializer.encodeToString(this)

/**
 * Find matching State from the all states including all Screens within the project
 */
fun Project.findLocalStateOrNull(stateId: StateId): ReadableState? {
    return globalStateHolder.findStateOrNull(this, stateId)
        ?: screenHolder.screens.firstOrNull { it.findStateOrNull(this, stateId) != null }
            ?.findStateOrNull(this, stateId)
        ?: componentHolder.components.firstOrNull { it.findStateOrNull(this, stateId) != null }
            ?.findStateOrNull(this, stateId)
}

fun Project.removeLocalState(stateId: StateId) {
    screenHolder.screens.firstOrNull {
        it.findStateOrNull(this, stateId) != null
    }?.removeState(stateId)
    componentHolder.components.firstOrNull {
        it.findStateOrNull(this, stateId) != null
    }?.removeState(stateId)
}

fun Project.findComposeNodeOrNull(nodeId: String): ComposeNode? {
    var node: ComposeNode?
    getAllCanvasEditable().forEach { canvasEditable ->
        node = canvasEditable.getAllComposeNodes().firstOrNull { it.id == nodeId }
        if (node != null) {
            return node
        }
    }
    return null
}

fun Project.findComposeNodeOrThrow(nodeId: String): ComposeNode {
    var node: ComposeNode?
    getAllCanvasEditable().forEach { canvasEditable ->
        node = canvasEditable.getAllComposeNodes().firstOrNull { it.id == nodeId }
        if (node != null) {
            return node
        }
    }
    throw IllegalStateException("No node is found : $nodeId")
}

fun Project.replaceNode(nodeId: String, newNode: ComposeNode) {
    var node: ComposeNode?
    getAllCanvasEditable().forEach { editable ->
        node = editable.getAllComposeNodes().firstOrNull { it.id == nodeId }
        node?.let { node ->
            val indexInParent = node.parentNode?.children?.indexOfFirst { it.id == nodeId }
            indexInParent?.let { index ->
                node.parentNode?.children?.set(index, newNode)
            }
        }
    }
}

fun Project.findDataTypeOrNull(dataTypeId: String): DataType? {
    return dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId }
}

fun Project.findCustomEnumOrNull(customEnumId: String): CustomEnum? {
    return customEnumHolder.enumList.firstOrNull { it.customEnumId == customEnumId }
}

fun Project.findDataTypeOrThrow(dataTypeId: String): DataType {
    return findDataTypeOrNull(dataTypeId)
        ?: throw IllegalStateException("No Data Type is found : $dataTypeId")
}

fun Project.findCanvasEditableHavingNodeOrNull(node: ComposeNode): CanvasEditable? {
    val screen = screenHolder.screens.firstOrNull { it.hasComposeNode(node) }
    if (screen != null) return screen
    val component = componentHolder.components.firstOrNull { it.hasComposeNode(node) }
    return component
}

fun Project.findCanvasEditableOrNull(id: String): CanvasEditable? {
    val screen = screenHolder.screens.firstOrNull { it.id == id }
    if (screen != null) return screen
    val component = componentHolder.components.firstOrNull { it.id == id }
    return component
}


fun Project.findScreenOrNull(screenId: String): Screen? = screenHolder.findScreen(screenId)

fun Project.findComponentOrNull(componentId: ComponentId): Component? =
    componentHolder.components.firstOrNull { it.id == componentId }

fun Project.findComponentOrThrow(componentId: ComponentId): Component =
    findComponentOrNull(componentId)
        ?: throw IllegalStateException("No component is found : $componentId")

fun Project.findParameterOrNull(parameterId: ParameterId): ParameterWrapper<*>? {
    val allParameters = componentHolder.components.flatMap {
        it.parameters
    } + screenHolder.screens.flatMap {
        it.parameters
    }

    return allParameters.firstOrNull {
        it.id == parameterId
    }
}

fun Project.findParameterOrThrow(parameterId: ParameterId): ParameterWrapper<*> =
    findParameterOrNull(parameterId)
        ?: throw IllegalStateException("No parameter is found : $parameterId")

fun Project.findApiDefinitionOrNull(apiId: ApiId): ApiDefinition? =
    apiHolder.findApiDefinitionOrNull(apiId)

fun Project.findFirestoreCollectionOrNull(collectionId: CollectionId): FirestoreCollection? {
    return firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections.firstOrNull { it.id == collectionId }
}
