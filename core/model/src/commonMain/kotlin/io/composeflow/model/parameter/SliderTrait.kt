package io.composeflow.model.parameter

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.Slider
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.action.ActionType
import io.composeflow.model.modifier.generateModifierCode
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.findCanvasEditableHavingNodeOrNull
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.FloatProperty
import io.composeflow.model.property.IntProperty
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.state.ScreenState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.WriteableState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("SliderTrait")
data class SliderTrait(
    val value: AssignableProperty = FloatProperty.FloatIntrinsicValue(),
    val enabled: AssignableProperty? = null,
    val steps: AssignableProperty? = null,
) : ComposeTrait {

    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Value", value, ComposeFlowType.FloatType()),
            PropertyContainer("Enabled", enabled, ComposeFlowType.BooleanType()),
            PropertyContainer("Steps", steps, ComposeFlowType.IntType()),
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
        codeBlockBuilder.add(value.transformedCodeBlock(project, context, dryRun = dryRun))
        codeBlockBuilder.addStatement(",")

        val canvasEditable = project.findCanvasEditableHavingNodeOrNull(node)
        val writeState = when (value) {
            is ValueFromState -> {
                canvasEditable?.findStateOrNull(project, value.readFromStateId)
            }

            else -> null
        }
        codeBlockBuilder.addStatement("onValueChange = {")
        if (node.actionsMap[ActionType.OnChange]?.isNotEmpty() == true) {
            node.actionsMap[ActionType.OnChange]?.forEach {
                codeBlockBuilder.add(it.generateCodeBlock(project, context, dryRun))
            }
        }
        if (writeState != null &&
            canvasEditable != null &&
            writeState is WriteableState &&
            node.companionStateId == writeState.id
        ) {
            codeBlockBuilder.add(
                writeState.generateWriteBlock(
                    project,
                    canvasEditable,
                    context,
                    dryRun = dryRun
                )
            )
        }
        codeBlockBuilder.addStatement("},")
        enabled?.let {
            codeBlockBuilder.add("enabled = ")
            codeBlockBuilder.add(it.transformedCodeBlock(project, context, dryRun = dryRun))
            codeBlockBuilder.addStatement(",")
        }
        steps?.let {
            codeBlockBuilder.add("steps = ")
            codeBlockBuilder.add(it.transformedCodeBlock(project, context, dryRun = dryRun))
            codeBlockBuilder.addStatement(",")
        }
        return codeBlockBuilder.build()
    }

    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(
            trait = mutableStateOf(
                SliderTrait(
                    value = FloatProperty.FloatIntrinsicValue()
                )
            )
        )

    override fun icon(): ImageVector = ComposeFlowIcons.Slider
    override fun iconText(): String = "Slider"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)
    override fun isResizeable(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnChange)
    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName = stateHolder.createUniqueName(project = project, initial = "slider")
        val screenState =
            ScreenState.FloatScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)

        val sliderTrait = node.trait.value as SliderTrait
        node.companionStateId = screenState.id
        node.trait.value =
            sliderTrait.copy(value = ValueFromState(screenState.id))
    }

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        zoomableContainerStateHolder: ZoomableContainerStateHolder,
        modifier: Modifier,
    ) {
        var value by remember(node.id) { mutableStateOf(0f) }
        Slider(
            value = value,
            onValueChange = {
                value = it
            },
            steps = when (steps) {
                is IntProperty.IntIntrinsicValue -> steps.value
                else -> 0
            },
            enabled =
                when (enabled) {
                    is BooleanProperty.BooleanIntrinsicValue -> enabled.value
                    is ValueFromState -> true
                    BooleanProperty.Empty -> false
                    else -> true
                },
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

    override fun generateCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement(
            "%M(",
            MemberName("androidx.compose.material3", "Slider"),
        )
        codeBlockBuilder.add(
            generateParamsCode(
                project = project,
                node = node,
                context = context,
                dryRun = dryRun,
            )
        )
        codeBlockBuilder.add(
            node.generateModifierCode(project, context, dryRun = dryRun)
        )
        codeBlockBuilder.addStatement(")")
        return codeBlockBuilder.build()
    }
}

