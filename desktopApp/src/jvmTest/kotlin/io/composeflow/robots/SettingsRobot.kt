package io.composeflow.robots

import androidx.compose.ui.test.DesktopComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.composeflow.ui.settings.preference.SettingsProjectRestoreButtonTag
import io.composeflow.ui.settings.preference.SettingsProjectSaveButtonTag

@OptIn(ExperimentalTestApi::class)
class SettingsRobot {
    context(DesktopComposeUiTest)
    fun clickSaveProject() {
        onNodeWithTag(SettingsProjectSaveButtonTag)
            .performClick()
    }

    context(DesktopComposeUiTest)
    fun clickRestoreProject() {
        onNodeWithTag(SettingsProjectRestoreButtonTag)
            .performClick()
    }
}
