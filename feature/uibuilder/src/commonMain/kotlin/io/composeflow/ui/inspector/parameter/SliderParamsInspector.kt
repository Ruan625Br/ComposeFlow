package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.editor.validator.FloatValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.model.parameter.SliderTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.FloatProperty
import io.composeflow.model.property.IntProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor

@Composable
fun SliderParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val sliderTrait = node.trait.value as SliderTrait
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty = sliderTrait.value,
            acceptableType = ComposeFlowType.FloatType(),
            label = "Value",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, sliderTrait.copy(value = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    sliderTrait.copy(value = FloatProperty.FloatIntrinsicValue())
                )
            },
            validateInput = FloatValidator(
                maxValue = 1.0f,
                minValue = 0.0f,
            )::validate,
            modifier = Modifier.hoverOverlay(),
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty = sliderTrait.steps,
            acceptableType = ComposeFlowType.IntType(),
            label = "Steps",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, sliderTrait.copy(steps = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    sliderTrait.copy(steps = IntProperty.IntIntrinsicValue())
                )
            },
            validateInput = IntValidator(
                maxValue = 40,
                minValue = 0,
            )::validate,
            modifier = Modifier.hoverOverlay(),
        )

        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = sliderTrait.enabled,
            label = "Enabled",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, sliderTrait.copy(enabled = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    sliderTrait.copy(enabled = BooleanProperty.BooleanIntrinsicValue())
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
