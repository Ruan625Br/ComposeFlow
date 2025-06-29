package io.composeflow.ui.datafield

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import io.composeflow.model.project.Project
import io.composeflow.model.type.ComposeFlowType

@Composable
fun FieldRow(
    project: Project,
    fieldName: String,
    type: ComposeFlowType,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    displayNameListAware: Boolean = false,
) {
    val enabledModifier =
        if (enabled) {
            Modifier
        } else {
            Modifier.alpha(0.4f)
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .then(enabledModifier)
                .clickable { onClick() },
    ) {
        Text(
            fieldName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            type.displayName(project = project, listAware = displayNameListAware),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
