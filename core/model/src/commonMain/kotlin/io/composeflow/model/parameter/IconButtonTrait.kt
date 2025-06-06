package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.Res
import io.composeflow.cloud.storage.BlobInfoWrapper
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.materialicons.ImageVectorHolder
import io.composeflow.materialicons.Outlined
import io.composeflow.model.modifier.generateModifierCode
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.parameter.wrapper.Material3ColorWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.ColorProperty
import io.composeflow.tooltip_icon_trait
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

/**
 * Trait for rendering an IconButton.
 * This is not visible in the palette, but exists to minimize the serializer error produced by LLM.
 * Has almost the same functionality as IconTrait except that it renders an IconButton.
 */
@Serializable
@SerialName("IconButtonTrait")
data class IconButtonTrait(
    override val assetType: IconAssetType = IconAssetType.Material,
    override val imageVectorHolder: ImageVectorHolder? = Outlined.Add,
    override val blobInfoWrapper: BlobInfoWrapper? = null,
    override val contentDescription: String = "Icon for ${imageVectorHolder?.name}",
    override val tint: AssignableProperty? = ColorProperty.ColorIntrinsicValue(
        value = ColorWrapper(
            themeColor = Material3ColorWrapper.OnSurface
        )
    ),
) : AbstractIconTrait(assetType, imageVectorHolder, blobInfoWrapper, contentDescription, tint),
    ComposeTrait {

    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(
            trait = mutableStateOf(IconButtonTrait(imageVectorHolder = Outlined.Add)),
        ).apply {
            addChild(
                ComposeNode(
                    trait = mutableStateOf(IconTrait.defaultTrait()),
                )
            )
        }

    override fun icon(): ImageVector = Icons.Outlined.AddCircle
    override fun iconText(): String = "IconButton"
    override fun tooltipResource(): StringResource = Res.string.tooltip_icon_trait
    override fun isResizeable(): Boolean = false
    override fun visibleInPalette(): Boolean = false

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        zoomableContainerStateHolder: ZoomableContainerStateHolder,
        modifier: Modifier,
    ) {
        IconButton(
            onClick = {},
            modifier = modifier.then(
                node.modifierChainForCanvas()
                    .modifierForCanvas(
                        project = project,
                        node = node,
                        canvasNodeCallbacks = canvasNodeCallbacks,
                        paletteRenderParams = paletteRenderParams,
                        zoomableContainerStateHolder = zoomableContainerStateHolder,
                    ),
            )
        ) {
            node.children.forEach { child ->
                super<AbstractIconTrait>.RenderedNode(
                    project,
                    child,
                    canvasNodeCallbacks,
                    paletteRenderParams,
                    zoomableContainerStateHolder,
                    Modifier
                )
            }
        }
    }

    // Has Container category as it can hold icons as its children
    override fun paletteCategories(): List<TraitCategory> =
        listOf(TraitCategory.Common, TraitCategory.Basic, TraitCategory.Container)

    override fun generateCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        val iconButtonMember = MemberName("androidx.compose.material3", "IconButton")
        // Click handler should be set in the modifier of the IconTrait
        codeBlockBuilder.addStatement("%M(onClick = {},", iconButtonMember)
        codeBlockBuilder.add(
            node.generateModifierCode(project, context, dryRun = dryRun)
        )
        codeBlockBuilder.addStatement(") {", iconButtonMember)
        node.children.forEach { child ->
            codeBlockBuilder.add(child.generateCode(project, context, dryRun))
        }
        codeBlockBuilder.addStatement("}", iconButtonMember)
        return codeBlockBuilder.build()
    }
}
