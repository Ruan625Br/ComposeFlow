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
import io.composeflow.model.action.Action
import io.composeflow.model.action.Share
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.StringProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.utils.TreeExpanderInverse

@Composable
fun ShareContent(
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    var shareActionsOpened by remember { mutableStateOf(true) }
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                shareActionsOpened = !shareActionsOpened
            },
        ) {
            Text(
                text = "Share",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            TreeExpanderInverse(
                expanded = shareActionsOpened,
                onClick = {
                    shareActionsOpened = !shareActionsOpened
                },
            )
        }
        if (shareActionsOpened) {
            Share.entries().forEach { shareAction ->
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
                                shareAction
                            )
                        }
                        .selectedActionModifier(
                            actionInEdit = actionInEdit,
                            predicate = {
                                actionInEdit != null &&
                                        actionInEdit is Share &&
                                        actionInEdit.name == shareAction.name
                            },
                        ),
                ) {
                    Text(
                        text = shareAction.name,
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
fun OpenUrlContent(
    project: Project,
    composeNode: ComposeNode,
    initialAction: Share.OpenUrl,
    onEditAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = composeNode,
            acceptableType = ComposeFlowType.StringType(),
            initialProperty = initialAction.url,
            label = "Url",
            onValidPropertyChanged = { property, _ ->
                val newAction = initialAction.copy(url = property)
                onEditAction(newAction)
            },
            modifier = Modifier.hoverOverlay(),
            onInitializeProperty = {
                val newAction = initialAction.copy(url = StringProperty.StringIntrinsicValue(""))
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