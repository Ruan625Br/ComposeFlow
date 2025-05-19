package io.composeflow.testing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width

/**
 * Drag to other SemanticsNodeInteraction
 */
context(DesktopComposeUiTest)
@OptIn(ExperimentalTestApi::class)
fun SemanticsNodeInteraction.dragTo(
    target: SemanticsNodeInteraction,
    targetPointXRatio: Float = 0.5F,
    targetPointYRatio: Float = 0.5F,
    isFinishDragging: () -> Boolean,
) {
    val fromBounds = getBoundsInRoot()
    val toBounds = target.getBoundsInRoot()
    onRoot().performTouchInput {
        val from = Offset(
            (fromBounds.left + fromBounds.width / 2).toPx(),
            (fromBounds.top + fromBounds.height / 2).toPx(),
        )
        down(
            from,
        )
        val destination = Offset(
            (toBounds.left + toBounds.width * targetPointXRatio).toPx(),
            (toBounds.top + toBounds.height * targetPointYRatio).toPx(),
        )
        moveTo(
            destination,
        )
        up()
        // We need to move the mouse cursor to cancel hovering.
        down(androidx.compose.ui.geometry.Offset.Infinite)
        up()
    }
    // Wait for the dragged node to be attached to the target node
    waitUntil {
        isFinishDragging()
    }
}

fun SemanticsNodeInteraction.exists(): Boolean {
    return try {
        fetchSemanticsNode()
        // Found the node
        true
    } catch (e: AssertionError) {
        false
    }
}
