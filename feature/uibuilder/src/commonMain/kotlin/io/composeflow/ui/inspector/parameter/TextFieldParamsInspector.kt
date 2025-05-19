package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.composeflow.Res
import io.composeflow.editor.validator.IntValidator
import io.composeflow.model.parameter.TextFieldTrait
import io.composeflow.model.parameter.TextFieldType
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.findLocalStateOrNull
import io.composeflow.model.property.StringProperty
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.password_hint_description
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.Tooltip
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.inspector.propertyeditor.ShapePropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextFieldParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val textFieldTrait = node.trait.value as TextFieldTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            label = "Value",
            initialProperty = textFieldTrait.value,
            onInitializeProperty = {
                val companionState = node.companionStateId?.let { project.findLocalStateOrNull(it) }
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    textFieldTrait.copy(
                        value = companionState?.let { ValueFromState(readFromStateId = it.id) }
                            ?: StringProperty.StringIntrinsicValue("")
                    ),
                )
            },
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    textFieldTrait.copy(value = property),
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            modifier = Modifier.hoverOverlay().fillMaxWidth(),
            singleLine = false,
        )

        Row {
            AssignableBooleanPropertyEditor(
                project = project,
                node = node,
                initialProperty = textFieldTrait.enabled,
                acceptableType = ComposeFlowType.BooleanType(),
                label = "Enabled",
                onValidPropertyChanged = { property, _ ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(enabled = property)
                    )
                },
                onInitializeProperty = {
                    composeNodeCallbacks.onTraitUpdated(node, textFieldTrait.copy(enabled = null))
                },
                modifier = Modifier.weight(1f).hoverOverlay(),
            )

            AssignableBooleanPropertyEditor(
                project = project,
                node = node,
                initialProperty = textFieldTrait.readOnly,
                acceptableType = ComposeFlowType.BooleanType(),
                label = "Read only",
                onValidPropertyChanged = { property, _ ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(readOnly = property)
                    )
                },
                onInitializeProperty = {
                    composeNodeCallbacks.onTraitUpdated(node, textFieldTrait.copy(readOnly = null))
                },
                modifier = Modifier.weight(1f).hoverOverlay(),
            )
        }

        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            label = "Label",
            initialProperty = textFieldTrait.label,
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    textFieldTrait.copy(label = null),
                )
            },
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    textFieldTrait.copy(label = property),
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            modifier = Modifier.hoverOverlay().fillMaxWidth(),
            singleLine = false,
        )
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            label = "Placeholder",
            initialProperty = textFieldTrait.placeholder,
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    textFieldTrait.copy(placeholder = null),
                )
            },
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    textFieldTrait.copy(placeholder = property),
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            modifier = Modifier.hoverOverlay().fillMaxWidth(),
            singleLine = false,
        )
        Row {
            IconPropertyEditor(
                label = "Leading icon",
                onIconSelected = {
                    composeNodeCallbacks.onTraitUpdated(node, textFieldTrait.copy(leadingIcon = it))
                },
                onIconDeleted = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(leadingIcon = null)
                    )
                },
                currentIcon = textFieldTrait.leadingIcon?.imageVector,
                modifier = Modifier
                    .weight(1f)
                    .hoverOverlay(),
            )
            IconPropertyEditor(
                label = "Trailing icon",
                onIconSelected = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(trailingIcon = it)
                    )
                },
                onIconDeleted = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(trailingIcon = null)
                    )
                },
                currentIcon = textFieldTrait.trailingIcon?.imageVector,
                modifier = Modifier
                    .weight(1f)
                    .hoverOverlay(),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            BooleanPropertyEditor(
                checked = textFieldTrait.singleLine == true,
                label = "Single line",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(node, textFieldTrait.copy(singleLine = it))
                },
                modifier = Modifier
                    .weight(1f)
                    .hoverOverlay(),
            )
            BasicEditableTextProperty(
                label = "Max lines",
                initialValue = textFieldTrait.maxLines?.toString() ?: "",
                validateInput = {
                    IntValidator(
                        allowLessThanZero = false,
                        maxValue = 999,
                    ).validate(
                        input = it,
                    )
                },
                onValidValueChanged = {
                    val value = if (it.isEmpty()) null else it.toInt()
                    composeNodeCallbacks.onTraitUpdated(node, textFieldTrait.copy(maxLines = value))
                },
                modifier = Modifier
                    .weight(1f)
                    .hoverOverlay(),
                singleLine = false,
            )
        }

        ShapePropertyEditor(
            initialShape = textFieldTrait.shapeWrapper,
            onShapeUpdated = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    textFieldTrait.copy(
                        shapeWrapper = it,
                    ),
                )
            },
        )
        Row {
            BasicDropdownPropertyEditor(
                items = TextFieldType.entries,
                label = "TextField type",
                selectedIndex = textFieldTrait.textFieldType.ordinal,
                onValueChanged = { _, item ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(
                            textFieldType = item,
                        ),
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .hoverOverlay()
            )

            if (textFieldTrait.textFieldType == TextFieldType.Default) {
                BooleanPropertyEditor(
                    checked = textFieldTrait.transparentIndicator ?: false,
                    label = "Transparent indicator",
                    onCheckedChange = {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            textFieldTrait.copy(transparentIndicator = it),
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .hoverOverlay(),
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
        }

        Tooltip(stringResource(Res.string.password_hint_description)) {
            BooleanPropertyEditor(
                checked = textFieldTrait.passwordField == true,
                label = "Password field",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(passwordField = it)
                    )
                },
                modifier = Modifier.hoverOverlay(),
            )
        }
    }
}
