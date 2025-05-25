package io.composeflow.model.parameter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.Row
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.modifier.generateModifierCode
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.parameter.wrapper.AlignmentVerticalWrapper
import io.composeflow.model.parameter.wrapper.ArrangementHorizontalWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RowTrait")
data class RowTrait(
    val horizontalArrangement: ArrangementHorizontalWrapper? = null,
    val verticalAlignment: AlignmentVerticalWrapper? = null,
) : ComposeTrait {
    override fun areAllParamsEmpty(): Boolean =
        horizontalArrangement == null &&
                verticalAlignment == null

    private fun generateParamsCode(): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        horizontalArrangement?.let {
            val arrangementMember = MemberName("androidx.compose.foundation.layout", "Arrangement")
            codeBlockBuilder.addStatement(
                "horizontalArrangement = %M.${it.name},",
                arrangementMember,
            )
        }
        verticalAlignment?.let {
            val alignmentMember = MemberName("androidx.compose.ui", "Alignment")
            codeBlockBuilder.addStatement(
                "verticalAlignment = %M.${it.name},",
                alignmentMember,
            )
        }
        return codeBlockBuilder.build()
    }

    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(
            modifierList = defaultModifierList(),
            trait = mutableStateOf(RowTrait()),
        )

    override fun icon(): ImageVector = ComposeFlowIcons.Row
    override fun iconText(): String = "Row"
    override fun paletteCategories(): List<TraitCategory> = listOf(
        TraitCategory.Common,
        TraitCategory.Container,
        TraitCategory.WrapContainer,
        TraitCategory.Layout
    )

    override fun defaultModifierList(): MutableList<ModifierWrapper> =
        mutableStateListEqualsOverrideOf(
            ModifierWrapper.Width(200.dp),
            ModifierWrapper.Height(120.dp),
            ModifierWrapper.Padding(all = 8.dp),
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
        Row(
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
            horizontalArrangement = horizontalArrangement?.arrangement
                ?: Arrangement.Start,
            verticalAlignment = verticalAlignment?.alignment ?: Alignment.Top,
        ) {
            node.children.forEach { child ->
                child.RenderedNodeInCanvas(
                    project = project,
                    canvasNodeCallbacks = canvasNodeCallbacks,
                    paletteRenderParams = paletteRenderParams,
                    zoomableContainerStateHolder = zoomableContainerStateHolder,
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
        val rowMember = MemberName("androidx.compose.foundation.layout", "Row")
        val allParamsEmpty = areAllParamsEmpty() && node.modifierList.isEmpty()
        if (allParamsEmpty) {
            codeBlockBuilder.addStatement("%M {", rowMember)
        } else {
            codeBlockBuilder.addStatement("%M(", rowMember)
            codeBlockBuilder.add(
                generateParamsCode()
            )
            codeBlockBuilder.add(
                node.generateModifierCode(project, context, dryRun = dryRun)
            )
            codeBlockBuilder.addStatement(") {")
        }
        node.children.forEach {
            codeBlockBuilder.add(
                it.generateCode(
                    project = project,
                    context = context,
                    dryRun = dryRun,
                )
            )
        }
        codeBlockBuilder.addStatement("}")
        return codeBlockBuilder.build()
    }
}
