package io.composeflow.ui.inspector.validator

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import io.composeflow.model.parameter.TextFieldTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.validator.TextFieldValidator
import io.composeflow.ui.Tooltip
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.utils.TreeExpanderInverse
import io.composeflow.validator
import io.composeflow.validator_accepts_description
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextFieldValidatorInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    val initiallyExpanded = (node.trait.value as TextFieldTrait).enableValidator == true
    Column(
        modifier = modifier
            .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        var expanded by remember(node.id) { mutableStateOf(initiallyExpanded) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = !expanded }
                .hoverIconClickable(),
        ) {
            Text(
                text = stringResource(Res.string.validator),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Spacer(modifier = Modifier.weight(1f))

            TreeExpanderInverse(
                expanded = expanded,
                onClick = { expanded = !expanded },
            )
        }

        if (expanded) {
            TextFieldValidatorInspectorContent(
                project = project,
                node = node,
                composeNodeCallbacks = composeNodeCallbacks,
            )
        }
    }
}

@Composable
private fun TextFieldValidatorInspectorContent(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    val textFieldTrait = node.trait.value as TextFieldTrait
    Column(modifier = modifier) {
        Row {
            BooleanPropertyEditor(
                checked = textFieldTrait.enableValidator == true,
                label = "Enable validator",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(enableValidator = it)
                    )
                },
                modifier = Modifier.weight(1f).hoverOverlay()
            )
            if (textFieldTrait.enableValidator == true) {
                Column(modifier = Modifier.weight(1f)) {
                    val acceptsTooltip = stringResource(Res.string.validator_accepts_description)
                    Tooltip(acceptsTooltip) {
                        BasicDropdownPropertyEditor(
                            project = project,
                            items = TextFieldValidator.entries(),
                            label = "Accepts",
                            selectedItem = textFieldTrait.textFieldValidator,
                            onValueChanged = { _, item ->
                                composeNodeCallbacks.onTraitUpdated(
                                    node,
                                    textFieldTrait.copy(textFieldValidator = item),
                                )
                            },
                            modifier = Modifier
                                .hoverOverlay()
                                .padding(top = 4.dp),
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
        }

        if (textFieldTrait.enableValidator == true) {
            textFieldTrait.textFieldValidator?.Editor(
                project = project,
                node = node,
                onValidatorEdited = {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        textFieldTrait.copy(textFieldValidator = it),
                    )
                },
                modifier = Modifier,
            )
        }
    }
}
