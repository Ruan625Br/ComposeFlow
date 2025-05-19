package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.SwitchTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.BooleanProperty
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor

@Composable
fun SwitchParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val switchTrait = node.trait.value as SwitchTrait
    Column {
        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = switchTrait.checked,
            label = "Checked",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, switchTrait.copy(checked = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    switchTrait.copy(checked = BooleanProperty.BooleanIntrinsicValue())
                )
            },
            modifier = Modifier.hoverOverlay(),
        )

        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = switchTrait.enabled,
            label = "Enabled",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, switchTrait.copy(enabled = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    switchTrait.copy(enabled = BooleanProperty.BooleanIntrinsicValue(true))
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
