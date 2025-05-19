package io.composeflow.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewCozy
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopDestination(val icon: ImageVector, val title: String) {
    Project(Icons.Outlined.ViewCozy, title = "Project"),
    Settings(Icons.Outlined.Settings, title = "Settings"),
    About(Icons.Outlined.Info, title = "About"),
    ;
}