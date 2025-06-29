package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.switch.ComposeFlowSwitch

@Composable
fun BooleanPropertyEditor(
    checked: Boolean,
    label: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .wrapContentHeight()
                .hoverIconClickable()
                .clickable {
                    onCheckedChange(!checked)
                }.padding(vertical = 4.dp),
    ) {
        LabeledBorderBox(
            label = label,
            modifier = Modifier.weight(1f),
        ) {
            ComposeFlowSwitch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                },
                enabled = enabled,
            )
        }
    }
}
