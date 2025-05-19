package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.asVariableName
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.cloud.storage.BlobInfoWrapper
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.materialicons.ImageVectorHolder
import io.composeflow.materialicons.Outlined
import io.composeflow.materialicons.asCodeBlock
import io.composeflow.model.modifier.generateModifierCode
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.parameter.wrapper.Material3ColorWrapper
import io.composeflow.model.project.COMPOSEFLOW_PACKAGE
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.issue.Issue
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.ColorProperty
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.serializer.FallbackEnumSerializer
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.utils.asIconComposable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("IconTrait")
data class IconTrait(
    val assetType: IconAssetType = IconAssetType.Material,
    /**
     * Nullable because it's better to express an Icon with a nullable state.
     * E.g. The navigation icon in the TopAppBar
     */
    val imageVectorHolder: ImageVectorHolder? = Outlined.Add,
    /**
     * Effective only when [IconAssetType] is [IconAssetType.CustomAsset]
     */
    val blobInfoWrapper: BlobInfoWrapper? = null,
    val contentDescription: String = "Icon for ${imageVectorHolder?.name}",
    val tint: AssignableProperty? = ColorProperty.ColorIntrinsicValue(
        value = ColorWrapper(
            themeColor = Material3ColorWrapper.OnSurface
        )
    ),
) : ComposeTrait {

    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Tint", tint, ComposeFlowType.Color()),
        )
    }

    override fun generateIssues(
        project: Project,
    ): List<Issue> {
        return buildList {
            when (assetType) {
                IconAssetType.CustomAsset -> {
                    if (!project.assetHolder.icons.any { it.blobId.name == blobInfoWrapper?.blobId?.name }) {
                        add(Issue.InvalidAssetReference())
                    }
                }

                else -> {}
            }
        }
    }

    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(
            trait = mutableStateOf(defaultTrait()),
            modifierList = defaultModifierList(),
        )

    override fun icon(): ImageVector = Icons.Outlined.Add
    override fun iconText(): String = "Icon"
    override fun isResizeable(): Boolean = false

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        modifier: Modifier,
    ) {
        when (assetType) {
            IconAssetType.Material -> {
                imageVectorHolder?.let {
                    Icon(
                        imageVector = it.imageVector,
                        contentDescription = contentDescription,
                        tint = (tint as? ColorProperty.ColorIntrinsicValue)?.value?.getColor()
                            ?: MaterialTheme.colorScheme.onBackground,
                        modifier = modifier.then(
                            node.modifierChainForCanvas()
                                .modifierForCanvas(
                                    project = project,
                                    node = node,
                                    canvasNodeCallbacks = canvasNodeCallbacks,
                                    paletteRenderParams = paletteRenderParams,
                                ),
                        ),
                    )
                }
            }

            IconAssetType.CustomAsset -> {
                val userId = LocalFirebaseIdToken.current.user_id
                blobInfoWrapper?.asIconComposable(
                    userId = userId,
                    projectId = project.id.toString(),
                    tint = (tint as? ColorProperty.ColorIntrinsicValue)?.value?.getColor()
                        ?: MaterialTheme.colorScheme.onBackground,
                    modifier = modifier.then(
                        node.modifierChainForCanvas()
                            .modifierForCanvas(
                                project = project,
                                node = node,
                                canvasNodeCallbacks = canvasNodeCallbacks,
                                paletteRenderParams = paletteRenderParams,
                            ),
                    ),
                )
            }
        }
    }

    override fun paletteCategories(): List<TraitCategory> =
        listOf(TraitCategory.Common, TraitCategory.Basic)

    private fun generateParamsCode(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        when (assetType) {
            IconAssetType.Material -> {
                imageVectorHolder?.let {
                    codeBlockBuilder.add("imageVector = ")
                    codeBlockBuilder.add(imageVectorHolder.asCodeBlock())
                    codeBlockBuilder.addStatement(",")
                    codeBlockBuilder.addStatement("""contentDescription = "$contentDescription",""")
                    tint?.let {
                        codeBlockBuilder.add("tint = ")
                        codeBlockBuilder.add(
                            it.transformedCodeBlock(
                                project,
                                context,
                                ComposeFlowType.Color(),
                                dryRun = dryRun
                            )
                        )
                        codeBlockBuilder.addStatement(",")
                    }
                }
            }

            IconAssetType.CustomAsset -> {
                blobInfoWrapper?.let { blob ->
                    if (blob.fileName.endsWith(".xml")) {
                        // Vector drawables
                        codeBlockBuilder.add(
                            CodeBlock.of(
                                "imageVector = %M(%M.drawable.%M),",
                                MemberHolder.JetBrains.vectorResource,
                                MemberHolder.ComposeFlow.Res,
                                MemberName(
                                    COMPOSEFLOW_PACKAGE,
                                    blob.fileName.asVariableName().removeSuffix(".xml")
                                )
                            )
                        )
                    } else {
                        codeBlockBuilder.add(
                            CodeBlock.of(
                                "bitmap = %M(%M.drawable.%M),",
                                MemberHolder.JetBrains.imageResource,
                                MemberHolder.ComposeFlow.Res,
                                MemberName(
                                    COMPOSEFLOW_PACKAGE,
                                    blob.fileName.asVariableName().substringBeforeLast(".")
                                )
                            )
                        )
                    }
                    tint?.let {
                        codeBlockBuilder.add("tint = ")
                        codeBlockBuilder.add(
                            it.transformedCodeBlock(
                                project,
                                context,
                                ComposeFlowType.Color(),
                                dryRun
                            )
                        )
                        codeBlockBuilder.addStatement(",")
                    }
                    codeBlockBuilder.addStatement("""contentDescription = "",""")
                }
            }
        }
        return codeBlockBuilder.build()
    }

    override fun generateCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        val iconMember = MemberName("androidx.compose.material3", "Icon")
        codeBlockBuilder.addStatement("%M(", iconMember)
        codeBlockBuilder.add(
            generateParamsCode(
                project = project,
                context = context,
                dryRun,
            )
        )
        codeBlockBuilder.add(
            node.generateModifierCode(project, context, dryRun = dryRun)
        )
        codeBlockBuilder.addStatement(")")
        return codeBlockBuilder.build()
    }

    companion object {
        fun defaultTrait(): IconTrait = IconTrait(imageVectorHolder = Outlined.Add)
    }
}

object IconAssetTypeSerializer : FallbackEnumSerializer<IconAssetType>(IconAssetType::class)

@Serializable(IconAssetTypeSerializer::class)
enum class IconAssetType {
    /**
     * Built-in material icons
     */
    Material,

    /**
     * User defined custom icons
     */
    CustomAsset
    ;
}