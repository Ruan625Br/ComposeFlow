package io.composeflow.ui.inspector.modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.SizeValidator
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.propertyeditor.ShapePropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty

@Composable
fun ShadowModifierInspector(
    node: ComposeNode,
    wrapper: ModifierWrapper.Shadow,
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
            fun initialValue(dp: Dp) = if (dp == Dp.Unspecified) "" else dp.value.toInt().toString()

            BasicEditableTextProperty(
                initialValue = initialValue(wrapper.elevation),
                label = "Elevation",
                validateInput = SizeValidator()::validate,
                onValidValueChanged = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(elevation = it.toFloat().dp),
                    )
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .hoverOverlay(),
            )
            ShapePropertyEditor(
                initialShape = wrapper.shapeWrapper,
                onShapeUpdated = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(shapeWrapper = it)
                    )
                },
            )
        }
    }
}
