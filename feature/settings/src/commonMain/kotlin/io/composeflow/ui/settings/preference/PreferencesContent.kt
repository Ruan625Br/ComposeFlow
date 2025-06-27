package io.composeflow.ui.settings.preference

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.initial_screen_description
import io.composeflow.initial_screen_not_logged_in_description
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.findScreenOrNull
import io.composeflow.model.settings.DarkThemeSettingSetter
import io.composeflow.model.settings.DarkThemeSettingSetterUiState
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.settings.SettingsCallbacks
import org.jetbrains.compose.resources.stringResource

@Composable
fun PreferencesContent(
    project: Project,
    darkThemeSettingsUiState: DarkThemeSettingSetterUiState,
    settingsCallbacks: SettingsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Preferences",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(end = 8.dp)
        )

        DarkThemeSettingSetter(
            darkThemeSettingSetterUiState = darkThemeSettingsUiState,
        )
        InitialScreenContent(
            project = project,
            settingsCallbacks = settingsCallbacks,
        )
    }
}

@Immutable
data class SettingsUiState(
    val darkThemeSettingSetterUiState: DarkThemeSettingSetterUiState,
)

val SettingsProjectSaveButtonTag = "Settings/ProjectSaveButtonTag"
val SettingsProjectRestoreButtonTag = "Settings/ProjectRestoreButtonTag"

@Composable
fun InitialScreenContent(
    project: Project,
    settingsCallbacks: SettingsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Initial screen",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp, end = 8.dp)
            )
            val initialScreenDesc = stringResource(Res.string.initial_screen_description)
            Tooltip(initialScreenDesc) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = initialScreenDesc,
                )
            }
        }

        val screens = buildList {
            add(Screen(name = ""))
            project.screenHolder.screens.forEach { add(it) }
        }
        val currentLoginScreen = project.screenHolder.loginScreenId.value?.let {
            project.findScreenOrNull(it)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicDropdownPropertyEditor(
                project = project,
                items = screens,
                onValueChanged = { _, screen ->
                    settingsCallbacks.onLoginScreenChanged(screen)
                },
                selectedItem = currentLoginScreen,
                label = "Not logged in",
                modifier = Modifier.width(280.dp).padding(end = 8.dp)
            )
            val initialScreenNotLoggedInDesc =
                stringResource(Res.string.initial_screen_not_logged_in_description)
            Tooltip(initialScreenNotLoggedInDesc) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = initialScreenNotLoggedInDesc
                )
            }
        }
    }
}