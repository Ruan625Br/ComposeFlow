package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ElectricalServices
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
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.Dbms
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.FunctionScopeParameterProperty
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.model.property.getErrorMessage
import io.composeflow.model.state.StateId
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.set_from_state
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.propertyeditor.variable.SetFromStateDialog
import org.jetbrains.compose.resources.stringResource

@Composable
fun AssignableDataTypePropertyEditor(
    project: Project,
    node: ComposeNode,
    acceptableType: ComposeFlowType,
    onValidPropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    label: String = "",
    initialProperty: AssignableProperty? = null,
    destinationStateId: StateId? = null,
    placeholder: String? = null,
    onInitializeProperty: (() -> Unit)? = null,
    functionScopeProperties: List<FunctionScopeParameterProperty> = emptyList(),
    singleLine: Boolean = true,
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
        EditableTextProperty(
            enabled = false,
            onValidValueChanged = {},
            initialValue = initialProperty?.transformedValueExpression(project) ?: "",
            label = label,
            placeholder = placeholder,
            singleLine = singleLine,
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = ComposeFlowIcons.Dbms,
                    contentDescription = null,
                )
            },
            supportingText = errorText,
            modifier = Modifier.weight(1f),
            valueSetFromVariable = initialProperty !is IntrinsicProperty<*>,
        )

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
                        contentDescription = stringResource(Res.string.set_from_state),
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
