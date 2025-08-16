package io.composeflow.ui.treeview

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import co.touchlab.kermit.Logger
import io.composeflow.ui.treeview.node.Node
import io.composeflow.ui.treeview.node.NodeWithLines
import io.composeflow.ui.treeview.node.extension.HoverableNode
import io.composeflow.ui.treeview.tree.Tree
import io.composeflow.ui.treeview.tree.handleKeyEvent

data class TreeViewStyle<T>(
    val toggleIcon: NodeIcon<T> = { rememberVectorPainter(Icons.Default.ChevronRight) },
    val toggleIconSize: Dp = 16.dp,
    val toggleIconColorFilter: ColorFilter? = null,
    val toggleShape: Shape = CircleShape,
    val toggleIconRotationDegrees: Float = 90f,
    val nodeIconSize: Dp = 16.dp,
    val nodePadding: PaddingValues = PaddingValues(all = 4.dp),
    val nodeShape: Shape = RoundedCornerShape(size = 4.dp),
    val colors: TreeViewColors = TreeViewColors(),
    val nodeCollapsedIcon: NodeIcon<T> = { null },
    val nodeCollapsedIconColorFilter: ColorFilter? = null,
    val nodeExpandedIcon: NodeIcon<T> = nodeCollapsedIcon,
    val nodeExpandedIconColorFilter: ColorFilter? = nodeCollapsedIconColorFilter,
    val nodeNameStartPadding: Dp = 0.dp,
    val nodeNameTextStyle: TextStyle = DefaultNodeTextStyle,
    val useHorizontalScroll: Boolean = true,
    val showLines: Boolean = true,
) {
    companion object {
        val DefaultNodeTextStyle: TextStyle =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            )
    }
}

data class TreeViewColors(
    val normal: Color = Color.Unspecified,
    val selected: Color = Color.LightGray.copy(alpha = 0.8f),
    val hovered: Color = selected.copy(0.9f),
    val hoveredSelected: Color = selected.copy(alpha = 0.4f),
    val stroke: Color = Color.Gray,
)

@Composable
fun <T> TreeView(
    tree: Tree<T>,
    modifier: Modifier = Modifier,
    onClick: ((node: Node<T>, ctrl: Boolean, shift: Boolean) -> Unit)? = null,
    onDoubleClick: OnNodeClick<T> = tree::onNodeClick,
    onLongClick: OnNodeClick<T> = tree::toggleSelection,
    onHover: OnNodeHover<T> = ::onNodeHover,
    style: TreeViewStyle<T> = TreeViewStyle(),
    dragAndDropState: DragDropNodeState<T>? = null,
    listState: LazyListState = rememberLazyListState(),
) {
    val scope =
        remember(tree) {
            TreeViewScope(
                expandableManager = tree,
                selectableManager = tree,
                hoverableManager = tree,
                style = style,
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                onHover = onHover,
            )
        }

    with(scope) {
        LazyColumn(
            state = listState,
            modifier =
                modifier
                    .fillMaxWidth()
                    .let { mod -> dragAndDropState?.let { mod.dragNodeContainer(it) } ?: mod }
                    .focusable(),
                    /*.onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            // TODO fix this
                            tree.handleKeyEvent(event, this)
                        } else {
                            false
                        }
                    }*/
        ) {
            itemsIndexed(
                items = tree.nodes,
                key = { index, node ->
                    node.key
                },
                contentType = { index, node ->
                    DraggableNodeItem(index, node)
                },
                itemContent = { index, node ->
                    val isDragging = dragAndDropState?.draggingNodeKey == node.key

                    Box(
                        modifier =
                            Modifier
                                .graphicsLayer {
                                    if (isDragging) translationY = dragAndDropState?.delta ?: 0f
                                }.zIndex(if (isDragging) 1f else 0f),
                    ) {
                        if (style.showLines) {
                            NodeWithLines(
                                node = node,
                                index = index,
                                nodes = tree.nodes,
                            )
                        } else {
                            Node(node = node)
                        }

                        if (dragAndDropState?.targetNodeKey == node.key) {
                            Box(
                                modifier =
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .padding(start = node.depth * style.toggleIconSize)
                                        .height(2.dp)
                                        .background(style.colors.hovered),
                            )
                        }
                    }
                },
            )
        }
    }
}

private fun <T> Tree<T>.onNodeClick(node: Node<T>) {
    clearSelection()
    toggleExpansion(node)
}

private fun <T> onNodeHover(
    node: Node<T>,
    isHovered: Boolean,
) {
    (node as? HoverableNode)?.setHovered(isHovered)
}
