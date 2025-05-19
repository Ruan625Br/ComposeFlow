package io.composeflow.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.ui.graphics.vector.ImageVector

const val uiBuilderRoute = "ui_builder_route"
const val dataTypeEditorRoute = "datatype_editor_route"
const val appStateEditorRoute = "appstate_editor_route"
const val firestoreEditorRoute = "firestore_editor_route"
const val apiEditorRoute = "api_editor_route"
const val themeEditorRoute = "theme_editor_route"
const val assetEditorRoute = "asset_editor_route"
const val settingsRoute = "settings_route"

enum class TopLevelDestination(val iconPath: String, val label: String, val route: String) {
    UiBuilder(
        "icons/editFolder.svg",
        "UI Builder",
        uiBuilderRoute,
    ),
    DataTypeEditor(
        "icons/dataColumn.svg",
        "Data type",
        dataTypeEditorRoute,
    ),
    AppStateEditor(
        "icons/dbms.svg",
        "App State",
        appStateEditorRoute,
    ),
    FirestoreEditor(
        "icons/cloud_firestore.svg",
        "Firestore",
        firestoreEditorRoute,
    ),
    ApiEditor(
        "icons/http_requests_filetype.svg",
        "API editor",
        apiEditorRoute,
    ),
    ThemeEditor(
        "icons/colors.svg",
        "Theme editor",
        themeEditorRoute,
    ),
    AssetEditor(
        "icons/assets.svg",
        "Assets",
        assetEditorRoute,
    ),
    Settings(
        "icons/settings.svg",
        "Settings",
        settingsRoute,
    ),
}

enum class InspectorTabDestination(
    val imageVector: ImageVector,
    val contentDesc: String,
) {
    Inspector(Icons.Outlined.ZoomIn, "Inspector"),
    Action(Icons.Outlined.FlashOn, "Action"),
    Code(Icons.Outlined.Code, "Code"),
}
