package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_action_icon
import io.composeflow.model.parameter.BottomAppBarTrait
import io.composeflow.model.parameter.IconTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.BottomAppBarNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomAppBarParamsInspector(
    node: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as BottomAppBarTrait
    val bottomAppBarNode = node as BottomAppBarNode
    val currentEditable = project.screenHolder.currentEditable()
    if (currentEditable !is Screen) return
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
                .height(42.dp),
        ) {
            Text(
                text = "Action Icons",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
            )

            ComposeFlowIconButton(
                onClick = {
                    bottomAppBarNode.addBottomAppbarActionIcon()
                    composeNodeCallbacks.onTraitUpdated(node, trait)
                },
                modifier = Modifier
                    .padding(start = 28.dp)
                    .hoverOverlay()
                    .hoverIconClickable(),
            ) {
                val contentDesc = stringResource(Res.string.add_action_icon)
                Tooltip(contentDesc) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = contentDesc,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        val actionIcons = bottomAppBarNode.getBottomAppBarActionIcons()
        actionIcons.forEachIndexed { i, icon ->
            IconPropertyEditor(
                label = "Icon $i",
                onIconDeleted = {
                    bottomAppBarNode.removeBottomAppBarActionIcon(i)
                    composeNodeCallbacks.onTraitUpdated(node, trait)
                },
                onIconSelected = {
                    val iconTrait = icon.trait.value as IconTrait
                    icon.trait.value =
                        iconTrait.copy(imageVectorHolder = it)
                    composeNodeCallbacks.onTraitUpdated(icon, icon.trait.value)
                },
                currentIcon = (icon.trait.value as IconTrait).imageVectorHolder?.imageVector,
                modifier = Modifier
                    .hoverOverlay()
                    .padding(start = 24.dp),
            )
        }

        BooleanPropertyEditor(
            checked = trait.showFab,
            label = "Show Fab",
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, trait.copy(showFab = it))
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
