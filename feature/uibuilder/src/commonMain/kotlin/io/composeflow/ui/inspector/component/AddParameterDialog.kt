package io.composeflow.ui.inspector.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component4
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_parameter
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.data_type
import io.composeflow.default_value
import io.composeflow.editor.validator.FloatValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.editor.validator.KotlinVariableNameValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.is_list
import io.composeflow.model.project.ParameterWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.copy
import io.composeflow.parameter_name
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.textfield.SmallOutlinedTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddParameterDialog(
    project: Project,
    onDialogClosed: () -> Unit,
    onParameterConfirmed: (ParameterWrapper<*>) -> Unit,
    modifier: Modifier = Modifier,
    availableParameters: List<ParameterWrapper<*>> = ParameterWrapper.entries(),
    listTypeAllowed: Boolean = true,
    initialParameter: ParameterWrapper<*>? = null,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onDialogClosed()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onDialogClosed()
                true
            } else {
                false
            }
        },
    ) {
        val (first, second, third, fourth) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        var editedParameterWrapper by remember {
            mutableStateOf<ParameterWrapper<*>>(
                initialParameter ?: ParameterWrapper.StringParameter(name = ""),
            )
        }
        val onConfirmParameter = {
            onParameterConfirmed(editedParameterWrapper)
            onDialogClosed()
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

                var parameterNameValidateResult by remember {
                    mutableStateOf(KotlinVariableNameValidator().validate(editedParameterWrapper.variableName))
                }
                SmallOutlinedTextField(
                    value = editedParameterWrapper.variableName,
                    onValueChange = {
                        editedParameterWrapper = editedParameterWrapper.copy(newName = it)
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
                var dropDownSelectedIndex by remember {
                    mutableStateOf(
                        initialParameter?.let {
                            availableParameters.indexOfFirst {
                                it.parameterType == initialParameter.parameterType
                            }
                        } ?: 0,
                    )
                }

                BasicDropdownPropertyEditor(
                    project = project,
                    items =
                        availableParameters
                            .map {
                                it.parameterType.displayName(
                                    project = project,
                                    listAware = false,
                                )
                            },
                    onValueChanged = { index, _ ->
                        val newType = availableParameters[index].parameterType
                        editedParameterWrapper =
                            editedParameterWrapper.copy(
                                newType = newType,
                            )
                        dropDownSelectedIndex = index
                    },
                    selectedIndex = dropDownSelectedIndex,
                    label = stringResource(Res.string.data_type),
                    modifier =
                        Modifier
                            .padding(top = 8.dp)
                            .focusRequester(second)
                            .moveFocusOnTab(),
                )

                if (listTypeAllowed) {
                    BooleanPropertyEditor(
                        checked = editedParameterWrapper.parameterType.isList,
                        onCheckedChange = {
                            val newType =
                                editedParameterWrapper.parameterType.copyWith(newIsList = it)
                            editedParameterWrapper =
                                editedParameterWrapper.copy(
                                    newType = newType,
                                )
                        },
                        label = stringResource(Res.string.is_list),
                    )
                }

                var defaultValueValidateResult by remember {
                    mutableStateOf<ValidateResult>(
                        ValidateResult.Success,
                    )
                }
                val isFormValid by remember {
                    derivedStateOf {
                        defaultValueValidateResult is ValidateResult.Success &&
                            parameterNameValidateResult is ValidateResult.Success
                    }
                }
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    when (val parameter = editedParameterWrapper) {
                        is ParameterWrapper.StringParameter -> {
                            SmallOutlinedTextField(
                                value = parameter.defaultValue,
                                onValueChange = {
                                    editedParameterWrapper =
                                        parameter.copy(
                                            defaultValue = it,
                                        )
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
                                        .focusRequester(third)
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
                        }

                        is ParameterWrapper.BooleanParameter -> {
                            BooleanPropertyEditor(
                                checked = parameter.defaultValue,
                                onCheckedChange = {
                                    editedParameterWrapper =
                                        parameter.copy(
                                            newDefaultValue = it,
                                        )
                                },
                                label = stringResource(Res.string.default_value),
                                modifier =
                                    Modifier
                                        .focusRequester(third)
                                        .moveFocusOnTab(),
                            )
                        }

                        is ParameterWrapper.IntParameter -> {
                            var value by remember { mutableStateOf(parameter.defaultValue.toString()) }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        IntValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            editedParameterWrapper =
                                                parameter.copy(
                                                    defaultValue = it.toInt(),
                                                )
                                        }

                                        is ValidateResult.Failure -> {}
                                    }
                                    defaultValueValidateResult = validateResult
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                supportingText =
                                    (defaultValueValidateResult as? ValidateResult.Failure)?.let {
                                        {
                                            Text(it.message)
                                        }
                                    },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueValidateResult is ValidateResult.Failure,
                                modifier =
                                    Modifier
                                        .focusRequester(third)
                                        .moveFocusOnTab()
                                        .width(width = fieldWidth)
                                        .onKeyEvent {
                                            if (it.key == Key.Enter && isFormValid) {
                                                onParameterConfirmed(editedParameterWrapper)
                                                true
                                            } else {
                                                false
                                            }
                                        },
                            )
                        }

                        is ParameterWrapper.FloatParameter -> {
                            var value by remember { mutableStateOf(parameter.defaultValue.toString()) }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        FloatValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            editedParameterWrapper =
                                                parameter.copy(
                                                    defaultValue = it.toFloat(),
                                                )
                                        }

                                        is ValidateResult.Failure -> {}
                                    }
                                    defaultValueValidateResult = validateResult
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                supportingText =
                                    (defaultValueValidateResult as? ValidateResult.Failure)?.let {
                                        {
                                            Text(it.message)
                                        }
                                    },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueValidateResult is ValidateResult.Failure,
                                modifier =
                                    Modifier
                                        .focusRequester(third)
                                        .moveFocusOnTab()
                                        .width(width = fieldWidth)
                                        .onKeyEvent {
                                            if (it.key == Key.Enter && isFormValid) {
                                                onParameterConfirmed(editedParameterWrapper)
                                                true
                                            } else {
                                                false
                                            }
                                        },
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onDialogClosed()
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
                                .focusRequester(fourth),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
