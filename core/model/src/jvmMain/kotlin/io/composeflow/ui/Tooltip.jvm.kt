package io.composeflow.ui

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.composeflow.ui.common.ComposeFlowTheme

@Composable
actual fun Tooltip(
    text: String,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    BasicTooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            ComposeFlowTheme {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier =
                        Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        state = rememberBasicTooltipState(),
        modifier = modifier,
    ) {
        content()
    }
}
