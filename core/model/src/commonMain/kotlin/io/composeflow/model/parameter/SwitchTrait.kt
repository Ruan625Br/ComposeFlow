package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.Res
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
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.property.asBooleanValue
import io.composeflow.model.state.ScreenState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.WriteableState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.tooltip_switch_trait
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
@SerialName("SwitchTrait")
data class SwitchTrait(
    val checked: AssignableProperty = BooleanProperty.BooleanIntrinsicValue(false),
    val enabled: AssignableProperty = BooleanProperty.BooleanIntrinsicValue(true),
) : ComposeTrait {

    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Checked", checked, ComposeFlowType.BooleanType()),
            PropertyContainer("Enabled", enabled, ComposeFlowType.BooleanType()),
        )
    }

    private fun generateParamsCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.add("checked = ")
        codeBlockBuilder.add(checked.transformedCodeBlock(project, context, dryRun = dryRun))
        codeBlockBuilder.addStatement(",")

        val canvasEditable = project.findCanvasEditableHavingNodeOrNull(node)
        val writeState = when (checked) {
            is ValueFromState -> {
                canvasEditable?.findStateOrNull(project, checked.readFromStateId)
            }

            else -> null
        }
        codeBlockBuilder.addStatement("onCheckedChange = {")
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

        enabled.let {
            codeBlockBuilder.add("enabled = ")
            codeBlockBuilder.add(it.transformedCodeBlock(project, context, dryRun = dryRun))
            codeBlockBuilder.addStatement(",")
        }
        return codeBlockBuilder.build()
    }

    // onCheckedChange is included in params. Handle the onClick in that method
    override fun onClickIncludedInParams(): Boolean = true
    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(
            trait = mutableStateOf(
                SwitchTrait(
                    checked = BooleanProperty.BooleanIntrinsicValue(false),
                    enabled = BooleanProperty.BooleanIntrinsicValue(true),
                )
            )
        )

    override fun icon(): ImageVector = Icons.Outlined.ToggleOn
    override fun iconText(): String = "Switch"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)
    override fun tooltipResource(): StringResource = Res.string.tooltip_switch_trait
    override fun isResizeable(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnChange)
    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName = stateHolder.createUniqueName(project = project, initial = "switch")
        val screenState =
            ScreenState.BooleanScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)

        val switchTrait = node.trait.value as SwitchTrait
        node.companionStateId = screenState.id
        node.trait.value =
            switchTrait.copy(checked = ValueFromState(screenState.id))

        node.label.value = stateName
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
        Switch(
            checked = false,
            onCheckedChange = {
            },
            enabled = enabled.asBooleanValue(),
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
            MemberName("androidx.compose.material3", "Switch"),
        )
        codeBlockBuilder.add(
            generateParamsCode(
                project = project,
                node = node,
                context = context,
                dryRun = dryRun
            )
        )
        codeBlockBuilder.add(
            node.generateModifierCode(project, context, dryRun = dryRun)
        )
        codeBlockBuilder.addStatement(")")
        return codeBlockBuilder.build()
    }
}

