package io.composeflow.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.hoverIconClickable(): Modifier = composed {
    var isHovered by remember { mutableStateOf(false) }

    onPointerEvent(PointerEventType.Enter) {
        isHovered = true
    }.onPointerEvent(PointerEventType.Exit) {
        isHovered = false
    }.then(
        if (isHovered) {
            pointerHoverIcon(PointerIcon.Hand)
        } else {
            this
        },
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.hoverOverlay(
    overlayColor: Color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
    clipRadius: Dp = 16.dp,
): Modifier = composed {
    var isHovered by remember { mutableStateOf(false) }

    onPointerEvent(PointerEventType.Enter) {
        isHovered = true
    }.onPointerEvent(PointerEventType.Exit) {
        isHovered = false
    }
        .then(
            if (isHovered) {
                clip(shape = RoundedCornerShape(clipRadius)).background(overlayColor)
            } else {
                this
            },
        ).then(Modifier.padding(horizontal = 4.dp))
}

@Composable
fun Modifier.moveFocusOnTab(
    focusManager: FocusManager = LocalFocusManager.current,
) = onPreviewKeyEvent {
    if (it.type == KeyEventType.KeyDown && it.key == Key.Tab) {
        focusManager.moveFocus(
            if (it.isShiftPressed) {
                FocusDirection.Previous
            } else {
                FocusDirection.Next
            },
        )
        true
    } else {
        false
    }
}

@Composable
fun Modifier.backgroundContainerNeutral(): Modifier {
    return this.then(
        Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    )
}
