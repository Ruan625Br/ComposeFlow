package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.parameter.NavigationDrawerTrait
import io.composeflow.model.parameter.NavigationDrawerType
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun NavigationDrawerParamsInspector(
    node: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as NavigationDrawerTrait
    val currentEditable = project.screenHolder.currentEditable()
    if (currentEditable !is Screen) return
    Column {
        Button(
            onClick = {
                trait.expandedInCanvas.value = !trait.expandedInCanvas.value
            },
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        ) {
            Text(
                if (trait.expandedInCanvas.value) {
                    "Close drawer"
                } else {
                    "Open drawer"
                }
            )
        }
        BooleanPropertyEditor(
            checked = trait.gesturesEnabled,
            label = "Gestures enabled",
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, trait.copy(gesturesEnabled = it))
            },
            modifier = Modifier.hoverOverlay(),
        )

        BasicDropdownPropertyEditor(
            project = project,
            items = NavigationDrawerType.entries,
            label = "Drawer type",
            selectedIndex = trait.navigationDrawerType.ordinal,
            onValueChanged = { _, item ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        navigationDrawerType = item,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay()
        )
    }
}
