package io.composeflow.ui.inspector.dynamicitems

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
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
import io.composeflow.bind_dynamic_items
import io.composeflow.dynamic_items
import io.composeflow.dynamic_items_description
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.leadingIcon
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.variable.SetFromStateDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun DynamicItemsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.dynamic_items),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )

            val dynamicItemDescription = stringResource(Res.string.dynamic_items_description)
            Tooltip(dynamicItemDescription) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = dynamicItemDescription,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier =
                        Modifier
                            .padding(start = 8.dp)
                            .size(18.dp),
                )
            }

            ComposeFlowIconButton(
                onClick = {
                    dialogOpen = true
                },
                modifier = Modifier.padding(start = 16.dp).hoverOverlay().hoverIconClickable(),
            ) {
                val bindDynamicItemsDesc = stringResource(Res.string.bind_dynamic_items)
                Tooltip(bindDynamicItemsDesc) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.ElectricalServices,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = bindDynamicItemsDesc,
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(start = 8.dp)) {
            node.dynamicItems.value?.let { dynamicItems ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp),
                ) {
                    Icon(
                        imageVector = dynamicItems.transformedValueType(project).leadingIcon(),
                        contentDescription = "Icon",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = dynamicItems.transformedValueExpression(project),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
    }

    val anyDialogIsOpen = LocalOnAnyDialogIsShown.current
    val allDialogsAreClosed = LocalOnAllDialogsClosed.current
    if (dialogOpen) {
        anyDialogIsOpen()
        SetFromStateDialog(
            project = project,
            node = node,
            acceptableType = ComposeFlowType.AnyType(isList = true),
            onValidPropertyChanged = { property, _ ->
                composeNodeCallbacks.onDynamicItemsUpdated(node, property)
            },
            initialProperty = node.dynamicItems.value,
            defaultValue = ComposeFlowType.AnyType(isList = true).defaultValue(),
            onInitializeProperty = {
                composeNodeCallbacks.onDynamicItemsUpdated(node, null)
            },
            onCloseClick = {
                dialogOpen = false
                allDialogsAreClosed()
            },
        )
    }
}
