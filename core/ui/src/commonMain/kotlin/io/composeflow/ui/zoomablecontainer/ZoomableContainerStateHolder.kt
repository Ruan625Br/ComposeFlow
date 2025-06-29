package io.composeflow.ui.zoomablecontainer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize

/**
 * see: https://kotlinlang.slack.com/archives/C01D6HTPATV/p1665697618754779?thread_ts=1664832307.853679&cid=C01D6HTPATV
 */
class ZoomableContainerStateHolder {
    private var mousePosition by mutableStateOf(Offset.Zero)

    var offset by mutableStateOf(Offset.Zero)
        private set

    var scale by mutableStateOf(1f)
        private set

    var size by mutableStateOf(IntSize.Zero)
        private set

    private fun zoom(
        value: Float,
        retainPoint: Offset,
    ) {
        val constrained = value.coerceIn(0.1f, 20f)
        val zoomRatio = constrained / scale
        val x =
            offset.x - (retainPoint.x - offset.x) * zoomRatio + (retainPoint.x - offset.x)
        val y =
            offset.y - (retainPoint.y - offset.y) * zoomRatio + (retainPoint.y - offset.y)
        offset =
            Offset(
                x,
                y,
            )
        scale = constrained
    }

    fun panBy(dragAmount: Offset) {
        offset += dragAmount
    }

    fun onToolbarZoomScaleChanged(scale: Float) {
        // Zoom to screen center
        zoom(scale, Offset(size.width / 2f, size.height / 2f))
    }

    fun onScroll(scrollDelta: Float) =
        zoom(
            value = scale + scale * scrollDelta / 100,
            retainPoint = mousePosition,
        )

    fun onSizeChanged(size: IntSize) {
        this.size = size
    }

    fun onMousePositionChanged(mousePosition: Offset) {
        this.mousePosition = mousePosition
    }

    fun resetZoom() {
        offset = Offset.Zero
        scale = 1f
    }
}

fun Rect.calculateAdjustedBoundsInZoomableContainer(zoomableContainerStateHolder: ZoomableContainerStateHolder): Rect {
    val scale = zoomableContainerStateHolder.scale
    val offset = zoomableContainerStateHolder.offset

    val contentTopLeft = (topLeft - offset) / scale
    val contentBottomRight = (bottomRight - offset) / scale
    return Rect(topLeft = contentTopLeft, bottomRight = contentBottomRight)
}
