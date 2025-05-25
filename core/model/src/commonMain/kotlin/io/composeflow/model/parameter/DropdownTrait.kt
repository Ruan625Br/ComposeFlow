package io.composeflow.model.parameter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import io.composeflow.model.project.findLocalStateOrNull
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.CustomEnumValuesProperty
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.property.StringProperty
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.state.ScreenState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.WriteableState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.textfield.DropdownMenuTextField
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DropdownTrait")
data class DropdownTrait(
    val selectedItem: AssignableProperty? = null,
    val items: AssignableProperty? = null,
    val label: AssignableProperty = StringProperty.StringIntrinsicValue("Dropdown item..."),
) : ComposeTrait {

    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Items", items, ComposeFlowType.StringType(isList = true)),
            PropertyContainer("Label", label, ComposeFlowType.StringType()),
        )
    }

    private fun generateParamsCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.add("items = ")
        when (items) {
            null -> {
                codeBlockBuilder.add("emptyList()")
            }

            else -> {
                codeBlockBuilder.add(items.transformedCodeBlock(project, context, dryRun = dryRun))
            }
        }
        codeBlockBuilder.addStatement(",")

        codeBlockBuilder.add("label = ")
        codeBlockBuilder.add(label.transformedCodeBlock(project, context, dryRun = dryRun))
        codeBlockBuilder.addStatement(",")

        val writeState = when (selectedItem) {
            is ValueFromState -> {
                project.findLocalStateOrNull(selectedItem.readFromStateId)
            }

            else -> null
        }
        val canvasEditable = project.findCanvasEditableHavingNodeOrNull(node)
        codeBlockBuilder.addStatement("onValueChange = {")
        if (writeState != null && canvasEditable != null && writeState is WriteableState) {
            codeBlockBuilder.add(
                writeState.generateWriteBlock(
                    project,
                    canvasEditable,
                    context,
                    dryRun = dryRun
                )
            )
        }
        if (node.actionsMap[ActionType.OnChange]?.isNotEmpty() == true) {
            node.actionsMap[ActionType.OnChange]?.forEach {
                codeBlockBuilder.add(it.generateCodeBlock(project, context, dryRun = dryRun))
            }
        }
        codeBlockBuilder.addStatement("},")

        selectedItem?.let {
            codeBlockBuilder.add("selectedItem = ")
            codeBlockBuilder.add(
                selectedItem.transformedCodeBlock(
                    project,
                    context,
                    ComposeFlowType.StringType(),
                    dryRun = dryRun
                )
            )
            codeBlockBuilder.addStatement(",")
        }
        return codeBlockBuilder.build()
    }

    override fun defaultComposeNode(project: Project): ComposeNode =
        ComposeNode(trait = mutableStateOf(DropdownTrait()))

    override fun icon(): ImageVector = Icons.Outlined.FormatListNumbered
    override fun iconText(): String = "Dropdown"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)

    override fun isResizeable(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnChange)
    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName =
            stateHolder.createUniqueName(project = project, initial = "dropdownSelectedText")
        val screenState =
            ScreenState.StringScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)

        val dropdownTrait = node.trait.value as DropdownTrait
        node.companionStateId = screenState.id
        node.trait.value =
            dropdownTrait.copy(selectedItem = ValueFromState(screenState.id))
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
        val items = when (val items = items) {
            is CustomEnumValuesProperty -> {
                items.rawVale(project)
            }

            else -> emptyList()
        }
        var selectedText by remember(node.id) { mutableStateOf("") }
        DropdownMenuTextField(
            items = items,
            label = label.transformedValueExpression(project),
            onValueChange = {
                selectedText = it
            },
            selectedItem = selectedText,
            modifier = modifier.then(
                node.modifierChainForCanvas()
                    .modifierForCanvas(
                        project = project,
                        node = node,
                        canvasNodeCallbacks = canvasNodeCallbacks,
                        zoomableContainerStateHolder = zoomableContainerStateHolder,
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
            MemberName("io.composeflow.ui.textfield", "DropdownMenuTextField"),
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
