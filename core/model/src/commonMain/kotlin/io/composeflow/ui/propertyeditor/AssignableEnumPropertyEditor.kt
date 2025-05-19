package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
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
import io.composeflow.model.enumwrapper.EnumWrapper
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.EnumProperty
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.model.property.getErrorMessage
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.set_from_state
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.propertyeditor.variable.SetFromStateDialog
import org.jetbrains.compose.resources.stringResource

@Composable
inline fun <reified T : Enum<T>> AssignableEnumPropertyEditor(
    project: Project,
    node: ComposeNode,
    acceptableType: ComposeFlowType,
    items: List<T>,
    initialProperty: AssignableProperty?,
    noinline onValidPropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    modifier: Modifier = Modifier,
    noinline onInitializeProperty: (() -> Unit)? = null,
    label: String = "",
    editable: Boolean = true,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    val editEnabled =
        editable && (initialProperty is IntrinsicProperty<*> || initialProperty == null)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        if (editEnabled) {
            LabeledBorderBox(
                label = label,
                modifier = Modifier.weight(1f),
            ) {
                DropdownProperty(
                    items = items,
                    onValueChanged = { _, item ->
                        onValidPropertyChanged(EnumProperty(item as EnumWrapper), null)
                    },
                    selectedItem = (initialProperty as? EnumProperty)?.value?.enumValue(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    editable = true,
                )
            }
        } else {
            EditableTextProperty(
                initialValue = initialProperty?.transformedValueExpression(project) ?: "",
                onValidValueChanged = {},
                label = label,
                enabled = false,
                leadingIcon = {
                    ComposeFlowIcon(
                        imageVector = Icons.AutoMirrored.Outlined.List,
                        contentDescription = null,
                    )
                },
                supportingText = initialProperty?.getErrorMessage(project, acceptableType),
                modifier = Modifier.width(IntrinsicSize.Min).weight(1f),
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
                ) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.ElectricalServices,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        contentDescription = stringResource(Res.string.set_from_state),
                    )
                }
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
            onValidPropertyChanged = onValidPropertyChanged,
            onInitializeProperty = onInitializeProperty,
            defaultValue = acceptableType.defaultValue(),
        )
    }
}
