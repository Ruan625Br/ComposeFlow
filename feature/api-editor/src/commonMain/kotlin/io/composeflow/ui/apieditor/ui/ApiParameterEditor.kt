package io.composeflow.ui.apieditor.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_parameter
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.default_value
import io.composeflow.editor.validator.KotlinVariableNameValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.invalid_api_parameter_reference
import io.composeflow.model.apieditor.ApiProperty
import io.composeflow.parameter_name
import io.composeflow.remove_parameter
import io.composeflow.select_parameter
import io.composeflow.set_from_parameter
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.propertyeditor.EditableTextProperty
import io.composeflow.ui.textfield.SmallOutlinedTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApiParameterEditor(
    initialProperty: ApiProperty? = null,
    parameters: List<ApiProperty.StringParameter>,
    onApiPropertyChanged: (ApiProperty) -> Unit,
    onAddApiParameter: (ApiProperty.StringParameter) -> Unit,
    onRemoveParameterAt: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String? = null,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    val textFieldEnabled =
        (initialProperty is ApiProperty.IntrinsicValue || initialProperty == null)
    val errorText =
        if (initialProperty is ApiProperty.StringParameter) {
            if (initialProperty !in parameters) {
                stringResource(Res.string.invalid_api_parameter_reference)
            } else {
                null
            }
        } else {
            null
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        EditableTextProperty(
            enabled = textFieldEnabled,
            onValidValueChanged = {
                onApiPropertyChanged(ApiProperty.IntrinsicValue(it))
            },
            initialValue =
                when (initialProperty) {
                    is ApiProperty.IntrinsicValue -> initialProperty.value
                    is ApiProperty.StringParameter -> "[param: ${initialProperty.name}]"
                    null -> "[Unknown]"
                },
            label = label,
            placeholder = placeholder,
            singleLine = true,
            modifier = Modifier.weight(1f),
            supportingText = errorText,
            valueSetFromVariable = initialProperty !is ApiProperty.IntrinsicValue,
        )

        val setFromVariable = stringResource(Res.string.set_from_parameter)
        Tooltip(setFromVariable) {
            ComposeFlowIconButton(
                onClick = {
                    dialogOpen = true
                },
                modifier = Modifier.padding(bottom = 12.dp),
            ) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.ElectricalServices,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    contentDescription = stringResource(Res.string.set_from_parameter),
                )
            }
        }
    }

    if (dialogOpen) {
        onAnyDialogIsShown()
        val closeDialog = {
            dialogOpen = false
            onAllDialogsClosed()
        }
        SelectApiParameterDialog(
            initialProperty = initialProperty,
            parameters = parameters,
            onAddApiParameter = onAddApiParameter,
            onApiParameterSelected = {
                onApiPropertyChanged(it)
            },
            onRemoveParameterAt = onRemoveParameterAt,
            onCloseDialog = closeDialog,
        )
    }
}

@Composable
private fun SelectApiParameterDialog(
    initialProperty: ApiProperty? = null,
    parameters: List<ApiProperty.StringParameter>,
    onApiParameterSelected: (ApiProperty.StringParameter) -> Unit,
    onAddApiParameter: (ApiProperty.StringParameter) -> Unit,
    onRemoveParameterAt: (Int) -> Unit,
    onCloseDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PositionCustomizablePopup(
        expanded = true,
        onDismissRequest = {
            onCloseDialog()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseDialog()
                true
            } else {
                false
            }
        },
    ) {
        var openAddParameterDialog by remember { mutableStateOf(false) }
        var parameterToBeRemovedIndex by remember { mutableStateOf<Int?>(null) }

        Surface(modifier = modifier.size(width = 480.dp, height = 520.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(Res.string.select_parameter),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp),
                )

                TextButton(
                    onClick = {
                        openAddParameterDialog = true
                    },
                ) {
                    Text("+ ${stringResource(Res.string.add_parameter)}")
                }
                LazyColumn {
                    itemsIndexed(parameters) { paramIndex, parameter ->
                        val selectedModifier =
                            if (initialProperty is ApiProperty.StringParameter &&
                                initialProperty.parameterId == parameter.parameterId
                            ) {
                                Modifier
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(8.dp),
                                    ).padding(2.dp)
                            } else {
                                Modifier
                            }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .hoverIconClickable()
                                    .hoverOverlay()
                                    .padding(vertical = 8.dp)
                                    .padding(start = 8.dp)
                                    .clickable {
                                        onApiParameterSelected(parameter)
                                        onCloseDialog()
                                    }.then(selectedModifier),
                        ) {
                            Text(
                                text = parameter.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )

                            Text(
                                text = "String",
                                color = MaterialTheme.colorScheme.tertiary,
                                style = MaterialTheme.typography.bodyMedium,
                            )

                            Text(
                                text = "default: ${parameter.defaultValue}",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp),
                            )

                            Spacer(Modifier.weight(1f))

                            val removeParameter = stringResource(Res.string.remove_parameter)
                            Tooltip(removeParameter) {
                                ComposeFlowIconButton(
                                    onClick = {
                                        parameterToBeRemovedIndex = paramIndex
                                    },
                                ) {
                                    ComposeFlowIcon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = removeParameter,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                HorizontalDivider()
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    TextButton(
                        onClick = {
                            onCloseDialog()
                        },
                        modifier =
                            Modifier
                                .padding(end = 16.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        }

        if (openAddParameterDialog) {
            AddApiParameterDialog(
                initialProperty = initialProperty,
                onParameterConfirmed = {
                    onAddApiParameter(it)
                },
                onCloseDialog = {
                    openAddParameterDialog = false
                },
            )
        }

        parameterToBeRemovedIndex?.let { removedIndexAt ->
            val parameter = parameters[removedIndexAt]
            SimpleConfirmationDialog(
                text = "Remove ${parameter.name} ?",
                onCloseClick = {
                    parameterToBeRemovedIndex = null
                },
                onConfirmClick = {
                    parameterToBeRemovedIndex = null
                    onRemoveParameterAt(removedIndexAt)
                },
                positiveText = "Remove",
            )
        }
    }
}

@Composable
fun AddApiParameterDialog(
    onParameterConfirmed: (ApiProperty.StringParameter) -> Unit,
    onCloseDialog: () -> Unit,
    modifier: Modifier = Modifier,
    initialProperty: ApiProperty? = null,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseDialog()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseDialog()
                true
            } else {
                false
            }
        },
    ) {
        val (first, second, third) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        var editedParameter by remember {
            mutableStateOf(
                initialProperty as? ApiProperty.StringParameter
                    ?: ApiProperty.StringParameter(name = ""),
            )
        }

        val onConfirmParameter = {
            onParameterConfirmed(editedParameter)
            onCloseDialog()
        }

        var parameterNameValidateResult by remember {
            mutableStateOf(KotlinVariableNameValidator().validate(editedParameter.variableName))
        }
        val isFormValid by remember {
            derivedStateOf { parameterNameValidateResult is ValidateResult.Success }
        }
        Surface {
            Column(
                modifier =
                    modifier
                        .size(width = 320.dp, height = 360.dp)
                        .padding(16.dp),
            ) {
                val fieldWidth = 300.dp
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.add_parameter),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }

                SmallOutlinedTextField(
                    value = editedParameter.variableName,
                    onValueChange = {
                        editedParameter = editedParameter.copy(name = it)
                        parameterNameValidateResult = KotlinVariableNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.parameter_name),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.6f),
                        )
                    },
                    singleLine = true,
                    isError = parameterNameValidateResult is ValidateResult.Failure,
                    supportingText =
                        (parameterNameValidateResult as? ValidateResult.Failure)?.let {
                            {
                                Text(it.message)
                            }
                        },
                    shape = RoundedCornerShape(8.dp),
                    modifier =
                        Modifier
                            .focusRequester(first)
                            .moveFocusOnTab()
                            .width(width = fieldWidth)
                            .onKeyEvent {
                                if (it.key == Key.Enter && parameterNameValidateResult is ValidateResult.Success) {
                                    onConfirmParameter()
                                    true
                                } else {
                                    false
                                }
                            },
                )

                SmallOutlinedTextField(
                    value = editedParameter.defaultValue,
                    onValueChange = {
                        editedParameter = editedParameter.copy(defaultValue = it)
                    },
                    label = {
                        Text(
                            stringResource(Res.string.default_value),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.6f),
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier =
                        Modifier
                            .focusRequester(second)
                            .moveFocusOnTab()
                            .width(width = fieldWidth)
                            .onKeyEvent {
                                if (it.key == Key.Enter && isFormValid) {
                                    onConfirmParameter()
                                    true
                                } else {
                                    false
                                }
                            },
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onCloseDialog()
                        },
                        modifier =
                            Modifier
                                .padding(end = 16.dp)
                                .focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirmParameter()
                        },
                        enabled = isFormValid,
                        modifier =
                            Modifier
                                .focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
