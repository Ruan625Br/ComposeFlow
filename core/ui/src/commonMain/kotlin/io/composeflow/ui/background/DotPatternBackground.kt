package io.composeflow.ui.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp

@Composable
fun DotPatternBackground(
    dotRadius: Dp,
    dotMargin: Dp,
    dotColor: Color,
    modifier: Modifier = Modifier,
    onDrag: ((Offset) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var backgroundOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxSize()) {
        // A transparent layer for catching mouse events
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (onDrag == null) {
                                return@detectDragGestures
                            }
                            backgroundOffset += dragAmount
                            onDrag(dragAmount)
                        }
                    },
        )
        Canvas(modifier = modifier.fillMaxSize()) {
            val patternSize = dotRadius * 2 + dotMargin
            val patternSizePx = patternSize.toPx()
            val patternSizePxInt = patternSizePx.toInt()
            val widthInt = size.width.toInt()
            val heightInt = size.height.toInt()

            val xOffset = backgroundOffset.x % patternSizePx
            val yOffset = backgroundOffset.y % patternSizePx
            val radiusPx = dotRadius.toPx()

            for (x in -patternSizePxInt..widthInt step patternSizePxInt) {
                for (y in -patternSizePxInt..heightInt step patternSizePxInt) {
                    drawDotPattern(
                        x = x + xOffset,
                        y = y + yOffset,
                        radius = radiusPx,
                        color = dotColor,
                    )
                }
            }
        }
        content()
    }
}

private fun DrawScope.drawDotPattern(
    x: Float,
    y: Float,
    radius: Float,
    color: Color,
) {
    drawCircle(color, radius, center = Offset(x + radius, y + radius))
}
