package io.composeflow.ui.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder

@Composable
fun rememberZoomableContainerState(): ZoomableContainerStateHolder = remember { ZoomableContainerStateHolder() }

@Composable
fun ZoomableContainer(
    zoomableContainerStateHolder: ZoomableContainerStateHolder,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
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
