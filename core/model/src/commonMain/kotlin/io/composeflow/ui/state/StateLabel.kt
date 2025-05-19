package io.composeflow.ui.state

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
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.state.ReadableState

@Composable
fun StateLabel(
    project: Project,
    state: ReadableState,
    modifier: Modifier = Modifier,
    onLabelClicked: ((ReadableState) -> Unit)? = null,
    enabled: Boolean = true,
    displayNameListAware: Boolean = true,
) {
    val onClickModifier = if (onLabelClicked != null) {
        Modifier.clickable {
            onLabelClicked(state)
        }
    } else {
        Modifier
    }
    val alphaModifier = if (enabled) {
        Modifier
    } else {
        Modifier.alpha(0.4f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(onClickModifier)
            .then(alphaModifier),
    ) {
        Text(
            state.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            state.valueType(project)
                .displayName(project = project, listAware = displayNameListAware),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}

@Composable
fun AssignablePropertyLabel(
    project: Project,
    property: AssignableProperty,
    modifier: Modifier = Modifier,
    onLabelClicked: ((AssignableProperty) -> Unit)? = null,
    enabled: Boolean = true,
    displayNameListAware: Boolean = true,
) {
    val onClickModifier = if (onLabelClicked != null) {
        Modifier.clickable {
            onLabelClicked(property)
        }
    } else {
        Modifier
    }
    val alphaModifier = if (enabled) {
        Modifier
    } else {
        Modifier.alpha(0.4f)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .then(onClickModifier)
            .then(alphaModifier),
    ) {
        Text(
            property.displayText(project),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(
            property.valueType(project)
                .displayName(project = project, listAware = displayNameListAware),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
        )
    }
}
