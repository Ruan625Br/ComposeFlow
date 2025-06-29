package io.composeflow.ui.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import io.composeflow.Res
import io.composeflow.tap_to_collapse
import io.composeflow.tap_to_expand
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.hoverIconClickable
import org.jetbrains.compose.resources.stringResource

@Composable
fun TreeExpander(
    expanded: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val rotationDegrees by animateFloatAsState(
        if (expanded) 90f else 0f,
    )
    ComposeFlowIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        val vector = Icons.Outlined.ChevronRight
        val contentDesc =
            if (expanded) {
                stringResource(Res.string.tap_to_collapse)
            } else {
                stringResource(
                    Res.string.tap_to_expand,
                )
            }
        ComposeFlowIcon(
            imageVector = vector,
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.secondary,
            modifier =
                Modifier
                    .rotate(rotationDegrees)
                    .hoverIconClickable(),
        )
    }
}

@Composable
fun TreeExpanderInverse(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val rotationDegrees by animateFloatAsState(
        if (expanded) -90f else 0f,
    )
    ComposeFlowIconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        val vector = Icons.Outlined.ChevronLeft
        val contentDesc =
            if (expanded) {
                stringResource(Res.string.tap_to_collapse)
            } else {
                stringResource(
                    Res.string.tap_to_expand,
                )
            }
        ComposeFlowIcon(
            imageVector = vector,
            contentDescription = contentDesc,
            tint = MaterialTheme.colorScheme.secondary,
            modifier =
                Modifier
                    .rotate(rotationDegrees),
        )
    }
}
