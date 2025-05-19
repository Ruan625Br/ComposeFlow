package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.ChipGroupTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun ChipGroupParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val chipGroupTrait = node.trait.value as ChipGroupTrait
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty = chipGroupTrait.chipItems,
            acceptableType = ComposeFlowType.StringType(isList = true),
            label = "items",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, chipGroupTrait.copy(chipItems = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(node, chipGroupTrait.copy(chipItems = null))
            },
            modifier = Modifier.hoverOverlay(),
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.FormatListNumbered,
                    contentDescription = ""
                )
            },
            variableAssignable = true,
        )

        if (chipGroupTrait.selectable) {
            BooleanPropertyEditor(
                checked = chipGroupTrait.multiSelect,
                label = "Multi select",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(node, chipGroupTrait.copy(multiSelect = it))
                },
                modifier = Modifier.hoverOverlay(),
            )
        }
        BooleanPropertyEditor(
            checked = chipGroupTrait.elevated,
            label = "Elevated",
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, chipGroupTrait.copy(elevated = it))
            },
            modifier = Modifier.hoverOverlay(),
        )
        BooleanPropertyEditor(
            checked = chipGroupTrait.wrapContent,
            label = "Wrap content",
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, chipGroupTrait.copy(wrapContent = it))
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
