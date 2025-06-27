package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.composeflow.ui.labeledbox.LabeledBorderBox

@Composable
inline fun <reified T> BasicDropdownPropertyEditor(
    items: List<T>,
    noinline onValueChanged: (Int, T) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    selectedIndex: Int? = null,
    selectedItem: T? = null,
    supportTooltipText: String? = null,
    crossinline displayText: @Composable (T) -> Unit = {
        val text: AnnotatedString? = when (it) {
            is String -> AnnotatedString(it)
            is DropdownTextDisplayable -> it.asDropdownText()
            is Enum<*> -> AnnotatedString(it.name)
            else -> null
        }
        text?.let { t ->
            Text(
                text = t,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.wrapContentWidth(),
            )
        }
    },
    crossinline dropDownMenuText: @Composable (T) -> Unit = {
        // Extracting this to a function mysteriously throws an IR lowring error, thus inlining the
        // default function
        val text: AnnotatedString? = when (it) {
            is String -> AnnotatedString(it)
            is DropdownTextDisplayable -> it.asDropdownText()
            is Enum<*> -> AnnotatedString(it.name)
            else -> null
        }
        text?.let { t ->
            Text(
                text = t,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(end = 8.dp),
    ) {
        LabeledBorderBox(
            label = label,
            modifier = Modifier.weight(1f),
        ) {
            DropdownProperty(
                items = items,
                onValueChanged = onValueChanged,
                selectedItem = selectedItem,
                selectedIndex = selectedIndex,
                modifier = Modifier.padding(bottom = 4.dp),
                supportToolTipText = supportTooltipText,
                displayText = displayText,
                dropDownMenuText = dropDownMenuText,
            )
        }
    }
}
