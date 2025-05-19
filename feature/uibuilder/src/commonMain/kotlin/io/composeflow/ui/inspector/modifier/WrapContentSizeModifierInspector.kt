package io.composeflow.ui.inspector.modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.propertyeditor.AlignmentPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun WrapContentSizeModifierInspector(
    node: ComposeNode,
    wrapper: ModifierWrapper.WrapContentSize,
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
            AlignmentPropertyEditor(
                initialValue = wrapper.align,
                onAlignmentSelected = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(align = it),
                    )
                },
                label = "Alignment",
            )

            BooleanPropertyEditor(
                checked = wrapper.unbounded ?: false,
                label = "Unbounded",
                onCheckedChange = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(unbounded = it),
                    )
                },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
