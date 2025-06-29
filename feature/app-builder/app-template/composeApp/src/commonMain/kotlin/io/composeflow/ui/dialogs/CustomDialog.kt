package io.composeflow.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {},
        text = {
            content()
        },
        modifier = modifier,
    )
}
