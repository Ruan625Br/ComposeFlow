package io.composeflow.ui.deviceframe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PixelPhoneFrame(
    contentPadding: Dp,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {

    val cornerRadius = 16.dp
    Box(modifier = modifier
        .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center) {
        // Main contents area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadius))
                .padding(contentPadding),
        ) {
            content?.let { it() }
        }

        // Frame overlay
        Canvas(modifier = Modifier.matchParentSize()) {
            val cornerRadiusPx = cornerRadius.toPx()
            val width = size.width
            val height = size.height


            // Shadow #1
            drawRoundRect(
                color = Color(0xFF6E707D),
                style = Stroke(width = 30.dp.toPx())
            )
            // Shadow #2
            drawRoundRect(
                topLeft = Offset(x = 3.dp.toPx(), y = 3.dp.toPx()),
                size = Size(width = width - 6.dp.toPx(), height = height - 6.dp.toPx()),
                color = Color(0xFF767676),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 3.dp.toPx())
            )
            // Shadow #1
            drawRoundRect(
                color = Color(0xFF6E707D),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 3.dp.toPx())
            )
            // Silver frame
            drawRoundRect(
                topLeft = Offset(x = 8.dp.toPx(), y = 8.dp.toPx()),
                size = Size(width = width - 16.dp.toPx(), height = height - 16.dp.toPx()),
                color = Color(0xFFA2A2A2),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 10.dp.toPx())
            )
            // Very thin black frame
            drawRoundRect(
                topLeft = Offset(x = 14.dp.toPx(), y = 14.dp.toPx()),
                size = Size(width = width - 28.dp.toPx(), height = height - 28.dp.toPx()),
                color = Color(0xFF242422),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 6.dp.toPx())
            )
            // Thin gray frame
            drawRoundRect(
                topLeft = Offset(x = 15.dp.toPx(), y = 15.dp.toPx()),
                size = Size(width = width - 30.dp.toPx(), height = height - 30.dp.toPx()),
                color = Color(0xFF444444),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 4.dp.toPx())
            )

            // Black frame
            drawRoundRect(
                topLeft = Offset(x = 20.dp.toPx(), y = 20.dp.toPx()),
                size = Size(width = width - 40.dp.toPx(), height = height - 40.dp.toPx()),
                color = Color.Black,
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = 10.dp.toPx())
            )

            // ---- Top speaker -----
            val speakerWidth = width * 0.2f
            val speakerHeight = 6.dp.toPx()
            val speakerX = (width - speakerWidth) / 2f
            val speakerY = 16.dp.toPx()

            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(speakerX, speakerY),
                size = Size(speakerWidth, speakerHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // ---- Bottom home bar ----
            val barWidth = width * 0.3f
            val barHeight = 5.dp.toPx()
            val barX = (width - barWidth) / 2f
            val barY = height - 20.dp.toPx()

            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(barX, barY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx())
            )
        }
    }
}
