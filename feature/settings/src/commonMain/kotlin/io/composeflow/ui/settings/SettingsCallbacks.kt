package io.composeflow.ui.settings

import io.composeflow.model.project.appscreen.screen.Screen

data class SettingsCallbacks(
    val onConnectFirebaseProjectId: (String) -> Unit,
    val onLoginScreenChanged: (Screen) -> Unit,
)