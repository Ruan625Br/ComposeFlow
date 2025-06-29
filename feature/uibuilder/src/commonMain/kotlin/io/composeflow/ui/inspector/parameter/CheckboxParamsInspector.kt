package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.CheckboxTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.BooleanProperty
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor

@Composable
fun CheckboxParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val checkboxTrait = node.trait.value as CheckboxTrait
    Column {
        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = checkboxTrait.checked,
            label = "Checked",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, checkboxTrait.copy(checked = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    checkboxTrait.copy(checked = BooleanProperty.BooleanIntrinsicValue()),
                )
            },
            modifier = Modifier.hoverOverlay(),
        )

        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = checkboxTrait.enabled,
            label = "Enabled",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, checkboxTrait.copy(enabled = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    checkboxTrait.copy(enabled = BooleanProperty.BooleanIntrinsicValue(true)),
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
