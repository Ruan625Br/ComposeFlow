package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.ButtonTrait
import io.composeflow.model.parameter.ButtonType
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.StringProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.inspector.propertyeditor.ShapePropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import kotlinx.coroutines.launch

@Composable
fun ButtonParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val buttonTrait = node.trait.value as ButtonTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            label = "Text",
            initialProperty = buttonTrait.textProperty,
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    buttonTrait.copy(textProperty = StringProperty.StringIntrinsicValue("")),
                )
            },
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    buttonTrait.copy(textProperty = property),
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            modifier = Modifier.hoverOverlay(),
            singleLine = true,
        )
        IconPropertyEditor(
            label = "Icon",
            onIconSelected = {
                composeNodeCallbacks.onTraitUpdated(node, buttonTrait.copy(imageVectorHolder = it))
            },
            onIconDeleted = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    buttonTrait.copy(imageVectorHolder = null)
                )
            },
            currentIcon = buttonTrait.imageVectorHolder?.imageVector,
            modifier = Modifier
                .hoverOverlay(),
        )

        AssignableBooleanPropertyEditor(
            project = project,
            node = node,
            initialProperty = buttonTrait.enabled,
            acceptableType = ComposeFlowType.BooleanType(),
            label = "Enabled",
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onTraitUpdated(node, buttonTrait.copy(enabled = property))
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(node, buttonTrait.copy(enabled = null))
            },
            modifier = Modifier.hoverOverlay(),
        )

        ShapePropertyEditor(
            initialShape = buttonTrait.shapeWrapper,
            onShapeUpdated = {
                composeNodeCallbacks.onTraitUpdated(node, buttonTrait.copy(shapeWrapper = it))
            },
        )
        BasicDropdownPropertyEditor(
            items = ButtonType.entries,
            label = "Button type",
            selectedIndex = buttonTrait.buttonType.ordinal,
            onValueChanged = { _, item ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    buttonTrait.copy(
                        buttonType = item,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay()
        )
    }
}
