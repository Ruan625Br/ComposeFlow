package io.composeflow.ui.nodetree

import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerButton
import io.composeflow.Res
import io.composeflow.component_name
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.CanvasNodeCallbacks
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.inspector.modifier.AddModifierDialog
import io.composeflow.ui.popup.SingleTextInputDialog
import io.composeflow.ui.uibuilder.UiBuilderContextMenuDropDown
import io.github.vooft.compose.treeview.core.TreeViewStyle
import io.github.vooft.compose.treeview.core.node.Branch
import io.github.vooft.compose.treeview.core.node.Leaf
import io.github.vooft.compose.treeview.core.tree.TreeScope
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.lazy.tree.Tree
import org.jetbrains.jewel.foundation.lazy.tree.TreeGeneratorScope
import org.jetbrains.jewel.foundation.lazy.tree.TreeState

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
    fun TreeScope.addNodeRecursively(node: ComposeNode) {
        Branch(node) {
            node.children.forEach { child ->
                if (child.children.isNotEmpty()) {
                    this.addNodeRecursively(child)
                } else {
                    Leaf(child)
                }
            }
        }
    }

    val tree =
        io.github.vooft.compose.treeview.core.tree.Tree<ComposeNode> {
            if (editable is Screen) {
                Branch(rootNode) {
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
                        Leaf(fabNode)
                    }
                }
            } else {
                Branch(rootNode)
            }
        }

    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

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
        /*   onSelectionChange = { selectedElements ->
               if (selectedElements.isNotEmpty()) {
                   // Check if any of the selected nodes are not currently focused
                   val hasUnfocusedNodes = selectedElements.any { !it.data.isFocused.value }

                   if (hasUnfocusedNodes) {
                       project.screenHolder.clearIsFocused()
                       // Set focus on all selected nodes
                       selectedElements.forEach { element ->
                           element.data.setFocus()
                       }
                   }
               }
           },*/

        TreeView(
            modifier = Modifier.fillMaxWidth(),
            tree = tree,
            project = project,
            listState = lazyListState,
            style = treeViewStyle,
            onClick = { treeNode ->
                project.screenHolder.clearIsFocused()
                treeNode.content.setFocus()
            },
            onShowActionTab = onShowActionTab,
            onShowInspectorTab = onShowInspectorTab,
            onVisibilityParamsUpdated = { node, params ->
                composeNodeCallbacks.onVisibilityParamsUpdated(node, params)
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

    LaunchedEffect(focusedNodes, tree) {
        tree.setFocus(focusedNodes)
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
