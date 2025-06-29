package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import io.composeflow.delete_instant
import io.composeflow.model.parameter.wrapper.InstantWrapper
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.modifier.hoverIconClickable
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource

@Composable
fun InstantPropertyEditor(
    label: String,
    initialInstant: InstantWrapper?,
    onInstantUpdated: (InstantWrapper) -> Unit,
    modifier: Modifier = Modifier,
    onInstantDeleted: (() -> Unit)? = null,
) {
    var dialogOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .wrapContentHeight()
                .hoverIconClickable()
                .clickable {
                    dialogOpen = true
                },
    ) {
        LabeledBorderBox(
            label = label.ifEmpty { "Instant" },
            modifier = Modifier.weight(1f),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = initialInstant?.asString() ?: "",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 8.dp),
                )
                if (initialInstant != null && onInstantDeleted != null) {
                    Spacer(Modifier.weight(1f))
                    ComposeFlowIconButton(
                        onClick = {
                            onInstantDeleted()
                        },
                    ) {
                        ComposeFlowIcon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(Res.string.delete_instant),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
    if (dialogOpen) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = initialInstant?.instant?.toEpochMilliseconds(),
            )

        DatePickerDialog(
            onDismissRequest = {
                dialogOpen = false
            },
            confirmButton = {
                Column {
                    Row {
                        OutlinedButton(
                            onClick = {
                                dialogOpen = false
                                datePickerState.selectedDateMillis?.let {
                                    onInstantUpdated(InstantWrapper(Instant.fromEpochMilliseconds(it)))
                                }
                            },
                        ) {
                            Text("Confirm")
                        }
                        Spacer(Modifier.size(8.dp))
                    }
                    Spacer(Modifier.size(8.dp))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        dialogOpen = false
                    },
                ) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(datePickerState)
        }
    }
}
