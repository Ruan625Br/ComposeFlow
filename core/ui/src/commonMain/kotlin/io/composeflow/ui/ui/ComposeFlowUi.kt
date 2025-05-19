package io.composeflow.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp

val minimumInteractiveComponentSize = 28.dp

val LocalOnAnyDialogIsShown =
    compositionLocalOf<() -> Unit> { error("No OnAnyDialogIsShown provided") }
val LocalOnAllDialogsClosed =
    compositionLocalOf<() -> Unit> { error("No OnAllDialogsClosed provided") }
val LocalOnShowSnackbar =
    compositionLocalOf<suspend (String, String?) -> Boolean> { error("No onShowSnackbar callback provided") }

@Composable
fun ProvideShowDialogCallback(
    onAnyDialogIsShown: () -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOnAnyDialogIsShown provides onAnyDialogIsShown) {
        content()
    }
}

@Composable
fun ProvideCloseDialogCallback(
    onAllDialogsClosed: () -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOnAllDialogsClosed provides onAllDialogsClosed) {
        content()
    }
}

@Composable
fun ProvideOnShowSnackbar(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOnShowSnackbar provides onShowSnackbar) {
        content()
    }
}
