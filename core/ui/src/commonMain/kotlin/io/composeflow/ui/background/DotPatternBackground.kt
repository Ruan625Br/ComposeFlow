package io.composeflow.ui.background

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp

@Composable
fun DotPatternBackground(
    dotRadius: Dp,
    dotMargin: Dp,
    dotColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val patternSize = dotRadius * 2 + dotMargin
            for (x in 0 until size.width.toInt() step patternSize.toPx().toInt()) {
                for (y in 0 until size.height.toInt() step patternSize.toPx().toInt()) {
                    drawDotPattern(
                        x = x.toFloat(),
                        y = y.toFloat(),
                        radius = dotRadius.toPx(),
                        color = dotColor,
                    )
                }
            }
        }
        content()
    }
}

private fun DrawScope.drawDotPattern(x: Float, y: Float, radius: Float, color: Color) {
    drawCircle(color, radius, center = Offset(x + radius, y + radius))
}
