package io.composeflow.ui.treeview

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import io.composeflow.ui.treeview.node.BranchNode
import io.composeflow.ui.treeview.node.Node
import io.composeflow.ui.treeview.tree.Tree
import kotlinx.coroutines.channels.Channel

@Composable
fun <T> rememberDragDropNodeState(
    lazyListState: LazyListState,
    draggableItemsNum: Int,
    tree: Tree<T>,
    onMove: (Int, Int) -> Unit,
): DragDropNodeState<T> {
    val state =
        remember(lazyListState) {
            DragDropNodeState<T>(
                draggableItemsNum = draggableItemsNum,
                stateList = lazyListState,
                tree = tree,
                onMove = onMove,
            )
        }

    LaunchedEffect(state) {
        while (true) {
            val diff = state.scrollChannel.receive()
            lazyListState.scrollBy(diff)
        }
    }

    return state
}

fun <T> Modifier.dragNodeContainer(dragDropState: DragDropNodeState<T>): Modifier =
    this.then(
        pointerInput(dragDropState) {
            detectDragGesturesAfterLongPress(
                onDrag = { change, offset ->
                    change.consume()
                    dragDropState.onDrag(offset)
                },
                onDragStart = {
                    dragDropState.onDragStart(it)
                },
                onDragEnd = {
                    dragDropState.onDragEnd()
                },
                onDragCancel = {
                    dragDropState.onDragInterrupted()
                },
            )
        },
    )

class DragDropNodeState<T>(
    private val draggableItemsNum: Int,
    private val stateList: LazyListState,
    private val tree: Tree<T>,
    private val onMove: (Int, Int) -> Unit,
) {
    var isDragging by mutableStateOf(false)
    var delta by mutableFloatStateOf(0f)
    var currentDragPointerPosition by mutableStateOf(Offset.Zero)
        private set

    var draggingNodeKey: Any? by mutableStateOf(null)
    var targetNodeKey: Any? by mutableStateOf(null)
    var targetNodeIndex: Int? by mutableStateOf(null)
    var draggingItem: LazyListItemInfo? = null

    val scrollChannel = Channel<Float>()

    internal fun onDragStart(offset: Offset) {
        stateList.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
            ?.also { info ->
                val d = info.contentType as DraggableNodeItem<*>
                draggingNodeKey = d.node.key
                draggingItem = info
            }
    }

    internal fun onDragInterrupted() {
        draggingNodeKey = null
        targetNodeKey = null
        draggingItem = null
        isDragging = false
        delta = 0f
        currentDragPointerPosition = Offset.Zero
    }

    internal fun onDrag(offset: Offset) {
        val keyBeingDragged = draggingNodeKey ?: return
        val infoBeingDragged = draggingItem ?: return

        delta += offset.y
        currentDragPointerPosition += offset
        isDragging = true

        val currentIndex = tree.nodes.indexOfFirst { it.key == keyBeingDragged }.takeIf { it >= 0 } ?: return

        val startOffset = infoBeingDragged.offset + delta
        val endOffset = infoBeingDragged.offset + infoBeingDragged.size + delta
        val middleOffset = startOffset + (endOffset - startOffset) / 2

        val targetItem =
            stateList.layoutInfo.visibleItemsInfo
                .find { item ->
                    middleOffset.toInt() in item.offset..(item.offset + item.size) &&
                        item.index != currentIndex
                }

        if (targetItem != null) {
            val draggingNodeKey = (targetItem.contentType as DraggableNodeItem<*>).node.key
            targetNodeIndex =
                tree.nodes.indexOfFirst { it.key == draggingNodeKey }.takeIf { it >= 0 } ?: return
            targetNodeKey = tree.nodes[targetNodeIndex!!].key

            val currentDraggingItem = draggingItem
            // onMove(currentIndex, targetNodeIndex)
            // draggingItem = targetItem
            // delta += (currentDraggingItem?.offset ?: 0) - targetItem.offset
        } else {
            val startOffsetToTop = startOffset - stateList.layoutInfo.viewportStartOffset
            val endOffsetToBottom = endOffset - stateList.layoutInfo.viewportEndOffset

            val scroll =
                when {
                    startOffsetToTop < 0 -> startOffsetToTop.coerceAtMost(0f)
                    endOffsetToBottom > 0 -> endOffsetToBottom.coerceAtLeast(0f)
                    else -> 0f
                }

            if (scroll != 0f &&
                currentIndex != 0 &&
                currentIndex != draggableItemsNum - 1
            ) {
                scrollChannel.trySend(scroll)
            }
        }
    }

    internal fun onDragEnd() {
        val keyBeingDragged = draggingNodeKey ?: return

        val currentIndex = tree.nodes.indexOfFirst { it.key == keyBeingDragged }.takeIf { it >= 0 } ?: return

        onMove(currentIndex, targetNodeIndex ?: return)

        onDragInterrupted()
    }
}

data class DraggableNodeItem<T>(
    val index: Int,
    val node: Node<T>,
)
