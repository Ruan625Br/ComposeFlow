package io.composeflow.ui.inspector.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.composeflow.Res
import io.composeflow.add_parameter
import io.composeflow.copyAsMutableStateMap
import io.composeflow.model.parameter.ComponentTrait
import io.composeflow.model.project.ParameterEditor
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.component.Component
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComponentInspector(
    project: Project,
    node: ComposeNode,
    component: Component,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    var addParameterDialogOpen by remember { mutableStateOf(false) }
    val trait = node.trait.value as ComponentTrait
    Column(modifier = modifier) {
        component.parameters.forEach { parameter ->
            val initialProperty =
                trait.paramsMap[parameter.id] ?: parameter.defaultValueAsAssignableProperty
            ParameterEditor(
                project = project,
                node = node,
                parameter = parameter,
                initialProperty = initialProperty,
                onValidPropertyChanged = { newProperty, lazyListSource ->
                    val newMap = trait.paramsMap.copyAsMutableStateMap().apply {
                        putAll(trait.paramsMap)
                    }
                    newMap[parameter.id] = newProperty
                    composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        trait.copy(paramsMap = newMap),
                        lazyListSource,
                    )
                },
                onInitializeProperty = {
                    val newMap = trait.paramsMap.copyAsMutableStateMap().apply {
                        putAll(trait.paramsMap)
                    }
                    newMap.remove(parameter.id)
                    composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        trait.copy(paramsMap = newMap),
                        null,
                    )
                }
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
                composeNodeCallbacks.onAddParameterToCanvasEditable(component, it)
            },
        )
    }
}
