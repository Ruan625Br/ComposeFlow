package io.composeflow.ui.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntSize

@Composable
fun rememberZoomableContainerState(): ZoomableContainerStateHolder {
    return remember { ZoomableContainerStateHolder() }
}

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

    private fun zoom(value: Float, retainPoint: Offset) {
        val constrained = value.coerceIn(0.1f, 20f)
        val zoomRatio = constrained / scale
        val x =
            offset.x - (retainPoint.x - offset.x) * zoomRatio + (retainPoint.x - offset.x)
        val y =
            offset.y - (retainPoint.y - offset.y) * zoomRatio + (retainPoint.y - offset.y)
        offset = Offset(
            x,
            y,
        )
        scale = constrained
    }

    fun onToolbarZoomScaleChanged(scale: Float) {
        // Zoom to screen center
        zoom(scale, Offset(size.width / 2f, size.height / 2f))
    }

    fun onScroll(scrollDelta: Float) {
        return zoom(
            value = scale + scale * scrollDelta / 100,
            retainPoint = mousePosition,
        )
    }

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

@Composable
fun ZoomableContainer(
    zoomableContainerStateHolder: ZoomableContainerStateHolder,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
            .graphicsLayer(
                scaleX = zoomableContainerStateHolder.scale,
                scaleY = zoomableContainerStateHolder.scale,
                transformOrigin = TransformOrigin(0f, 0f),
                translationX = zoomableContainerStateHolder.offset.x,
                translationY = zoomableContainerStateHolder.offset.y,
            ),
        content = content,
    )
}
