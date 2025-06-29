package io.composeflow.ui.reorderable

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.ReorderableLazyListState
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

@Composable
fun ComposeFlowReorderableItem(
    index: Int,
    reorderableLazyListState: ReorderableLazyListState =
        rememberReorderableLazyListState(onMove = { _, _ -> }),
    content: @Composable () -> Unit,
) {
    ReorderableItem(reorderableLazyListState, key = "item-$index", index = index) { isDragging ->
        val elevation = animateDpAsState(if (isDragging) 16.dp else 0.dp)
        val backgroundModifier =
            if (isDragging) {
                Modifier.background(
                    color =
                        MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.8f,
                        ),
                )
            } else {
                Modifier
            }
        val clipShape =
            if (isDragging) RoundedCornerShape(16.dp) else RectangleShape
        Column(
            modifier =
                Modifier
                    .clip(clipShape)
                    .shadow(elevation.value)
                    .then(backgroundModifier),
        ) {
            content()
        }
    }
}
