package io.composeflow.ui.labeledbox

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LabeledBorderBox(
    label: String,
    cornerRadius: Dp = 8.dp,
    borderWidth: Dp = 1.dp,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    val textStyle = MaterialTheme.typography.labelSmall
    var focused by remember { mutableStateOf(false) }

    val textMeasure = rememberTextMeasurer()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    textLayoutResult = textMeasure.measure(
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontSize = textStyle.fontSize,
                ),
            ) {
                append(label)
            }
        },
    )
    val drawColor = if (focused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    val textWidth = textLayoutResult?.size?.width ?: 0
    val density = LocalDensity.current
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .padding(4.dp)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .focusable()
            .onFocusChanged {
                focused = it.hasFocus
            }
            .drawBehind {
                with(density) {
                    val labelPadding = if (label.isEmpty()) 0.dp.toPx() else 8.dp.toPx()
                    val labelStartOffset = 6.dp
                    val roundedRect = RoundRect(
                        left = -labelStartOffset.toPx(),
                        top = -4.dp.toPx(),
                        right = size.width + labelStartOffset.toPx(),
                        bottom = size.height + 4.dp.toPx(),
                        radiusX = cornerRadius.toPx(),
                        radiusY = cornerRadius.toPx(),
                    )

                    val borderPath = Path().apply {
                        addRoundRect(roundedRect)
                    }
                    drawPath(
                        path = borderPath,
                        color = drawColor,
                        style = Stroke(width = borderWidth.toPx()),
                    )
                    drawLine(
                        color = backgroundColor,
                        start = Offset(-labelStartOffset.toPx() + labelPadding, -4.dp.toPx()),
                        end = Offset(textWidth + labelPadding, -4.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                    )

                    textLayoutResult?.let {
                        drawText(
                            textLayoutResult = it,
                            color = drawColor,
                            topLeft = Offset(
                                x = labelStartOffset.toPx(),
                                y = -10.dp.toPx(),
                            ),
                        )
                    }
                }
            }
            .padding(vertical = 4.dp)
            .padding(start = 6.dp),
    ) {
        content()
    }
}
