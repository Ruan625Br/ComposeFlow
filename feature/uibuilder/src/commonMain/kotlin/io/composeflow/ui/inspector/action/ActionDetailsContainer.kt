package io.composeflow.ui.inspector.action

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.edit
import io.composeflow.model.action.Action
import io.composeflow.model.action.ActionNodeId
import io.composeflow.model.action.Auth
import io.composeflow.model.action.CallApi
import io.composeflow.model.action.DateOrTimePicker
import io.composeflow.model.action.FirestoreAction
import io.composeflow.model.action.FocusableActionNode
import io.composeflow.model.action.Navigation
import io.composeflow.model.action.Share
import io.composeflow.model.action.ShowConfirmationDialog
import io.composeflow.model.action.ShowInformationDialog
import io.composeflow.model.action.ShowMessaging
import io.composeflow.model.action.ShowModalWithComponent
import io.composeflow.model.action.ShowNavigationDrawer
import io.composeflow.model.action.StateAction
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.findApiDefinitionOrNull
import io.composeflow.not_defined
import io.composeflow.remove
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.popup.PositionCustomizablePopup
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionDetailsContainer(
    project: Project,
    focusableActionNode: FocusableActionNode?,
    composeNode: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onEditAction: (Action?) -> Unit,
    onRemoveActionNode: (ActionNodeId?) -> Unit,
) {
    var selectActionDialogOpen by remember { mutableStateOf(false) }
    val action = focusableActionNode?.getFocusedAction()
    Column(
        modifier = Modifier
            .padding(top = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                if (action != null) {
                    Text(
                        action.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Text(
                        stringResource(Res.string.not_defined),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            val editContentDescription = stringResource(Res.string.edit)
            Tooltip(editContentDescription) {
                ComposeFlowIconButton(onClick = {
                    selectActionDialogOpen = true
                    onEditAction(action)
                }) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = editContentDescription,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            val removeDescription = stringResource(Res.string.remove)
            Tooltip(removeDescription) {
                ComposeFlowIconButton(onClick = {
                    onRemoveActionNode(focusableActionNode?.id)
                }) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = removeDescription,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(8.dp)) {
            when (action) {
                Navigation.NavigateBack -> {}
                is Navigation.NavigateTo -> {
                    val screen = project.screenHolder.findScreen(action.screenId)
                    Text(
                        screen?.name ?: "",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    screen?.let {
                        NavigateToActionDetail(
                            project = project,
                            composeNode = composeNode,
                            composeNodeCallbacks = composeNodeCallbacks,
                            destinationScreen = screen,
                            initialParamsMap = action.paramsMap,
                            onParametersMapUpdated = { newParamsMap ->
                                val newAction = action.copy(paramsMap = newParamsMap)
                                onEditAction(newAction)
                            }
                        )
                    }
                }

                is StateAction.SetAppStateValue -> {
                    SetStateContentDetail(
                        project = project,
                        action = action,
                        node = composeNode,
                        onActionUpdated = {
                            onEditAction(it)
                        },
                    )
                }

                is CallApi -> {
                    val api = project.findApiDefinitionOrNull(action.apiId)
                    api?.let {
                        Text(
                            api.name,
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        CallApiActionDetail(
                            project = project,
                            composeNode = composeNode,
                            composeNodeCallbacks = composeNodeCallbacks,
                            apiDefinition = api,
                            initialParamsMap = action.paramsMap,
                            onParametersMapUpdated = { newParamsMap ->
                                val newAction = action.copy(paramsMap = newParamsMap)
                                onEditAction(newAction)
                            }
                        )
                    }
                }

                is ShowInformationDialog -> {
                    ShowInformationDialogContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is ShowModalWithComponent -> {
                    ShowModalWithComponentContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is ShowConfirmationDialog -> {
                    ShowConfirmationDialogContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is ShowNavigationDrawer -> {
                    ShowNavigationDrawerContent(
                        project = project,
                        initialAction = action,
                    )
                }

                is ShowMessaging.Snackbar -> {
                    ShowSnackbarContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is DateOrTimePicker.OpenDatePicker -> {
                    OpenDatePickerContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is DateOrTimePicker.OpenDateAndTimePicker -> {
                    OpenDatePickerContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is Share.OpenUrl -> {
                    OpenUrlContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is Auth.SignInWithGoogle -> {}
                is Auth.CreateUserWithEmailAndPassword -> {
                    CreateUserWithEmailAndPasswordContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is Auth.SignInWithEmailAndPassword -> {
                    SignInWithEmailAndPasswordContent(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is Auth.SignOut -> {}
                is FirestoreAction.SaveToFirestore -> {
                    SaveToFirestoreContentDetail(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is FirestoreAction.UpdateDocument -> {
                    UpdateFirestoreDocumentContentDetail(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                is FirestoreAction.DeleteDocument -> {
                    DeleteFirestoreDocumentContentDetail(
                        project = project,
                        composeNode = composeNode,
                        initialAction = action,
                        onEditAction = {
                            onEditAction(it)
                        }
                    )
                }

                null -> {}
            }
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (selectActionDialogOpen) {
        onAnyDialogIsShown()
        val closeDialog = {
            selectActionDialogOpen = false
            onAllDialogsClosed()
        }
        PositionCustomizablePopup(
            expanded = selectActionDialogOpen,
            onDismissRequest = {
                closeDialog()
            },
            onKeyEvent = {
                if (it.key == Key.Escape) {
                    closeDialog()
                    true
                } else {
                    false
                }
            },
        ) {
            AddActionDialogContent(
                project = project,
                actionInEdit = action,
                onActionSelected = {
                    onEditAction(it)
                },
                onCloseClick = {
                    closeDialog()
                },
            )
        }
    }
}
