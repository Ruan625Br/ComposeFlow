package io.composeflow.ui.inspector.action.datatype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_filter
import io.composeflow.model.datatype.AndFilter
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.FilterExpression
import io.composeflow.model.datatype.OrFilter
import io.composeflow.model.datatype.SingleFilter
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.firebase.FirestoreCollection
import io.composeflow.remove_filter
import io.composeflow.ui.Tooltip
import io.composeflow.ui.dashedBorder
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogicalFilterEditor(
    project: Project,
    composeNode: ComposeNode,
    firestoreCollection: FirestoreCollection,
    dataType: DataType,
    title: String,
    filters: List<FilterExpression>,
    onFiltersChange: (List<FilterExpression>) -> Unit,
    onRemoveFilter: () -> Unit,
    modifier: Modifier = Modifier,
    level: Int = 0,
) {
    var overlayVisible by remember { mutableStateOf(false) }
    val overlayModifier =
        if (overlayVisible) {
            Modifier.background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
            )
        } else {
            Modifier
        }
    Column(
        modifier =
            modifier
                .padding(start = (level * 16).dp)
                .dashedBorder(
                    strokeWidth = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp),
                ).onPointerEvent(PointerEventType.Enter) {
                    overlayVisible = true
                }.onPointerEvent(PointerEventType.Exit) {
                    overlayVisible = false
                }.then(overlayModifier),
    ) {
        var openAddFilterMenu by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(8.dp)) {
            Row {
                Text(title)
                Spacer(Modifier.size(8.dp))
                val contentDesc = stringResource(Res.string.add_filter)
                Tooltip(contentDesc) {
                    ComposeFlowIconButton(
                        onClick = {
                            openAddFilterMenu = true
                        },
                    ) {
                        ComposeFlowIcon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            contentDescription = contentDesc,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))

                val removeFilter = stringResource(Res.string.remove_filter)
                Tooltip(removeFilter) {
                    ComposeFlowIconButton(
                        onClick = {
                            onRemoveFilter()
                        },
                    ) {
                        ComposeFlowIcon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = removeFilter,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
            Spacer(Modifier.size(12.dp))

            // Render each nested filter
            filters.forEachIndexed { index, filter ->
                Row(modifier = Modifier.padding(bottom = 12.dp)) {
                    FilterExpressionEditor(
                        project = project,
                        composeNode = composeNode,
                        firestoreCollection = firestoreCollection,
                        dataType = dataType,
                        filterExpression = filter,
                        onFilterExpressionUpdated = { updatedFilter ->
                            val updatedFilters =
                                filters.toMutableList().apply { set(index, updatedFilter) }
                            onFiltersChange(updatedFilters)
                        },
                        onRemoveFilter = {
                            onFiltersChange(
                                filters.toMutableList().apply {
                                    removeAt(index)
                                },
                            )
                        },
                        level = level + 1,
                    )
                }
            }

            if (openAddFilterMenu) {
                AddFilterMenu(
                    onAndFilterAdded = {
                        val newFilters =
                            filters.toMutableList().apply {
                                add(AndFilter())
                            }
                        onFiltersChange(newFilters)
                    },
                    onOrFilterAdded = {
                        val newFilters =
                            filters.toMutableList().apply {
                                add(OrFilter())
                            }
                        onFiltersChange(newFilters)
                    },
                    onCloseMenu = {
                        openAddFilterMenu = false
                    },
                    onSingleFilterAdded = {
                        onFiltersChange(
                            filters.toMutableList().apply {
                                add(SingleFilter())
                            },
                        )
                    },
                )
            }
        }
    }
}
