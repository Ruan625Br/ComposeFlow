package io.composeflow.ui.radiobutton

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.composeflow.ui.minimumInteractiveComponentSize

@Composable
fun ComposeFlowRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier.height(minimumInteractiveComponentSize),
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}
