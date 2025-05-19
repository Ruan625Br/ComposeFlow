package io.composeflow.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InformationDialog(
    confirmText: String,
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
            }) {
                Text(confirmText)
            }
        },
        title = title?.let {
            {
                Text(it)
            }
        },
        text = message?.let {
            {
                Text(it)
            }
        },
        modifier = modifier,
    )
}