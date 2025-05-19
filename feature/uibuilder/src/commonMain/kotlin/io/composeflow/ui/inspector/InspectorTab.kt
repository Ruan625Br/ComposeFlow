package io.composeflow.ui.inspector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.InspectorTabDestination
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.Tooltip
import io.composeflow.ui.inspector.action.ActionInspector
import io.composeflow.ui.inspector.codeviewer.CodeInspector
import io.composeflow.ui.tab.ComposeFlowTab

@Composable
fun InspectorTab(
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onResetPendingDestination: () -> Unit,
    selectedDestination: InspectorTabDestination?,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    LaunchedEffect(selectedDestination) {
        if (selectedDestination != null) {
            selectedTabIndex = selectedDestination.ordinal
        }
        onResetPendingDestination()
    }

    Column(
        modifier = modifier
            .width(420.dp)
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxHeight(),
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
        ) {
            InspectorTabDestination.entries.forEachIndexed { i, destination ->
                Tooltip(destination.contentDesc) {
                    ComposeFlowTab(
                        selected = selectedTabIndex == i,
                        onClick = {
                            selectedTabIndex = i
                        },
                        icon = {
                            Icon(
                                imageVector = destination.imageVector,
                                contentDescription = destination.contentDesc,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                }
            }
        }

        when (selectedTabIndex) {
            0 -> {
                PropertyInspector(
                    project = project,
                    composeNodeCallbacks = composeNodeCallbacks,
                )
            }

            1 -> {
                ActionInspector(
                    project = project,
                    composeNodeCallbacks = composeNodeCallbacks,
                )
            }

            2 -> {
                CodeInspector(
                    project = project,
                    onShowSnackbar = onShowSnackbar,
                )
            }
        }
    }
}
