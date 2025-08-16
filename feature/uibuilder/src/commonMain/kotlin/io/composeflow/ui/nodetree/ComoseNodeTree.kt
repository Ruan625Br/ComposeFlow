package io.composeflow.ui.nodetree

import androidx.compose.foundation.Image
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DesktopMac
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TabletMac
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.composeflow.Res
import io.composeflow.component_name
import io.composeflow.model.enumwrapper.NodeVisibility
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.parameter.ColumnTrait
import io.composeflow.model.parameter.LazyRowTrait
import io.composeflow.model.parameter.RowTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.appscreen.screen.composenode.VisibilityParams
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.inspector.modifier.AddModifierDialog
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.popup.SingleTextInputDialog
import io.composeflow.ui.treeview.TreeView
import io.composeflow.ui.treeview.TreeViewColors
import io.composeflow.ui.treeview.TreeViewScope
import io.composeflow.ui.treeview.TreeViewStyle
import io.composeflow.ui.treeview.node.Branch
import io.composeflow.ui.treeview.node.BranchNode
import io.composeflow.ui.treeview.node.Leaf
import io.composeflow.ui.treeview.node.Node
import io.composeflow.ui.treeview.rememberDragDropNodeState
import io.composeflow.ui.treeview.tree.Tree
import io.composeflow.ui.treeview.tree.TreeScope
import io.composeflow.ui.treeview.tree.findParent
import io.composeflow.ui.uibuilder.UiBuilderContextMenuDropDown
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Tooltip

@Composable
fun ComposeNodeTree(
    project: Project,
    canvasNodeCallbacks: CanvasNodeCallbacks,
    composeNodeCallbacks: ComposeNodeCallbacks,
    copiedNodes: List<ComposeNode>,
    onFocusedStatusUpdated: (ComposeNode) -> Unit,
    onHoveredStatusUpdated: (ComposeNode, Boolean) -> Unit,
    onShowInspectorTab: () -> Unit,
    onShowActionTab: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val editable = project.screenHolder.currentEditable()
    val rootNode = editable.getRootNode()
    val focusedNodes = project.screenHolder.findFocusedNodes()

    @Composable
    fun TreeScope.addBranch(
        composeNode: ComposeNode,
        children: @Composable (TreeScope.() -> Unit),
    ) {
        Branch(
            composeNode = composeNode,
            project = project,
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onVisibilityParamsUpdated = { node, params ->
                composeNodeCallbacks.onVisibilityParamsUpdated(node, params)
            },
            children = children,
        )
    }

    @Composable
    fun TreeScope.addLeaf(composeNode: ComposeNode) {
        Leaf(
            composeNode = composeNode,
            project = project,
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onVisibilityParamsUpdated = { node, params ->
                composeNodeCallbacks.onVisibilityParamsUpdated(node, params)
            },
        )
    }

    @Composable
    fun TreeScope.addNodeRecursively(node: ComposeNode) {
        addBranch(node) {
            node.children.forEach { child ->
                if (child.children.isNotEmpty()) {
                    this.addNodeRecursively(child)
                } else {
                    addLeaf(child)
                }
            }
        }
    }

    val tree =
        Tree<ComposeNode> {
            if (editable is Screen) {
                addBranch(rootNode) {
                    editable.navigationDrawerNode.value?.let { navDrawer ->
                        addNodeRecursively(navDrawer)
                    }
                    editable.topAppBarNode.value?.let { topAppBar ->
                        addNodeRecursively(topAppBar)
                    }
                    addNodeRecursively(editable.contentRootNode())

                    editable.getBottomAppBar()?.let { bottomAppBar ->
                        addNodeRecursively(bottomAppBar)
                    }
                    editable.fabNode.value?.let { fabNode ->
                        addLeaf(fabNode)
                    }
                }
            } else {
                addNodeRecursively(rootNode)
            }
        }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    var contextMenuExpanded by remember { mutableStateOf(false) }
    var addModifierDialogVisible by remember { mutableStateOf(false) }
    var convertToComponentNode by remember { mutableStateOf<ComposeNode?>(null) }

    // TODO adjust this to work like MaterialThemes
    val treeViewStyle =
        TreeViewStyle<ComposeNode>(
            toggleIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            colors =
                TreeViewColors(
                    selected = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                    hovered =
                        MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.9f,
                        ),
                ),
            nodeCollapsedIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            nodeExpandedIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            useHorizontalScroll = false,
        )

    val dragDropNodeState =
        rememberDragDropNodeState(
            lazyListState = lazyListState,
            tree = tree,
            draggableItemsNum = tree.nodes.size,
            onMove = { fromIndex, toIndex ->
                Logger.d("onMove: $fromIndex -> $toIndex")
                val fromNode = tree.nodes[fromIndex]
                val toNode = tree.nodes[toIndex]
                val fromCompose = fromNode.content
                val toCompose = tree.findParent(toNode)?.content ?: toNode.content

                Logger.d { "Node pos: ${fromCompose.boundsInWindow.value.topLeft}" }

                val dropPosition =
                    when {
                        toCompose.trait.value is LazyRowTrait -> {
                            toCompose
                                .boundsInWindow.value.topLeft
                        }
                        toCompose.trait.value is ColumnTrait -> {
                            Offset(
                                toCompose.boundsInWindow.value.topLeft.x,
                                toCompose.boundsInWindow.value.bottomLeft.y,
                            )
                        }
                        else -> toCompose.boundsInWindow.value.center
                    }

                canvasNodeCallbacks
                    .onNodeDropToPosition(
                        dropPosition,
                        fromCompose,
                    )

                // onnNodeDropToPosition:  Offset(712.7, 652.5) valida
                //  onNodeDropToPosition:  Offset(-96.0, 0.0) invalida
                Logger.d("onMoveCompose: ${fromCompose.fallbackId} -> \n ->->->${toCompose.fallbackId}")
            },
        )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                    contextMenuExpanded = true
                },
    ) {
        TreeView(
            modifier = Modifier.fillMaxWidth(),
            tree = tree,
            listState = lazyListState,
            dragAndDropState = dragDropNodeState,
            style = treeViewStyle,
            onClick = { treeNode, isCtrlPressed, isShitPressed ->
                project.screenHolder.clearIsFocused()

                val nodes = tree.handleMultipleSelection(treeNode, isCtrlPressed, isShitPressed)
                nodes.forEach {
                    it.content.setFocus()
                }
            },
            onHover = { node, isHovered ->
                node.content.isHovered.value = isHovered

                tree.setHovered(node, isHovered)
            },
        )

        if (contextMenuExpanded) {
            UiBuilderContextMenuDropDown(
                project = project,
                canvasNodeCallbacks = canvasNodeCallbacks,
                composeNodeCallbacks = composeNodeCallbacks,
                copiedNodes = copiedNodes,
                currentEditable = project.screenHolder.currentEditable(),
                onAddModifier = {
                    addModifierDialogVisible = true
                },
                onCloseMenu = {
                    contextMenuExpanded = !contextMenuExpanded
                },
                onShowSnackbar = onShowSnackbar,
                coroutineScope = coroutineScope,
                onFocusedStatusUpdated = onFocusedStatusUpdated,
                onHoveredStatusUpdated = onHoveredStatusUpdated,
                onOpenConvertToComponentDialog = {
                    convertToComponentNode = it
                },
            )
        }

        val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
        val onAllDialogsClosed = LocalOnAllDialogsClosed.current
        if (addModifierDialogVisible) {
            onAnyDialogIsShown()
            project.screenHolder.findFocusedNodes().firstOrNull()?.let { focused ->
                val modifiers =
                    ModifierWrapper
                        .values()
                        .filter { m ->
                            focused.parentNode?.let {
                                m.hasValidParent(it.trait.value)
                            } ?: true
                        }.mapIndexed { i, modifierWrapper ->
                            i to modifierWrapper
                        }

                AddModifierDialog(
                    modifiers = modifiers,
                    onModifierSelected = {
                        addModifierDialogVisible = false
                        composeNodeCallbacks.onModifierAdded(focused, modifiers[it].second)
                        onAllDialogsClosed()
                    },
                    onCloseClick = {
                        addModifierDialogVisible = false
                        onAllDialogsClosed()
                    },
                )
            }
        }

        convertToComponentNode?.let { nodeToConvert ->
            onAnyDialogIsShown()
            SingleTextInputDialog(
                textLabel = stringResource(Res.string.component_name),
                onTextConfirmed = {
                    canvasNodeCallbacks.onConvertToComponent(it, nodeToConvert)
                    convertToComponentNode = null
                    onAllDialogsClosed()
                },
                onDismissDialog = {
                    convertToComponentNode = null
                    onAllDialogsClosed()
                },
            )
        }
    }

    LaunchedEffect(focusedNodes) {
        tree.setFocus(focusedNodes)
    }

    if (focusedNodes.size == 1) {
        LaunchedEffect(focusedNodes.firstOrNull()?.fallbackId) {
            focusedNodes.firstOrNull()?.let { focused ->
                val index = tree.findLazyListIndex(target = focused)
                if (index != -1) {
                    lazyListState.scrollToItem(index)
                }
            }
        }
    }
}

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

suspend fun Tree<ComposeNode>.setFocus(composeNodes: List<ComposeNode>) {
    clearSelection()

    composeNodes.forEach { composeNode ->
        expandPathToNode(composeNode)

        findNodeByFallbackId(composeNode.fallbackId)?.let { node ->
            selectNode(node)
        }
    }
}

suspend fun Tree<ComposeNode>.expandPathToNode(node: ComposeNode) {
    val path = node.findNodesUntilRoot(includeSelf = true)
    path.reversed().forEach { composeNodeInPath ->

        val treeNode = findNodeByFallbackId(composeNodeInPath.fallbackId)

        if (treeNode is BranchNode && !treeNode.isExpanded) {
            expandNode(treeNode)
            withFrameNanos { }
        }
    }
}

fun Tree<ComposeNode>.findNodeByFallbackId(id: String) =
    nodes.firstOrNull {
        it.content.fallbackId == id
    }

@Composable
private fun TreeScope.Branch(
    composeNode: ComposeNode,
    project: Project,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
    onVisibilityParamsUpdated: (ComposeNode, VisibilityParams) -> Unit,
    children: @Composable (TreeScope.() -> Unit),
) {
    with(composeNode) {
        Branch(
            content = composeNode,
            key = composeNode.id,
            children = children,
            customIcon = { node ->
                if (node.isHovered != composeNode.isHovered.value) {
                    hoverableManager.setHovered(node, composeNode.isHovered.value)
                }

                ComposeNodeIcon(node, project)
            },
            customName = { node ->
                ComposeNodeName(
                    node.content,
                    project,
                    onShowActionTab,
                    onShowInspectorTab,
                    onVisibilityParamsUpdated,
                )
            },
        )
    }
}

@Composable
private fun TreeScope.Leaf(
    composeNode: ComposeNode,
    project: Project,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
    onVisibilityParamsUpdated: (ComposeNode, VisibilityParams) -> Unit,
) {
    with(composeNode) {
        Leaf(
            content = composeNode,
            key = composeNode.id,
            customIcon = { node ->
                if (node.isHovered != composeNode.isHovered.value) {
                    hoverableManager.setHovered(node, composeNode.isHovered.value)
                }
                ComposeNodeIcon(node, project)
            },
            customName = { node ->
                ComposeNodeName(
                    node.content,
                    project,
                    onShowActionTab,
                    onShowInspectorTab,
                    onVisibilityParamsUpdated,
                )
            },
        )
    }
}

@Composable
private fun TreeViewScope<ComposeNode>.ComposeNodeIcon(
    node: Node<ComposeNode>,
    project: Project,
) {
    val colorFilter =
        if (node is BranchNode && node.isExpanded) {
            style.nodeExpandedIconColorFilter
        } else {
            style.nodeCollapsedIconColorFilter
        }
    val composeNode = node.content

    Image(
        imageVector = composeNode.trait.value.icon(),
        colorFilter = colorFilter,
        contentDescription = "Tree node icon for ${composeNode.displayName(project)}",
        modifier = Modifier.size(style.nodeIconSize),
    )
}

@Composable
private fun TreeViewScope<ComposeNode>.ComposeNodeName(
    node: ComposeNode,
    project: Project,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
    onVisibilityParamsUpdated: (ComposeNode, VisibilityParams) -> Unit,
) {
    val visibilityParams = node.visibilityParams.value

    Row {
        Text(
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
                        Text(
                            "${allActions.size} actions",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        allActions.forEach { action ->
                            Text(
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
                        Modifier.size(style.nodeIconSize).hoverIconClickable().clickable {
                            onShowActionTab()
                        },
                )
            }
        }
        if (visibilityParams.nodeVisibilityValue() != NodeVisibility.AlwaysVisible ||
            !visibilityParams.formFactorVisibility.alwaysVisible()
        ) {
            val contentDesc =
                "Visible if " +
                    visibilityParams.visibilityCondition.transformedValueExpression(
                        project,
                    )
            Tooltip({
                Column {
                    Text(
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
}
