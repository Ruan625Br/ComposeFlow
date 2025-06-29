package io.composeflow.ui.utils

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key

fun isPlusPressed(keyEvent: KeyEvent): Boolean {
    val isShiftPressed = keyEvent.isShiftPressed
    return keyEvent.key == Key.Plus ||
        (keyEvent.key == Key.Equals && isShiftPressed)
}
