package io.composeflow.ui.inspector.action

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Note
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.composeflow.add_parameter
import io.composeflow.model.action.Action
import io.composeflow.model.action.Navigation
import io.composeflow.model.project.ParameterEditor
import io.composeflow.model.project.ParameterId
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.AssignableProperty
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.inspector.component.AddParameterDialog
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigateToActionContent(
    project: Project,
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    var navigationActionsOpened by remember { mutableStateOf(true) }
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.clickable {
                    navigationActionsOpened = !navigationActionsOpened
                },
        ) {
            Text(
                text = "Navigate to",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            TreeExpanderInverse(
                expanded = navigationActionsOpened,
                onClick = {
                    navigationActionsOpened = !navigationActionsOpened
                },
            )
        }
        if (navigationActionsOpened) {
            project.screenHolder.screens.forEach { screen ->

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .hoverIconClickable()
                            .hoverOverlay()
                            .padding(vertical = 4.dp)
                            .padding(start = 8.dp)
                            .clickable {
                                onActionSelected(
                                    Navigation.NavigateTo(screenId = screen.id),
                                )
                            }.selectedActionModifier(
                                actionInEdit = actionInEdit,
                                predicate = {
                                    actionInEdit != null &&
                                        actionInEdit is Navigation.NavigateTo &&
                                        actionInEdit.screenId == screen.id
                                },
                            ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Note,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier
                                .padding(start = 4.dp)
                                .size(16.dp),
                    )

                    Text(
                        text = screen.name,
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
fun NavigateBackContent(
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .clickable {
                    onActionSelected(Navigation.NavigateBack)
                }.fillMaxWidth()
                .hoverIconClickable()
                .hoverOverlay()
                .padding(vertical = 4.dp)
                .padding(start = 4.dp)
                .then(
                    Modifier.selectedActionModifier(
                        actionInEdit = actionInEdit,
                        predicate = {
                            it != null &&
                                it is Navigation.NavigateBack
                        },
                    ),
                ),
    ) {
        Text(
            text = "Navigate back",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun NavigateToActionDetail(
    project: Project,
    composeNode: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    destinationScreen: Screen,
    initialParamsMap: MutableMap<ParameterId, AssignableProperty>,
    onParametersMapUpdated: (MutableMap<ParameterId, AssignableProperty>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var addParameterDialogOpen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        destinationScreen.parameters.forEach { parameter ->
            val initialProperty =
                initialParamsMap[parameter.id] ?: parameter.defaultValueAsAssignableProperty
            ParameterEditor(
                project = project,
                node = composeNode,
                parameter = parameter,
                initialProperty = initialProperty,
                onValidPropertyChanged = { newProperty, _ ->
                    initialParamsMap[parameter.id] = newProperty
                    onParametersMapUpdated(initialParamsMap)
                },
                onInitializeProperty = {
                    initialParamsMap.remove(parameter.id)
                    onParametersMapUpdated(initialParamsMap)
                },
            )
        }
        TextButton(onClick = {
            addParameterDialogOpen = true
        }) {
            Text("+ " + stringResource(Res.string.add_parameter))
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (addParameterDialogOpen) {
        onAnyDialogIsShown()
        val closeDialog = {
            addParameterDialogOpen = false
            onAllDialogsClosed()
        }
        AddParameterDialog(
            project = project,
            onDialogClosed = closeDialog,
            onParameterConfirmed = {
                composeNodeCallbacks.onAddParameterToCanvasEditable(destinationScreen, it)
            },
            listTypeAllowed = false,
        )
    }
}
