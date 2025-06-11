package io.composeflow.ui.popup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.cancel
import io.composeflow.delete
import io.composeflow.ui.common.ComposeFlowTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SimpleConfirmationDialog(
    text: String,
    onCloseClick: () -> Unit,
    onConfirmClick: () -> Unit,
    positiveText: String = stringResource(Res.string.delete),
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    positiveButtonColor: Color = MaterialTheme.colorScheme.error,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        Surface(color = surfaceColor) {
            Column(
                modifier = Modifier
                    .size(width = 300.dp, height = 160.dp)
                    .padding(16.dp),
            ) {
                Text(text = text)

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier = Modifier
                            .padding(end = 16.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirmClick()
                        },
                    ) {
                        Text(
                            text = positiveText,
                            color = positiveButtonColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemedSimpleConfirmationDialogPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        SimpleConfirmationDialog(
            text = "Are you sure you want to delete this item?",
            onCloseClick = {},
            onConfirmClick = {}
        )
    }
}

@Preview
@Composable
fun SimpleConfirmationDialogPreview_Light() {
    ThemedSimpleConfirmationDialogPreview(useDarkTheme = false)
}

@Preview
@Composable
fun SimpleConfirmationDialogPreview_Dark() {
    ThemedSimpleConfirmationDialogPreview(useDarkTheme = true)
}

@Composable
private fun ThemedSimpleConfirmationDialogCustomPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        SimpleConfirmationDialog(
            text = "Do you want to save your changes before closing?",
            onCloseClick = {},
            onConfirmClick = {},
            positiveText = "Save",
            positiveButtonColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun SimpleConfirmationDialogCustomPreview_Light() {
    ThemedSimpleConfirmationDialogCustomPreview(useDarkTheme = false)
}

@Preview
@Composable
fun SimpleConfirmationDialogCustomPreview_Dark() {
    ThemedSimpleConfirmationDialogCustomPreview(useDarkTheme = true)
}
