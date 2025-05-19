package io.composeflow.ui.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.editor.validator.KotlinIdentifierValidator.MUST_NOT_BE_EMPTY
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.textfield.SmallOutlinedTextField
import org.jetbrains.compose.resources.stringResource

@Composable
fun SingleTextInputDialog(
    textLabel: String,
    onTextConfirmed: (String) -> Unit,
    onDismissDialog: () -> Unit,
    initialValue: String? = null,
    validator: ((String) -> ValidateResult)? = null,
) {
    var editedText by remember { mutableStateOf(initialValue ?: "") }
    PositionCustomizablePopup(
        onDismissRequest = {
            onDismissDialog()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onDismissDialog()
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
        var validateResult by remember {
            mutableStateOf<ValidateResult?>(
                validator?.let {
                    ValidateResult.Failure(
                        MUST_NOT_BE_EMPTY
                    )
                }
            )
        }
        val isFormValid by remember {
            derivedStateOf {
                validateResult == null || validateResult is ValidateResult.Success
            }
        }
        Surface {
            Column(
                modifier = Modifier
                    .size(width = 280.dp, height = 160.dp)
                    .padding(16.dp),
            ) {
                SmallOutlinedTextField(
                    value = editedText,
                    onValueChange = { changedValue ->
                        editedText = changedValue
                        validateResult = validator?.let {
                            it(changedValue)
                        }
                    },
                    label = {
                        Text(
                            textLabel,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.8f),
                        )
                    },
                    singleLine = true,
                    isError = !isFormValid,
                    supportingText = when (val failedResult = validateResult) {
                        is ValidateResult.Failure -> {
                            {
                                Text(failedResult.message)
                            }
                        }

                        else -> {
                            {
                            }
                        }
                    },
                    modifier = Modifier.focusRequester(first).moveFocusOnTab().onKeyEvent {
                        if (it.key == Key.Enter && editedText.isNotEmpty()) {
                            onTextConfirmed(editedText)
                            true
                        } else {
                            false
                        }
                    }
                        .fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onDismissDialog()
                        },
                        modifier = Modifier.padding(end = 16.dp).focusRequester(second),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onTextConfirmed(editedText)
                        },
                        enabled = isFormValid,
                        modifier = Modifier.focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
