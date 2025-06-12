package io.composeflow.model.parameter

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.Res
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.Choice
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.model.action.ActionType
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.modifier.generateCode
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
import io.composeflow.model.state.ScreenState
import io.composeflow.model.state.StateHolder
import io.composeflow.model.state.WriteableState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.tooltip_chip_group_trait
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.modifierForCanvas
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource

@Serializable
@SerialName("ChipGroupTrait")
data class ChipGroupTrait(
    val chipItems: AssignableProperty? = null,
    val selectable: Boolean = true,
    val multiSelect: Boolean = false,
    val elevated: Boolean = false,
    val wrapContent: Boolean = false,
) : ComposeTrait {
    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Chip items", chipItems, ComposeFlowType.StringType(isList = true)),
        )
    }

    override fun defaultComposeNode(project: Project): ComposeNode {
        val chipGroup = ComposeNode(
            modifierList = defaultModifierList(),
            trait = mutableStateOf(ChipGroupTrait()),
        )
        return chipGroup
    }

    override fun onAttachStateToNode(
        project: Project,
        stateHolder: StateHolder,
        node: ComposeNode,
    ) {
        val stateName =
            stateHolder.createUniqueName(project = project, initial = "chipGroupSelectedItems")

        val companionEnum = project.customEnumHolder.newCustomEnum("ChipGroupItem").apply {
            values.addAll(listOf("Chip1", "Chip2", "Chip3"))
        }
        project.customEnumHolder.enumList.add(companionEnum)
        val chipGroupTrait = node.trait.value as ChipGroupTrait
        node.trait.value =
            chipGroupTrait.copy(chipItems = CustomEnumValuesProperty(companionEnum.customEnumId))

        val screenState =
            ScreenState.StringListScreenState(name = stateName, companionNodeId = node.id)
        stateHolder.addState(screenState)
        node.companionStateId = screenState.id
        node.label.value = stateName
    }

    override fun icon(): ImageVector = ComposeFlowIcons.Choice
    override fun iconText(): String = "ChipGroup"
    override fun paletteCategories(): List<TraitCategory> =
        listOf(TraitCategory.Basic, TraitCategory.Container)

    override fun tooltipResource(): StringResource = Res.string.tooltip_chip_group_trait

    override fun isDroppable(): Boolean = false
    override fun isResizeable(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnChange)

    @Composable
    override fun RenderedNode(
        project: Project,
        node: ComposeNode,
        canvasNodeCallbacks: CanvasNodeCallbacks,
        paletteRenderParams: PaletteRenderParams,
        zoomableContainerStateHolder: ZoomableContainerStateHolder,
        modifier: Modifier,
    ) {
        val selectedChipItems = remember(node.id) { mutableStateListOf<String>() }

        @Composable
        fun LocalChip(
            label: String = "",
            chipGroupTrait: ChipGroupTrait,
        ) {
            if (chipGroupTrait.selectable) {
                FilterChip(
                    selected = selectedChipItems.contains(label),
                    onClick = {
                        if (chipGroupTrait.multiSelect) {
                            if (selectedChipItems.contains(label)) {
                                selectedChipItems.remove(label)
                            } else {
                                selectedChipItems.add(label)
                            }
                        } else {
                            if (selectedChipItems.isEmpty()) {
                                selectedChipItems.add(label)
                            } else {
                                selectedChipItems.clear()
                                selectedChipItems.add(label)
                            }
                        }
                    },
                    label = {
                        Text(label)
                    },
                    colors = if (chipGroupTrait.elevated) {
                        FilterChipDefaults.elevatedFilterChipColors()
                    } else {
                        FilterChipDefaults.filterChipColors()
                    },
                    elevation = if (chipGroupTrait.elevated) {
                        FilterChipDefaults.elevatedFilterChipElevation()
                    } else {
                        FilterChipDefaults.filterChipElevation()
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            } else {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(label)
                    },
                    colors = if (chipGroupTrait.elevated) {
                        AssistChipDefaults.elevatedAssistChipColors()
                    } else {
                        AssistChipDefaults.assistChipColors()
                    },
                    elevation = if (chipGroupTrait.elevated) {
                        AssistChipDefaults.elevatedAssistChipElevation()
                    } else {
                        AssistChipDefaults.elevatedAssistChipElevation()
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        val chipItems = when (val chipItems = chipItems) {
            is CustomEnumValuesProperty -> {
                chipItems.rawVale(project)
            }

            null -> listOf("[Empty item]")
            else -> listOf(chipItems.transformedValueExpression(project))
        }
        if (wrapContent) {
            FlowRow(
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
            ) {
                chipItems.forEachIndexed { _, item ->
                    LocalChip(
                        label = item,
                        chipGroupTrait = node.trait.value as ChipGroupTrait,
                    )
                }
            }
        } else {
            LazyRow(
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
            ) {
                chipItems.forEachIndexed { _, item ->
                    item {
                        LocalChip(
                            label = item,
                            chipGroupTrait = node.trait.value as ChipGroupTrait,
                        )
                    }
                }
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
        val chipGroupTrait = node.trait.value as ChipGroupTrait

        val companionState = node.companionStateId?.let {
            project.findLocalStateOrNull(it)
        }
        val updatedState = if (companionState is ScreenState.StringListScreenState) {
            companionState.copy(singleValueOnly = !multiSelect)
        } else {
            companionState
        }
        val canvasEditable = project.findCanvasEditableHavingNodeOrNull(node)!!
        (updatedState as? WriteableState)?.let {
            canvasEditable.updateState(updatedState)
        }

        val itemsCodeBlock = when (val items = chipItems) {
            null -> {
                CodeBlock.of("emptyList<String>()")
            }

            else -> {
                items.transformedCodeBlock(project, context, dryRun = dryRun)
            }
        }

        fun generateChipCode(
            groupParams: ChipGroupTrait,
            builder: CodeBlock.Builder,
            dryRun: Boolean,
        ) {
            val chipMember = if (groupParams.selectable) {
                MemberName("androidx.compose.material3", "FilterChip")
            } else {
                MemberName("androidx.compose.material3", "AssistChip")
            }
            builder.add("%M(", chipMember)
            if (updatedState is WriteableState) {
                builder.addStatement("selected = ${updatedState.getReadVariableName(project)}.contains(it),")
            } else {
                builder.addStatement("selected = false,")
            }
            builder.addStatement("label = { %M(it) },", MemberHolder.Material3.Text)
            builder.addStatement("onClick = {")
            if (node.actionsMap[ActionType.OnChange]?.isNotEmpty() == true) {
                node.actionsMap[ActionType.OnChange]?.forEach {
                    builder.add(it.generateCodeBlock(project, context, dryRun = dryRun))
                }
            }
            if (updatedState is WriteableState) {
                builder.add(
                    updatedState.generateWriteBlock(
                        project,
                        canvasEditable,
                        context,
                        dryRun = dryRun
                    )
                )
            }
            builder.addStatement("},")
            listOf(ModifierWrapper.Padding(4.dp)).generateCode(
                project,
                context,
                builder,
                dryRun = dryRun
            )
            builder.add(")")
        }

        if (wrapContent) {
            codeBlockBuilder.addStatement(
                "%M(",
                MemberName("androidx.compose.foundation.layout", "FlowRow"),
            )
            codeBlockBuilder.addStatement(") {")
            codeBlockBuilder.add(
                node.generateModifierCode(project, context, dryRun = dryRun)
            )

            codeBlockBuilder.add(itemsCodeBlock)
            codeBlockBuilder.addStatement(".forEach {")
            generateChipCode(groupParams = chipGroupTrait, codeBlockBuilder, dryRun = dryRun)
            codeBlockBuilder.addStatement("}")

            codeBlockBuilder.addStatement("}")
        } else {
            codeBlockBuilder.addStatement(
                "%M(",
                MemberName("androidx.compose.foundation.lazy", "LazyRow"),
            )
            codeBlockBuilder.add(
                node.generateModifierCode(project, context, dryRun = dryRun)
            )
            codeBlockBuilder.addStatement(") {")
            codeBlockBuilder.add(CodeBlock.of("%M(", MemberHolder.AndroidX.Lazy.items))
            codeBlockBuilder.add(itemsCodeBlock)
            codeBlockBuilder.addStatement(") { ")

            generateChipCode(groupParams = chipGroupTrait, codeBlockBuilder, dryRun = dryRun)

            codeBlockBuilder.addStatement("}")
        }
        codeBlockBuilder.addStatement("}")
        return codeBlockBuilder.build()
    }
}
