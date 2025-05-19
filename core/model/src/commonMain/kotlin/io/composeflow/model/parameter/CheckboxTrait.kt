package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
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
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CheckboxTrait")
data class CheckboxTrait(
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
                codeBlockBuilder.add(it.generateCodeBlock(project, context, dryRun = dryRun))
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
                CheckboxTrait(
                    checked = BooleanProperty.BooleanIntrinsicValue(false),
                    enabled = BooleanProperty.BooleanIntrinsicValue(true),
                )
            )
        )

    override fun icon(): ImageVector = Icons.Outlined.CheckBox
    override fun iconText(): String = "Checkbox"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)
    override fun isResizeable(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnChange)
    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName = stateHolder.createUniqueName(project = project, initial = "checkbox")
        val screenState =
            ScreenState.BooleanScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)

        val checkboxTrait = node.trait.value as CheckboxTrait
        node.companionStateId = screenState.id
        node.trait.value =
            checkboxTrait.copy(checked = ValueFromState(screenState.id))
    }

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        modifier: Modifier,
    ) {
        Checkbox(
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
            MemberName("androidx.compose.material3", "Checkbox"),
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

