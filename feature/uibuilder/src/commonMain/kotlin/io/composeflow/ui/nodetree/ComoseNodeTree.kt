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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import io.composeflow.ui.jewel.SingleSelectionLazyTree
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.mousePointerEvents
import io.composeflow.ui.popup.SingleTextInputDialog
import io.composeflow.ui.uibuilder.UiBuilderContextMenuDropDown
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.foundation.lazy.SelectableLazyListState
import org.jetbrains.jewel.foundation.lazy.tree.Tree
import org.jetbrains.jewel.foundation.lazy.tree.TreeGeneratorScope
import org.jetbrains.jewel.foundation.lazy.tree.TreeState
import org.jetbrains.jewel.foundation.lazy.tree.buildTree
import org.jetbrains.jewel.foundation.lazy.tree.rememberTreeState
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.component.styling.LocalLazyTreeStyle

@Composable
fun ComposeNodeTree(
    project: Project,
    canvasNodeCallbacks: CanvasNodeCallbacks,
    composeNodeCallbacks: ComposeNodeCallbacks,
    copiedNode: ComposeNode?,
    onFocusedStatusUpdated: (ComposeNode) -> Unit,
    onHoveredStatusUpdated: (ComposeNode, Boolean) -> Unit,
    onShowInspectorTab: () -> Unit,
    onShowActionTab: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val editable = project.screenHolder.currentEditable()
    val rootNode = editable.getRootNode()
    val focusedNode = project.screenHolder.findFocusedNodeOrNull()
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

    val tree = if (editable is Screen) {
        buildTree<ComposeNode> {
            addNode(rootNode, rootNode.fallbackId) {
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
                    addLeaf(fabNode, id = fabNode.fallbackId)
                }
            }
        }
    } else {
        buildTree {
            addNodeRecursively(rootNode)
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val selectableLazyListState = remember { SelectableLazyListState(lazyListState) }
    val treeState = rememberTreeState(selectableLazyListState = selectableLazyListState)
    focusedNode?.let { focused ->
        treeState.setFocus(focused)
    }
    var contextMenuExpanded by remember { mutableStateOf(false) }
    var addModifierDialogVisible by remember { mutableStateOf(false) }
    var convertToComponentNode by remember { mutableStateOf<ComposeNode?>(null) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .onClick(
                matcher = PointerMatcher.mouse(PointerButton.Secondary),
            ) {
                contextMenuExpanded = true
            },
    ) {
        SingleSelectionLazyTree(
            tree = tree,
            treeState = treeState,
            style = LocalLazyTreeStyle.current,
            onSelectionChange = {
                if (it.isNotEmpty() && !it.first().data.isFocused.value) {
                    project.screenHolder.clearIsFocused()
                    val element = it.first()
                    element.data.setFocus()
                }
            },
        ) {
            val visibilityParams = it.data.visibilityParams.value
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .alpha(if (visibilityParams.visibleInUiBuilder) 1f else 0.5f)
                    .mousePointerEvents(
                        node = it.data,
                        onFocusedStatusUpdated = onFocusedStatusUpdated,
                        onHoveredStatusUpdated = onHoveredStatusUpdated,
                    ),
            ) {
                Icon(
                    imageVector = it.data.trait.value.icon(),
                    contentDescription = "Tree node icon for ${it.data.displayName(project)}",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    it.data.displayName(project),
                    color = if (it.data.generateTrackableIssues(project).isNotEmpty()) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                )

                val allActions = it.data.allActions()
                if (allActions.isNotEmpty()) {
                    val contentDesc = if (allActions.size == 1) {
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
                            modifier = Modifier.size(16.dp)
                                .hoverIconClickable()
                                .clickable {
                                    onShowActionTab()
                                },
                        )
                    }
                }
                if (visibilityParams.nodeVisibilityValue() != NodeVisibility.AlwaysVisible ||
                    !visibilityParams.formFactorVisibility.alwaysVisible()
                ) {
                    val contentDesc =
                        "Visible if " + visibilityParams.visibilityCondition.transformedValueExpression(
                            project,
                        )
                    Tooltip({
                        Column {
                            Text(
                                buildAnnotatedString {
                                    append("Visible if ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.tertiary,
                                        ),
                                    ) {
                                        append(
                                            visibilityParams.visibilityCondition.transformedValueExpression(
                                                project
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
                                    val iconModifier = if (visible) {
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
                            imageVector = if (visibilityParams.visibleInUiBuilder) {
                                Icons.Outlined.Visibility
                            } else {
                                Icons.Outlined.VisibilityOff
                            },
                            contentDescription = contentDesc,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                                .hoverIconClickable()
                                .clickable {
                                    onShowInspectorTab()
                                    composeNodeCallbacks.onVisibilityParamsUpdated(
                                        it.data,
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
    }

    LaunchedEffect(focusedNode?.fallbackId) {
        focusedNode?.let { focused ->
            val index = tree.findLazyListIndex(target = focused, treeState = treeState)
            selectableLazyListState.scrollToItem(index, animateScroll = true)
        }
    }

    if (contextMenuExpanded) {
        UiBuilderContextMenuDropDown(
            project = project,
            canvasNodeCallbacks = canvasNodeCallbacks,
            composeNodeCallbacks = composeNodeCallbacks,
            copiedNode = copiedNode,
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
        project.screenHolder.findFocusedNodeOrNull()?.let { focused ->
            val modifiers = ModifierWrapper.values().filter { m ->
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
        if (next is Tree.Element.Node &&
            next.id in treeState.openNodes
        ) {
            queue.addAll(0, next.children.orEmpty())
        }
        result += 1
    }
    return result
}

private fun TreeState.setFocus(node: ComposeNode) {
    if (!selectedKeys.contains(node.fallbackId)) {
        openNodes(node.findNodesUntilRoot().map { it.fallbackId })
        selectedKeys = listOf(node.fallbackId)
    }
}
