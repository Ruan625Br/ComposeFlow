package io.composeflow.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ConfirmationDialog(
    positiveText: String,
    negativeText: String,
    positiveBlock: () -> Unit,
    negativeBlock: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            OutlinedButton(onClick = {
                onDismissRequest()
                positiveBlock()
            }) {
                Text(positiveText)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest()
                negativeBlock()
            }) {
                Text(negativeText)
            }
        },
        title =
            title?.let {
                {
                    Text(it)
                }
            },
        text =
            message?.let {
                {
                    Text(it)
                }
            },
        modifier = modifier,
    )
}
