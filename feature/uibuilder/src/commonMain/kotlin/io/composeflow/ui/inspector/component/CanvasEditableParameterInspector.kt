package io.composeflow.ui.inspector.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_parameter
import io.composeflow.model.project.CanvasEditable
import io.composeflow.model.project.ParameterWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.component.Component
import io.composeflow.parameter_description_component
import io.composeflow.parameter_description_screen
import io.composeflow.parameters
import io.composeflow.remove
import io.composeflow.remove_parameter
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.datafield.FieldRow
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun ComponentParameterInspector(
    project: Project,
    component: Component,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    CanvasEditableParameterInspector(
        project = project,
        canvasEditable = component,
        composeNodeCallbacks = composeNodeCallbacks,
        modifier = modifier,
    )
}

@Composable
fun ScreenParameterInspector(
    project: Project,
    screen: Screen,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    CanvasEditableParameterInspector(
        project = project,
        canvasEditable = screen,
        composeNodeCallbacks = composeNodeCallbacks,
        modifier = modifier,
        listTypeAllowed = false,
    )
}

@Composable
fun CanvasEditableParameterInspector(
    project: Project,
    canvasEditable: CanvasEditable,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
    availableParameters: List<ParameterWrapper<*>> = ParameterWrapper.entries(),
    listTypeAllowed: Boolean = true,
) {
    var addParameterDialogOpen by remember { mutableStateOf(false) }
    var parameterToBeRemoved by remember { mutableStateOf<ParameterWrapper<*>?>(null) }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        val editableId =
            when (canvasEditable) {
                is Screen -> canvasEditable.id
                is Component -> canvasEditable.id
                else -> null
            }
        var expanded by remember(editableId) { mutableStateOf(true) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .hoverIconClickable(),
        ) {
            Text(
                text = "${canvasEditable.name} ${stringResource(Res.string.parameters).lowercase()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp),
            )

            val parameterDesc =
                when (canvasEditable) {
                    is Screen -> stringResource(Res.string.parameter_description_screen)
                    is Component -> stringResource(Res.string.parameter_description_component)
                    else -> ""
                }
            Tooltip(parameterDesc) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = parameterDesc,
                    modifier = Modifier.padding(start = 8.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }

            val addParameter = stringResource(Res.string.add_parameter)
            Tooltip(addParameter) {
                ComposeFlowIconButton(onClick = {
                    addParameterDialogOpen = true
                }) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = addParameter,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            TreeExpanderInverse(
                expanded = expanded,
                onClick = { expanded = !expanded },
            )
        }

        if (expanded) {
            canvasEditable.parameters.forEach { parameter ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .hoverOverlay()
                            .padding(vertical = 4.dp)
                            .padding(start = 8.dp),
                ) {
                    FieldRow(
                        project = project,
                        fieldName = parameter.variableName,
                        type = parameter.parameterType,
                        displayNameListAware = true,
                    )
                    Spacer(Modifier.weight(1f))

                    val removeParameter = stringResource(Res.string.remove)
                    Tooltip(removeParameter) {
                        ComposeFlowIconButton(onClick = {
                            parameterToBeRemoved = parameter
                        }) {
                            ComposeFlowIcon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = removeParameter,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (addParameterDialogOpen) {
        onAnyDialogIsShown()
        val closeDialog = {
            onAllDialogsClosed()
            addParameterDialogOpen = false
        }
        AddParameterDialog(
            project = project,
            onDialogClosed = {
                closeDialog()
            },
            onParameterConfirmed = { parameter ->
                composeNodeCallbacks.onAddParameterToCanvasEditable(canvasEditable, parameter)
                closeDialog()
            },
            availableParameters = availableParameters,
            listTypeAllowed = listTypeAllowed,
        )
    }

    parameterToBeRemoved?.let {
        onAnyDialogIsShown()
        val closeDialog = {
            onAllDialogsClosed()
            parameterToBeRemoved = null
        }
        SimpleConfirmationDialog(
            text = "${stringResource(Res.string.remove_parameter)}?",
            onCloseClick = {
                closeDialog()
            },
            onConfirmClick = {
                composeNodeCallbacks.onRemoveParameterFromCanvasEditable(canvasEditable, it)
                closeDialog()
            },
        )
    }
}
