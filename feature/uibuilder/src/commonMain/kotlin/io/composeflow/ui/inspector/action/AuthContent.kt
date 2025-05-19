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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.enable_auth_to_proceed
import io.composeflow.model.action.Action
import io.composeflow.model.action.Auth
import io.composeflow.model.action.Share
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.StringProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun AuthContent(
    project: Project,
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    val authEnabled = project.firebaseAppInfoHolder.firebaseAppInfo.authenticationEnabled.value

    var authActionsOpened by remember { mutableStateOf(authEnabled) }
    val enableAuthToProceed = stringResource(Res.string.enable_auth_to_proceed)
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        if (authEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    authActionsOpened = !authActionsOpened
                },
            ) {
                Text(
                    text = "Auth",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
                Spacer(Modifier.weight(1f))
                TreeExpanderInverse(
                    expanded = authActionsOpened,
                    onClick = {
                        authActionsOpened = !authActionsOpened
                    },
                )
            }
        } else {
            Tooltip(enableAuthToProceed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Auth",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                            .alpha(0.5f),
                    )
                    Spacer(Modifier.weight(1f))
                    TreeExpanderInverse(
                        expanded = authActionsOpened,
                        onClick = {
                            authActionsOpened = !authActionsOpened
                        },
                        enabled = false
                    )
                }
            }
        }
        if (authActionsOpened) {
            Auth.entries().forEach { authAction ->
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
                                authAction
                            )
                        }
                        .selectedActionModifier(
                            actionInEdit = actionInEdit,
                            predicate = {
                                actionInEdit != null &&
                                        actionInEdit is Share &&
                                        actionInEdit.name == authAction.name
                            },
                        ),
                ) {
                    Text(
                        text = authAction.name,
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
fun CreateUserWithEmailAndPasswordContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: Auth.CreateUserWithEmailAndPassword,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.email,
            label = "Email",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(email = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(email = StringProperty.StringIntrinsicValue(""))
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
            initialProperty = initialAction.password,
            label = "Password",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(password = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction =
                    initialAction.copy(password = StringProperty.StringIntrinsicValue(""))
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

// TODO: It can be merged with CreateUserWithEmailAndPasswordContent
@Composable
fun SignInWithEmailAndPasswordContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: Auth.SignInWithEmailAndPassword,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.email,
            label = "Email",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(email = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(email = StringProperty.StringIntrinsicValue(""))
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
            initialProperty = initialAction.password,
            label = "Password",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(password = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction =
                    initialAction.copy(password = StringProperty.StringIntrinsicValue(""))
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