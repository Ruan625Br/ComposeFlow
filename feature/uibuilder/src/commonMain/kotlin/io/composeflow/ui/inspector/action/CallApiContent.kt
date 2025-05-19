package io.composeflow.ui.inspector.action

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.composeflow.add_parameter
import io.composeflow.model.action.Action
import io.composeflow.model.action.CallApi
import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.project.ParameterId
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.StringProperty
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.apieditor.ui.AddApiParameterDialog
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.utils.TreeExpanderInverse
import org.jetbrains.compose.resources.stringResource

@Composable
fun CallApiActionContent(
    project: Project,
    actionInEdit: Action?,
    onActionSelected: (Action) -> Unit,
) {
    var callApiActionsOpened by remember { mutableStateOf(true) }
    Column(modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 })) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                callApiActionsOpened = !callApiActionsOpened
            },
        ) {
            Text(
                text = "Call API",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
            Spacer(Modifier.weight(1f))
            TreeExpanderInverse(
                expanded = callApiActionsOpened,
                onClick = {
                    callApiActionsOpened = !callApiActionsOpened
                },
            )
        }
        if (callApiActionsOpened) {
            project.apiHolder.apiDefinitions.forEach { api ->
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
                                CallApi(apiId = api.id)
                            )
                        }
                        .selectedActionModifier(
                            actionInEdit = actionInEdit,
                            predicate = {
                                actionInEdit != null &&
                                        actionInEdit is CallApi &&
                                        actionInEdit.apiId == api.id
                            },
                        ),
                ) {
                    Text(
                        text = api.name,
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
fun CallApiActionDetail(
    project: Project,
    composeNode: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    apiDefinition: ApiDefinition,
    initialParamsMap: MutableMap<ParameterId, AssignableProperty>,
    onParametersMapUpdated: (MutableMap<ParameterId, AssignableProperty>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var addParameterDialogOpen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        apiDefinition.parameters.forEach { parameter ->
            StringProperty.StringIntrinsicValue().Editor(
                project = project,
                node = composeNode,
                initialProperty = initialParamsMap[parameter.parameterId]
                    ?: StringProperty.StringIntrinsicValue(
                        parameter.defaultValue
                    ),
                label = parameter.variableName,
                onValidPropertyChanged = { newProperty, _ ->
                    initialParamsMap[parameter.parameterId] = newProperty
                    onParametersMapUpdated(initialParamsMap)
                },
                modifier = Modifier.hoverOverlay(),
                destinationStateId = null,
                onInitializeProperty = {
                    initialParamsMap.remove(parameter.parameterId)
                    onParametersMapUpdated(initialParamsMap)
                },
                validateInput = null,
                editable = true,
                functionScopeProperties = emptyList(),
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
        AddApiParameterDialog(
            onParameterConfirmed = {
                apiDefinition.parameters.add(it)
                composeNodeCallbacks.onApiUpdated(apiDefinition)
            },
            onCloseDialog = {
                closeDialog()
            }
        )
    }
}