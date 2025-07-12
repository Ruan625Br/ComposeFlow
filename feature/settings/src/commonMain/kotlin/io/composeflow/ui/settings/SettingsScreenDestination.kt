package io.composeflow.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.AppAsset
import io.composeflow.custom.composeflowicons.Firebase

enum class SettingsScreenDestination(
    val icon: ImageVector,
    val destinationName: String,
) {
    Preferences(
        icon = Icons.Outlined.Settings,
        destinationName = "Preferences",
    ),
    Firebase(
        icon = ComposeFlowIcons.Firebase,
        destinationName = "Firebase",
    ),
    AppAssets(
        icon = ComposeFlowIcons.AppAsset,
        destinationName = "App Assets",
    ),
}
