package io.composeflow.ui.inspector.action.datatype

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
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
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.where_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun WhereExpressionEditor(
    project: Project,
    composeNode: ComposeNode,
    firestoreCollection: FirestoreCollection,
    dataType: DataType,
    filterExpression: FilterExpression?,
    onFilterExpressionUpdated: (FilterExpression?) -> Unit,
) {
    Column {
        var openAddFilterMenu by remember { mutableStateOf(false) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "WHERE",
                style = MaterialTheme.typography.titleSmall,
            )
            val contentDesc = stringResource(Res.string.where_description)
            Spacer(Modifier.size(8.dp))
            Tooltip(contentDesc) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = contentDesc,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Spacer(Modifier.size(12.dp))

        if (filterExpression == null) {
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
        }

        FilterExpressionEditor(
            project = project,
            composeNode = composeNode,
            firestoreCollection = firestoreCollection,
            dataType = dataType,
            filterExpression = filterExpression,
            onFilterExpressionUpdated = onFilterExpressionUpdated,
            onRemoveFilter = {
                onFilterExpressionUpdated(null)
            },
        )

        if (openAddFilterMenu) {
            AddFilterMenu(
                onAndFilterAdded = {
                    onFilterExpressionUpdated(AndFilter(filters = listOf()))
                },
                onOrFilterAdded = {
                    onFilterExpressionUpdated(OrFilter(filters = listOf()))
                },
                onCloseMenu = {
                    openAddFilterMenu = false
                },
                onSingleFilterAdded = {
                    onFilterExpressionUpdated(SingleFilter())
                },
            )
        }
    }
}
