package io.composeflow.ui.icon

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.composeflow.ui.minimumInteractiveComponentSize
import io.composeflow.ui.modifier.hoverIconClickable

@Composable
fun ComposeFlowIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(minimumInteractiveComponentSize)
            .hoverIconClickable(),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content,
    )
}
