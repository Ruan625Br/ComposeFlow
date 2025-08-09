package io.composeflow.ui.nodetree

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.component_name
import io.composeflow.model.enumwrapper.NodeVisibility
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.inspector.modifier.AddModifierDialog
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.popup.SingleTextInputDialog
import io.composeflow.ui.uibuilder.UiBuilderContextMenuDropDown
import io.github.vooft.compose.treeview.core.TreeViewStyle
import io.github.vooft.compose.treeview.core.node.Branch
import io.github.vooft.compose.treeview.core.node.BranchNode
import io.github.vooft.compose.treeview.core.node.Leaf
import io.github.vooft.compose.treeview.core.tree.TreeScope
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.lazy.tree.Tree
import org.jetbrains.jewel.foundation.lazy.tree.TreeGeneratorScope
import org.jetbrains.jewel.foundation.lazy.tree.TreeState
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

    fun TreeGeneratorScope<ComposeNode>.addNodeRecursively(node: ComposeNode) {
        addNode(node, id = node.fallbackId) {
            node.children.forEach { child ->
                if (child.children.isNotEmpty()) {
                    this.addNodeRecursively(child)
                } else {
                    addLeaf(child, id = child.fallbackId)
                }
            }
        }
    }

    @Composable
    fun TreeScope.addBranchNode(
        node: ComposeNode,
        children: @Composable (TreeScope.() -> Unit) = {},
    ) {
        Branch(
            composeNode = node,
            project = project,
            composeNodeCallbacks = composeNodeCallbacks,
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onFocusedStatusUpdated = onFocusedStatusUpdated,
            onHoveredStatusUpdated = onHoveredStatusUpdated,
            children = children,
        )
    }

    @Composable
    fun TreeScope.addLeafNode(node: ComposeNode) {
        Leaf(
            composeNode = node,
            project = project,
            composeNodeCallbacks = composeNodeCallbacks,
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onFocusedStatusUpdated = onFocusedStatusUpdated,
            onHoveredStatusUpdated = onHoveredStatusUpdated,
        )
    }

    @Composable
    fun TreeScope.addNodeRecursively(node: ComposeNode) {
        addBranchNode(node) {
            node.children.forEach { child ->
                if (child.children.isNotEmpty()) {
                    this.addNodeRecursively(child)
                } else {
                    addLeafNode(child)
                }
            }
        }
    }

    val myTree =
        io.github.vooft.compose.treeview.core.tree.Tree<ComposeNode> {
            if (editable is Screen) {
                addBranchNode(rootNode) {
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
                        addLeafNode(fabNode)
                    }
                }
            } else {
                addBranchNode(rootNode)
            }
        }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    myTree.setFocus(focusedNodes)
    var contextMenuExpanded by remember { mutableStateOf(false) }
    var addModifierDialogVisible by remember { mutableStateOf(false) }
    var convertToComponentNode by remember { mutableStateOf<ComposeNode?>(null) }

    val treeViewStyle =
        TreeViewStyle<ComposeNode>(
            toggleIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            nodeSelectedBackgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
            nodeCollapsedIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            nodeExpandedIconColorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            useHorizontalScroll = false,
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
            tree = myTree,
            project = project,
            listState = lazyListState,
            style = treeViewStyle,
            onClick = { treeNode ->
                val wasSelected = treeNode.isSelected

                myTree.clearSelection()

                if (!wasSelected) {
                    myTree.selectNode(treeNode)
                    myTree.expandNode(treeNode)
                }

                if (!treeNode.content.isFocused.value) {
                    project.screenHolder.clearIsFocused()
                    treeNode.content.setFocus()
                }
            },
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onVisibilityParamsUpdated = { node, params ->
                composeNodeCallbacks.onVisibilityParamsUpdated(node, params)
            },
        )

        if (contextMenuExpanded) {
            println("Context menu in ComposeNodeTree is expanded")
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

    /* if (focusedNodes.size == 1) {
         LaunchedEffect(focusedNodes.firstOrNull()?.fallbackId) {
             focusedNodes.firstOrNull()?.let { focused ->
                 val index = tree.findLazyListIndex(target = focused, treeState = treeState)
                 selectableLazyListState.scrollToItem(index, animateScroll = true)
                 lazyListState.scrollToItem(index)
             }
         }
     }*/
}

/**
 * Find the corresponding index for a [target] node in the Tree.
 * When a node is closed, the children inside that node aren't included in the LazyTree.
 *
 * Thus, we need to find the corresponding index in the form of visible items.
 */
private fun Tree<ComposeNode>.findLazyListIndex(
    target: ComposeNode,
    treeState: TreeState,
): Int {
    val queue = roots.toMutableList()
    var result = 0
    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        if (target.fallbackId == next.id) {
            return result
        }
        if (next is Tree.Element.Node && next.id in treeState.openNodes) {
            queue.addAll(0, next.children.orEmpty())
        }
        result += 1
    }
    return result
}

private fun TreeState.setFocus(nodes: List<ComposeNode>) {
    nodes.forEach { node ->
        if (!selectedKeys.contains(node.fallbackId)) {
            openNodes(node.findNodesUntilRoot().map { it.fallbackId })
        }
    }
    // Set the selected keys to show all focused nodes as selected
    selectedKeys = nodes.map { it.fallbackId }
}

private fun io.github.vooft.compose.treeview.core.tree.Tree<ComposeNode>.setFocus(composeNodes: List<ComposeNode>) {
    val currentSelected = selectedNodes.toMutableList()

    composeNodes.forEach { node ->
        expandNodes(node.findNodesUntilRoot())

        if (currentSelected.none { it.content.fallbackId == node.fallbackId }) {
            findNodeByFallbackId(node.fallbackId)?.let { currentSelected.add(it) }
        }
    }
    clearSelection()
    currentSelected.forEach { selectNode(it) }
}

private fun io.github.vooft.compose.treeview.core.tree.Tree<ComposeNode>.findNodeByFallbackId(id: String) =
    nodes.firstOrNull {
        it.content.fallbackId == id
    }

private fun io.github.vooft.compose.treeview.core.tree.Tree<ComposeNode>.expandNodes(composeNodes: List<ComposeNode>) {
    val treeNodes = nodes.asSequence().filterIsInstance<BranchNode<ComposeNode>>()

    composeNodes.forEach { node ->
        treeNodes
            .find { it.content.fallbackId == node.fallbackId }
            ?.setExpanded(true, Int.MAX_VALUE)
    }
}

@Composable
private fun TreeScope.Branch(
    composeNode: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onFocusedStatusUpdated: (ComposeNode) -> Unit,
    onHoveredStatusUpdated: (ComposeNode, Boolean) -> Unit,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
    children: @Composable (TreeScope.() -> Unit) = {},
) {
    Branch(
        content = composeNode,
        customIcon = { treeNode ->
            val node = treeNode.content
            if (treeNode.isSelected) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface
            }
            node.visibilityParams.value

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TreeNodeCustomIcon(treeNode.content, project)
                TreeNodeCustomName(
                    node = node,
                    project = project,
                    composeNodeCallbacks = composeNodeCallbacks,
                    onShowActionTab = onShowActionTab,
                    onShowInspectorTab = onShowInspectorTab,
                )
            }
        },
        customName = { treeNode -> },
    ) {
        children()
    }
}

@Composable
private fun TreeScope.Leaf(
    composeNode: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onFocusedStatusUpdated: (ComposeNode) -> Unit,
    onHoveredStatusUpdated: (ComposeNode, Boolean) -> Unit,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
) {
    Leaf(
        content = composeNode,
        customIcon = { treeNode ->
            val node = treeNode.content
            if (treeNode.isSelected) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface
            }
            node.visibilityParams.value

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TreeNodeCustomIcon(treeNode.content, project)
                TreeNodeCustomName(
                    node = node,
                    project = project,
                    composeNodeCallbacks = composeNodeCallbacks,
                    onShowActionTab = onShowActionTab,
                    onShowInspectorTab = onShowInspectorTab,
                )
            }
        },
        customName = { treeNode -> },
    )
}

@Composable
private fun TreeNodeCustomIcon(
    node: ComposeNode,
    project: Project,
) {
    Icon(
        imageVector = node.trait.value.icon(),
        contentDescription = "Tree node icon for ${node.displayName(project)}",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.size(16.dp),
    )
}

@Composable
private fun TreeNodeCustomName(
    node: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onShowActionTab: () -> Unit,
    onShowInspectorTab: () -> Unit,
) {
    val visibilityParams = node.visibilityParams.value

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
                        composeNodeCallbacks.onVisibilityParamsUpdated(
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
