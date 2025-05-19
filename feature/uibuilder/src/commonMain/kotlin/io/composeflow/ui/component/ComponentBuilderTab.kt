package io.composeflow.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_new_component
import io.composeflow.component
import io.composeflow.component_description
import io.composeflow.model.palette.PaletteNodeCallbacks
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.popup.SingleTextInputDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComponentBuilderTab(
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
    paletteNodeCallbacks: PaletteNodeCallbacks,
    modifier: Modifier,
) {
    var addNewComponentDialogOpen by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.component),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
            val componentDescription = stringResource(Res.string.component_description)
            Tooltip(componentDescription) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = componentDescription,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 8.dp)
                        .size(16.dp),
                )
            }
            Spacer(Modifier.size(8.dp))
            val addNewComponent = stringResource(Res.string.add_new_component)
            Tooltip(addNewComponent) {
                ComposeFlowIconButton(
                    onClick = {
                        addNewComponentDialogOpen = true
                    }
                ) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = addNewComponent
                    )
                }
            }
        }
        LazyColumn {
            item {
                Spacer(Modifier.size(16.dp))
            }
            items(project.componentHolder.components) {
                it.Thumbnail(
                    project = project,
                    composeNodeCallbacks = composeNodeCallbacks,
                    paletteNodeCallbacks = paletteNodeCallbacks,
                )
                Spacer(Modifier.size(8.dp))
            }
            item {
                Spacer(Modifier.size(8.dp))
            }
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (addNewComponentDialogOpen) {
        onAnyDialogIsShown()

        val onCloseDialog = {
            onAllDialogsClosed()
            addNewComponentDialogOpen = false
        }
        SingleTextInputDialog(
            textLabel = "Component name",
            onTextConfirmed = {
                composeNodeCallbacks.onCreateComponent(it)
                onCloseDialog()
            },
            onDismissDialog = onCloseDialog
        )
    }
}
