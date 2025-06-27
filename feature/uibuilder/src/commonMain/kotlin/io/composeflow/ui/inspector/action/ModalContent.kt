package io.composeflow.ui.inspector.action

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.copyAsMutableStateMap
import io.composeflow.dialog_bottom_sheet_drawer
import io.composeflow.model.action.Action
import io.composeflow.model.action.ShowConfirmationDialog
import io.composeflow.model.action.ShowCustomDialog
import io.composeflow.model.action.ShowInformationDialog
import io.composeflow.model.action.ShowModal
import io.composeflow.model.action.ShowModalWithComponent
import io.composeflow.model.action.ShowNavigationDrawer
import io.composeflow.model.project.ParameterEditor
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.StringProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.parameters
import io.composeflow.show_navigation_warning_screen_does_not_have_nav_drawer
import io.composeflow.ui.common.warning
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.DropdownProperty
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShowModalActionContent(
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    var showDialogActionsOpened by remember { mutableStateOf(true) }
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                showDialogActionsOpened = !showDialogActionsOpened
            },
        ) {
            Text(
                text = stringResource(Res.string.dialog_bottom_sheet_drawer),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            TreeExpanderInverse(
                expanded = showDialogActionsOpened,
                onClick = {
                    showDialogActionsOpened = !showDialogActionsOpened
                },
            )
        }
        if (showDialogActionsOpened) {
            ShowModal.entries().forEach { showDialogAction ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .hoverIconClickable()
                        .hoverOverlay()
                        .padding(vertical = 4.dp)
                        .padding(start = 8.dp)
                        .clickable {
                            onActionSelected(
                                showDialogAction
                            )
                        }
                        .selectedActionModifier(
                            actionInEdit = actionInEdit,
                            predicate = {
                                actionInEdit != null &&
                                        actionInEdit is ShowModal &&
                                        actionInEdit.name == showDialogAction.name
                            },
                        ),
                ) {
                    Text(
                        text = showDialogAction.name,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ShowInformationDialogContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: ShowInformationDialog,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.title,
            label = "Title",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(title = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(title = null)
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.message,
            label = "Message",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(message = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(message = null)
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.confirmText,
            label = "Confirm text",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(confirmText = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction =
                    initialAction.copy(confirmText = StringProperty.StringIntrinsicValue(""))
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
fun ShowConfirmationDialogContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: ShowConfirmationDialog,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.title,
            label = "Title",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(title = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(title = null)
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.message,
            label = "Message",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(message = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(message = null)
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.positiveText,
            label = "Positive text",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(positiveText = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction =
                    initialAction.copy(positiveText = StringProperty.StringIntrinsicValue(""))
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )

        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.negativeText,
            label = "Negative text",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(negativeText = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction =
                    initialAction.copy(negativeText = StringProperty.StringIntrinsicValue(""))
                onEditAction(newAction)
            },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.TextFields,
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
fun ShowModalWithComponentContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: ShowModalWithComponent,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val components = project.componentHolder.components
        val selectedIndex = components.indexOfFirst { it.id == initialAction.componentId }
        Row(modifier = Modifier.fillMaxWidth()) {
            LabeledBorderBox(
                label = "Inside Component",
                modifier = Modifier.weight(1f),
            ) {

                DropdownProperty(
                    project = project,
                    items = components.map { it.name },
                    onValueChanged = { index, _ ->
                        if (index <= components.size) {
                            onEditAction(
                                ShowCustomDialog(
                                    componentId = components[index].id
                                )
                            )
                        }
                    },
                    selectedIndex = if (selectedIndex == -1) 0 else selectedIndex
                )
            }
        }

        val selectedComponent = if (selectedIndex != -1) components[selectedIndex] else null
        selectedComponent?.let { component ->
            if (component.parameters.isNotEmpty()) {

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        stringResource(Res.string.parameters),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    component.parameters.forEach { parameter ->
                        val initialProperty = initialAction.paramsMap[parameter.id]
                            ?: parameter.defaultValueAsAssignableProperty
                        ParameterEditor(
                            project = project,
                            node = composeNode,
                            parameter = parameter,
                            initialProperty = initialProperty,
                            onValidPropertyChanged = { newProperty, _ ->
                                val newMap = initialAction.paramsMap.copyAsMutableStateMap().apply {
                                    putAll(initialAction.paramsMap)
                                }
                                newMap[parameter.id] = newProperty
                                val newAction = initialAction.copy(paramsMap = newMap)
                                onEditAction(newAction)
                            },
                            onInitializeProperty = {
                                val newMap = initialAction.paramsMap.copyAsMutableStateMap().apply {
                                    putAll(initialAction.paramsMap)
                                }
                                newMap.remove(parameter.id)
                                val newAction = initialAction.copy(paramsMap = newMap)
                                onEditAction(newAction)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowNavigationDrawerContent(
    project: Project,
    initialAction: ShowNavigationDrawer,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val screenHavingThisAction = project.screenHolder.screens.firstOrNull { screen ->
            screen.getAllComposeNodes().firstOrNull { node ->
                node.allActions().any { action ->
                    action.id == initialAction.id
                }
            } != null
        }
        if (screenHavingThisAction != null && screenHavingThisAction.navigationDrawerNode.value == null) {
            Text(
                text = stringResource(Res.string.show_navigation_warning_screen_does_not_have_nav_drawer),
                color = MaterialTheme.colorScheme.warning,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}