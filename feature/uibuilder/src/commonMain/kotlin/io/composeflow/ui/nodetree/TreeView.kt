package io.composeflow.ui.nodetree

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DesktopMac
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TabletMac
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import io.composeflow.model.enumwrapper.NodeVisibility
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.VisibilityParams
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.hoverIconClickable
import io.github.vooft.compose.treeview.core.OnNodeClick
import io.github.vooft.compose.treeview.core.TreeViewStyle
import io.github.vooft.compose.treeview.core.node.BranchNode
import io.github.vooft.compose.treeview.core.node.Node
import io.github.vooft.compose.treeview.core.tree.Tree
import io.github.vooft.compose.treeview.core.tree.extension.ExpandableTree
import io.github.vooft.compose.treeview.core.tree.extension.SelectableTree
import org.jetbrains.jewel.foundation.modifier.onHover
import org.jetbrains.jewel.ui.component.Tooltip

@Composable
fun <T> TreeView(
    tree: Tree<T>,
    modifier: Modifier = Modifier,
    onClick: OnNodeClick<T>,
    project: Project,
    onVisibilityParamsUpdated: (ComposeNode, VisibilityParams) -> Unit,
    onDoubleClick: OnNodeClick<T> = {},
    onLongClick: OnNodeClick<T> = tree::toggleSelection,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
    style: TreeViewStyle<T> = TreeViewStyle(),
    listState: LazyListState = rememberLazyListState(),
) {
    val scope =
        remember(tree) {
            TreeViewScope(
                expandableManager = tree,
                selectableManager = tree,
                project = project,
                style = style,
                onClick = onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
                onShowActionTab = onShowActionTab,
                onShowInspectorTab = onShowInspectorTab,
                onVisibilityParamsUpdated = onVisibilityParamsUpdated,
            )
        }

    with(scope) {
        LazyColumn(
            state = listState,
            modifier =
                modifier
                    .fillMaxWidth()
                    .run {
                        if (style.useHorizontalScroll) {
                            horizontalScroll(rememberScrollState())
                        } else {
                            this
                        }
                    }.onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            tree.handleKeyEvent(event, this)
                            true
                        } else {
                            false
                        }
                    },
        ) {
            items(
                tree.nodes,
                { (it.content as? ComposeNode)?.fallbackId.toString() + it.key },
            ) { node ->
                Node(node)
            }
        }
    }
}

@Composable
internal fun <T> TreeViewScope<T>.Node(node: Node<T>) {
    val backgroundColor =
        when {
            node.content is ComposeNode -> {
                val composeNode = node.content as ComposeNode
                when {
                    composeNode.isHovered.value && node.isSelected ->
                        style.nodeSelectedBackgroundColor.copy(
                            alpha = 0.4f,
                        )

                    composeNode.isHovered.value ->
                        MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.9f,
                        )

                    node.isSelected -> style.nodeSelectedBackgroundColor
                    else -> Color.Unspecified
                }
            }

            node.isSelected -> style.nodeSelectedBackgroundColor
            else -> Color.Unspecified
        }

    Box(
        modifier =
            Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .background(backgroundColor, style.nodeShape)
                .then(clickableNode(node))
                .onHover { isHovered ->
                    (node.content as? ComposeNode)?.let { node ->
                        node.isHovered.value = isHovered
                    }
                },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .padding(vertical = 1.dp)
                    .padding(start = node.depth * style.toggleIconSize),
        ) {
            ToggleIcon(node)
            NodeContent(node)
        }
    }
}

@Composable
private fun <T> TreeViewScope<T>.ToggleIcon(node: Node<T>) {
    val toggleIcon = style.toggleIcon(node) ?: return

    if (node is BranchNode) {
        val rotationDegrees by animateFloatAsState(
            if (node.isExpanded) style.toggleIconRotationDegrees else 0f,
        )

        Image(
            painter = toggleIcon,
            contentDescription = if (node.isExpanded) "Collapse node" else "Expand node",
            colorFilter = style.toggleIconColorFilter,
            modifier =
                Modifier
                    .clip(style.toggleShape)
                    .clickable { expandableManager.toggleExpansion(node) }
                    .size(style.nodeIconSize)
                    .requiredSize(style.toggleIconSize)
                    .rotate(rotationDegrees),
        )
    } else {
        Spacer(Modifier.size(style.nodeIconSize))
    }
}

@Composable
private fun <T> TreeViewScope<T>.NodeContent(node: Node<T>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(style.nodePadding),
    ) {
        with(node) {
            if (node.content is ComposeNode) {
                ComposeNodeIcon(node)
                ComposeNodeName(node.content as ComposeNode)
            } else {
                DefaultNodeIcon(node)
                DefaultNodeName(node)
            }
        }
    }
}

@Composable
internal fun <T> TreeViewScope<T>.DefaultNodeIcon(node: Node<T>) {
    val (icon, colorFilter) =
        if (node is BranchNode && node.isExpanded) {
            style.nodeExpandedIcon(node) to style.nodeExpandedIconColorFilter
        } else {
            style.nodeCollapsedIcon(node) to style.nodeCollapsedIconColorFilter
        }

    if (icon != null) {
        Image(
            painter = icon,
            colorFilter = colorFilter,
            contentDescription = node.name,
        )
    }
}

@Composable
internal fun <T> TreeViewScope<T>.DefaultNodeName(node: Node<T>) {
    BasicText(
        text = node.name,
        style = style.nodeNameTextStyle,
        modifier = Modifier.padding(start = style.nodeNameStartPadding),
    )
}

@Composable
internal fun <T> TreeViewScope<T>.ComposeNodeIcon(node: Node<T>) {
    val colorFilter =
        if (node is BranchNode && node.isExpanded) {
            style.nodeExpandedIconColorFilter
        } else {
            style.nodeCollapsedIconColorFilter
        }
    val composeNode = node.content as? ComposeNode

    if (composeNode != null) {
        Image(
            imageVector = composeNode.trait.value.icon(),
            colorFilter = colorFilter,
            contentDescription = "Tree node icon for ${node.name}",
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
internal fun <T> TreeViewScope<T>.ComposeNodeName(node: ComposeNode) {
    val visibilityParams = node.visibilityParams.value

    androidx.compose.material3.Text(
        node.displayName(project),
        color =
            if (node.generateTrackableIssues(project).isNotEmpty()) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp),
    )

    val allActions = node.allActions()
    if (allActions.isNotEmpty()) {
        val contentDesc =
            if (allActions.size == 1) {
                allActions[0].name
            } else {
                "${allActions.size} actions"
            }
        Tooltip({
            if (allActions.size == 1) {
                allActions[0].SimplifiedContent(project)
            } else {
                Column {
                    androidx.compose.material3.Text(
                        "${allActions.size} actions",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    allActions.forEach { action ->
                        androidx.compose.material3.Text(
                            action.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }) {
            Icon(
                imageVector = Icons.Outlined.FlashOn,
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier =
                    Modifier.size(16.dp).hoverIconClickable().clickable {
                        onShowActionTab()
                    },
            )
        }
    }
    if (visibilityParams.nodeVisibilityValue() != NodeVisibility.AlwaysVisible || !visibilityParams.formFactorVisibility.alwaysVisible()) {
        val contentDesc =
            "Visible if " +
                visibilityParams.visibilityCondition.transformedValueExpression(
                    project,
                )
        Tooltip({
            Column {
                androidx.compose.material3.Text(
                    buildAnnotatedString {
                        append("Visible if ")
                        withStyle(
                            style =
                                SpanStyle(
                                    color = MaterialTheme.colorScheme.tertiary,
                                ),
                        ) {
                            append(
                                visibilityParams.visibilityCondition.transformedValueExpression(
                                    project,
                                ),
                            )
                        }
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!visibilityParams.formFactorVisibility.alwaysVisible()) {
                    @Composable
                    fun FormFactorIcon(
                        formFactorImageVector: ImageVector,
                        visible: Boolean,
                    ) {
                        val iconModifier =
                            if (visible) {
                                Modifier
                            } else {
                                Modifier.alpha(0.3f)
                            }
                        ComposeFlowIcon(
                            imageVector = formFactorImageVector,
                            contentDescription = "",
                            modifier = Modifier.padding(2.dp).then(iconModifier),
                        )
                    }
                    Row {
                        FormFactorIcon(
                            Icons.Outlined.Smartphone,
                            visible = visibilityParams.formFactorVisibility.visibleInCompact,
                        )
                        FormFactorIcon(
                            Icons.Outlined.TabletMac,
                            visible = visibilityParams.formFactorVisibility.visibleInMedium,
                        )
                        FormFactorIcon(
                            Icons.Outlined.DesktopMac,
                            visible = visibilityParams.formFactorVisibility.visibleInExpanded,
                        )
                    }
                }
            }
        }) {
            Icon(
                imageVector =
                    if (visibilityParams.visibleInUiBuilder) {
                        Icons.Outlined.Visibility
                    } else {
                        Icons.Outlined.VisibilityOff
                    },
                contentDescription = contentDesc,
                tint = MaterialTheme.colorScheme.secondary,
                modifier =
                    Modifier.size(16.dp).hoverIconClickable().clickable {
                        onShowInspectorTab()
                        onVisibilityParamsUpdated(
                            node,
                            visibilityParams.copy(
                                visibleInUiBuilder = !visibilityParams.visibleInUiBuilder,
                            ),
                        )
                    },
            )
        }
    }
}

private fun <T> Tree<T>.handleKeyEvent(
    event: KeyEvent,
    scope: TreeViewScope<T>,
): Boolean =
    when (event.key) {
        Key.DirectionUp -> {
            selectPrevious(scope)
        }

        Key.DirectionDown -> {
            selectNext(scope)
        }

        Key.DirectionLeft -> {
            collapseSelected(scope)
        }

        Key.DirectionRight -> {
            expandSelected(scope)
        }

        else -> false
    }

private fun <T> Tree<T>.selectPrevious(scope: TreeViewScope<T>): Boolean {
    val selectedNode = selectedNodes.firstOrNull() ?: return false
    val previousNode = nodes.getOrNull(nodes.indexOf(selectedNode) - 1) ?: return false

    scope.onClick?.invoke(previousNode)
    // selectNode(previousNode)
    return true
}

private fun <T> Tree<T>.selectNext(scope: TreeViewScope<T>): Boolean {
    val selectedNode = selectedNodes.lastOrNull() ?: return false
    val nextNode = nodes.getOrNull(nodes.indexOf(selectedNode) + 1) ?: return false

    scope.onClick?.invoke(nextNode)

    return true
}

private fun <T> Tree<T>.collapseSelected(scope: TreeViewScope<T>): Boolean {
    val selectedNode = selectedNodes.firstOrNull() ?: return false

    return if (selectedNode is BranchNode) {
        if (selectedNode.isExpanded) {
            collapseNode(selectedNode)
            true
        } else {
            val parent = findParent(selectedNode)
            if (parent != null) {
                scope.onClick?.invoke(parent)
                false
            } else {
                false
            }
        }
    } else {
        val parent = findParent(selectedNode)
        if (parent != null) {
            scope.onClick?.invoke(parent)
            false
        } else {
            false
        }
    }
}

private fun <T> Tree<T>.expandSelected(scope: TreeViewScope<T>): Boolean {
    val selectedNode = selectedNodes.firstOrNull() ?: return false

    if (selectedNode is BranchNode) {
        return if (!selectedNode.isExpanded) {
            expandNode(selectedNode)
            true
        } else {
            val firstChild = findFirstChild(selectedNode)
            if (firstChild != null) {
                scope.onClick?.invoke(firstChild)
                false
            } else {
                false
            }
        }
    } else {
        selectNext(scope)
    }

    return false
}

private fun <T> Tree<T>.findParent(node: Node<T>): Node<T>? {
    val idx = nodes.indexOf(node)

    for (i in idx - 1 downTo 0) {
        if (nodes[i].depth < node.depth) return nodes[i]
    }

    return null
}

private fun <T> Tree<T>.findFirstChild(branch: BranchNode<T>): Node<T>? {
    val idx = nodes.indexOf(branch)

    for (i in idx + 1 until nodes.size) {
        if (nodes[i].depth <= branch.depth) break
        if (nodes[i].depth == branch.depth + 1) return nodes[i]
    }

    return null
}

// poor performance
fun <T> TreeViewScope<T>.clickableNode(node: Node<T>): Modifier =
    Modifier.combinedClickable(
        onClick = { onClick?.invoke(node) },
    /*  onDoubleClick = { onDoubleClick?.invoke(node) },
      onLongClick = { onLongClick?.invoke(node) },*/
    )

fun Tree<ComposeNode>.setFocus(composeNodes: List<ComposeNode>) {
    println("Set focus")
    val nodesToCollapse =
        nodes
            .asSequence()
            .filterIsInstance<BranchNode<ComposeNode>>()
            .filter { it.isExpanded }
            .toMutableList()

    val nodesToExpand = mutableSetOf<ComposeNode>()
    val nodesToSelect = mutableSetOf<Node<ComposeNode>>()

    composeNodes.forEach { composeNode ->
        val ancestors = composeNode.findNodesUntilRoot(includeSelf = true)
        nodesToExpand.addAll(ancestors)

        findNodeByFallbackId(composeNode.fallbackId)?.let { node ->
            nodesToSelect.add(node)
        }
    }

    clearSelection()

    nodesToSelect.forEach { selectNode(it) }

    nodesToCollapse.removeAll { collapseNode ->
        nodesToExpand.any { expandNode -> expandNode.fallbackId == collapseNode.content.fallbackId }
    }

    // expandNodes(nodesToExpand.toList())

    // collapseNodes(nodesToCollapse)
}

fun Tree<ComposeNode>.collapseNodes(composeNodes: List<BranchNode<ComposeNode>>) {
    composeNodes.forEach { node ->
        node.setExpanded(false, Int.MAX_VALUE)
    }
}

fun Tree<ComposeNode>.expandNodes(composeNodes: List<ComposeNode>) {
    val treeNodes = nodes.asSequence().filterIsInstance<BranchNode<ComposeNode>>()

    composeNodes.forEach { node ->
        treeNodes
            .find { it.content.fallbackId == node.fallbackId }
            ?.setExpanded(true, Int.MAX_VALUE)
    }
}

fun Tree<ComposeNode>.findNodeByFallbackId(id: String) =
    nodes.firstOrNull {
        it.content.fallbackId == id
    }

fun Tree<ComposeNode>.expandedNodes() = nodes.filterIsInstance<BranchNode<ComposeNode>>().filter { it.isExpanded }

fun Tree<ComposeNode>.findLazyListIndex(target: ComposeNode): Int {
    val visibleNodes = mutableListOf<Node<ComposeNode>>()

    fun addVisible(node: Node<ComposeNode>) {
        visibleNodes.add(node)
        if (node is BranchNode<ComposeNode> && node.isExpanded) {
            val startIndex = nodes.indexOf(node) + 1
            for (i in startIndex until nodes.size) {
                val child = nodes[i]
                if (child.depth <= node.depth) break
                if (child.depth == node.depth + 1) {
                    addVisible(child)
                }
            }
        }
    }

    nodes.forEach { addVisible(it) }

    return visibleNodes.indexOfFirst { it.content.fallbackId == target.fallbackId }
}

@ConsistentCopyVisibility
@Immutable
data class TreeViewScope<T> internal constructor(
    internal val expandableManager: ExpandableTree<T>,
    internal val selectableManager: SelectableTree<T>,
    internal val style: TreeViewStyle<T>,
    internal val project: Project,
    internal val onClick: OnNodeClick<T>,
    internal val onLongClick: OnNodeClick<T>,
    internal val onDoubleClick: OnNodeClick<T>,
    internal val onShowActionTab: () -> Unit,
    internal val onShowInspectorTab: () -> Unit,
    internal val onVisibilityParamsUpdated: (ComposeNode, VisibilityParams) -> Unit,
)
