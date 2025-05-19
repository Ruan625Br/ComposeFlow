package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.condition_must_not_be_empty
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.FunctionScopeParameterProperty
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.model.property.getErrorMessage
import io.composeflow.model.property.mergeProperty
import io.composeflow.model.state.StateId
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.set_from_state
import io.composeflow.set_from_state_for
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.propertyeditor.variable.SetFromStateDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun AssignableBooleanPropertyEditor(
    project: Project,
    node: ComposeNode,
    modifier: Modifier = Modifier,
    acceptableType: ComposeFlowType = ComposeFlowType.BooleanType(isList = false),
    label: String = "",
    initialProperty: AssignableProperty? = null,
    destinationStateId: StateId? = null,
    onValidPropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit = { _, _ -> },
    onInitializeProperty: (() -> Unit)? = null,
    functionScopeProperties: List<FunctionScopeParameterProperty> = emptyList(),
    editable: Boolean = true,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        val errorText = initialProperty?.getErrorMessage(project, acceptableType)
        val editEnabled =
            editable && (initialProperty is IntrinsicProperty<*> || initialProperty == null)
        if (editEnabled) {
            BooleanPropertyEditor(
                checked = (initialProperty as? BooleanProperty.BooleanIntrinsicValue)?.value == true,
                label = label,
                onCheckedChange = {
                    val newProperty = BooleanProperty.BooleanIntrinsicValue(it)
                    onValidPropertyChanged(
                        initialProperty.mergeProperty(project, newProperty),
                        null
                    )
                },
                modifier = Modifier.padding(bottom = 12.dp).weight(1f),
            )
        } else {
            val supportingText = if (initialProperty == BooleanProperty.Empty) {
                stringResource(Res.string.condition_must_not_be_empty)
            } else {
                null
            }
            EditableTextProperty(
                initialValue = initialProperty?.transformedValueExpression(project) ?: "",
                onValidValueChanged = {},
                enabled = false,
                label = label,
                leadingIcon = {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.ToggleOff,
                        contentDescription = null,
                    )
                },
                supportingText = errorText ?: supportingText,
                modifier = Modifier.weight(1f),
                valueSetFromVariable = initialProperty !is IntrinsicProperty<*>,
            )
        }

        if (editable) {
            val setFromVariable = stringResource(Res.string.set_from_state)
            Tooltip(setFromVariable) {
                ComposeFlowIconButton(
                    onClick = {
                        dialogOpen = true
                    },
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.ElectricalServices,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = stringResource(Res.string.set_from_state_for),
                    )
                }
            }
        }
        if (dialogOpen) {
            onAnyDialogIsShown()
            SetFromStateDialog(
                project = project,
                initialProperty = initialProperty,
                node = node,
                acceptableType = acceptableType,
                onCloseClick = {
                    dialogOpen = false
                    onAllDialogsClosed()
                },
                onInitializeProperty = onInitializeProperty,
                onValidPropertyChanged = onValidPropertyChanged,
                defaultValue = acceptableType.defaultValue(),
                destinationStateId = destinationStateId,
                functionScopeProperties = functionScopeProperties,
            )
        }
    }
}
