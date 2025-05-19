package io.composeflow.ui.inspector.modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.inspector.propertyeditor.ShapePropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableColorPropertyEditor

@Composable
fun BackgroundModifierInspector(
    project: Project,
    node: ComposeNode,
    wrapper: ModifierWrapper.Background,
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
            AssignableColorPropertyEditor(
                project = project,
                node = node,
                label = "Color",
                acceptableType = ComposeFlowType.Color(),
                initialProperty = wrapper.colorWrapper,
                onValidPropertyChanged = { property, _ ->
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(colorWrapper = property),
                    )
                },
                onInitializeProperty = {
                    composeNodeCallbacks.onModifierUpdatedAt(
                        node,
                        modifierIndex,
                        wrapper.copy(colorWrapper = wrapper.defaultColorProperty()),
                    )
                },
                modifier = Modifier
                    .hoverOverlay()
                    .fillMaxWidth(),
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
