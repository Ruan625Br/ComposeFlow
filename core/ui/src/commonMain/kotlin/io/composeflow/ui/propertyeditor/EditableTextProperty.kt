package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.ui.textfield.SmallOutlinedTextField

@Composable
fun BasicEditableTextProperty(
    initialValue: String,
    onValidValueChanged: (String) -> Unit,
    enabled: Boolean = true,
    label: String = "",
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    validateInput: (String) -> ValidateResult = { ValidateResult.Success },
    singleLine: Boolean = true,
    valueSetFromVariable: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    EditableTextProperty(
        initialValue = initialValue,
        onValidValueChanged = onValidValueChanged,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        validateInput = validateInput,
        singleLine = singleLine,
        valueSetFromVariable = valueSetFromVariable,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}

@Composable
fun EditableTextProperty(
    initialValue: String,
    onValidValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String = "",
    placeholder: String? = null,
    validateInput: (String) -> ValidateResult = { ValidateResult.Success },
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    valueSetFromVariable: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    var value by remember(initialValue) { mutableStateOf(initialValue) }
    var errorMessage by remember(supportingText) { mutableStateOf(supportingText) }

    SmallOutlinedTextField(
        value = value,
        onValueChange = {
            value = it
            when (val validateResult = validateInput(it)) {
                ValidateResult.Success -> {
                    errorMessage = null
                    onValidValueChanged(it)
                }

                is ValidateResult.Failure -> {
                    errorMessage = validateResult.message
                }
            }
        },
        enabled = enabled,
        placeholder = {
            placeholder?.let {
                Text(
                    text = it,
                    modifier = Modifier.alpha(0.5f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        },
        label =
            if (label.isNotEmpty()) {
                {
                    Text(
                        text = label,
                        modifier = Modifier.alpha(0.7f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            } else {
                null
            },
        textStyle =
            if (errorMessage != null) {
                MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
            } else if (valueSetFromVariable) {
                MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.tertiary)
            } else {
                MaterialTheme.typography.bodyMedium
            },
        colors =
            OutlinedTextFieldDefaults.colors().copy(
                disabledSupportingTextColor = MaterialTheme.colorScheme.error,
            ),
        isError = errorMessage != null,
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        supportingText = { errorMessage?.let { Text(it) } },
        modifier = modifier,
        leadingIcon = leadingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}
