package io.composeflow.model.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.ui.radiobutton.ComposeFlowRadioButton

@Immutable
data class DarkThemeSettingSetterUiState(
    val darkThemeSetting: DarkThemeSetting,
    val onThemeChanged: (DarkThemeSetting) -> Unit,
)

@Composable
fun DarkThemeSettingSetter(
    darkThemeSettingSetterUiState: DarkThemeSettingSetterUiState,
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Dark Theme",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
        )
        DarkThemeSetting.entries.forEachIndexed { index, element ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        darkThemeSettingSetterUiState.onThemeChanged(
                            DarkThemeSetting.fromOrdinal(
                                index
                            )
                        )
                    }
                    .padding(horizontal = 8.dp),
            ) {
                ComposeFlowRadioButton(
                    selected = darkThemeSettingSetterUiState.darkThemeSetting.ordinal == index,
                    onClick = {
                        darkThemeSettingSetterUiState.onThemeChanged(
                            DarkThemeSetting.fromOrdinal(
                                index
                            )
                        )
                    },
                    modifier = Modifier.height(32.dp),
                )
                Text(
                    text = element.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}