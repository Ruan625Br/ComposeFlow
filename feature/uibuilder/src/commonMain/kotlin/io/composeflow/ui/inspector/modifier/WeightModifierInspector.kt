package io.composeflow.ui.inspector.modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.NotEmptyIntValidator
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun WeightModifierInspector(
    node: ComposeNode,
    wrapper: ModifierWrapper.Weight,
    modifierIndex: Int,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onVisibilityToggleClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModifierInspectorContainer(
        node = node,
        wrapper = wrapper,
        modifierIndex = modifierIndex,
        composeNodeCallbacks = composeNodeCallbacks,
        onVisibilityToggleClicked = onVisibilityToggleClicked,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(start = 36.dp)) {
            fun initialValue(weight: Float) = weight.toInt().toString()

            BasicEditableTextProperty(
                initialValue = initialValue(wrapper.weight),
                label = "Weight",
                validateInput = NotEmptyIntValidator()::validate,
                onValidValueChanged = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(weight = it.toFloat()),
                    )
                },
            )

            BooleanPropertyEditor(
                checked = wrapper.fill,
                label = "Fill",
                onCheckedChange = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(fill = it),
                    )
                },
            )
        }
    }
}
