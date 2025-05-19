package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.DropdownTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.StringProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor

@Composable
fun DropdownParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val dropdownTrait = node.trait.value as DropdownTrait
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty = dropdownTrait.items,
            acceptableType = ComposeFlowType.StringType(isList = true),
            label = "items",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, dropdownTrait.copy(items = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(node, dropdownTrait.copy(items = null))
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

        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty = dropdownTrait.label,
            acceptableType = ComposeFlowType.StringType(),
            label = "label",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, dropdownTrait.copy(label = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    dropdownTrait.copy(label = StringProperty.StringIntrinsicValue())
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
