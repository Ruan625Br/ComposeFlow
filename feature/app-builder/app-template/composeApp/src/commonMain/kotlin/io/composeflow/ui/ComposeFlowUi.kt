package io.composeflow.ui

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalOnShowSnackbar =
    compositionLocalOf<suspend (String, String?) -> Boolean> { error("No onShowSnackbar callback provided") }

@Composable
fun ProvideOnShowSnackbar(
    snackbarHostState: SnackbarHostState,
    content: @Composable () -> Unit,
) {
    val onShowSnackbar: (suspend (String, String?) -> Boolean) = { message, action ->
        snackbarHostState.showSnackbar(
            message = message,
            actionLabel = action,
            duration = SnackbarDuration.Short,
        ) == SnackbarResult.ActionPerformed
    }
    CompositionLocalProvider(LocalOnShowSnackbar provides onShowSnackbar) {
        content()
    }
}
