package io.composeflow.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.ui.graphics.vector.ImageVector

const val UI_BUILDER_ROUTE = "ui_builder_route"
const val DATA_TYPE_EDITOR_ROUTE = "datatype_editor_route"
const val APP_STATE_EDITOR_ROUTE = "appstate_editor_route"
const val FIRESTORE_EDITOR_ROUTE = "firestore_editor_route"
const val API_EDITOR_ROUTE = "api_editor_route"
const val THEME_EDITOR_ROUTE = "theme_editor_route"
const val ASSET_EDITOR_ROUTE = "asset_editor_route"
const val STRING_EDITOR_ROUTE = "string_editor_route"
const val SETTINGS_ROUTE = "settings_route"

enum class TopLevelDestination(
    val iconPath: String,
    val label: String,
    val route: String,
) {
    UiBuilder(
        "icons/editFolder.svg",
        "UI Builder",
        UI_BUILDER_ROUTE,
    ),
    DataTypeEditor(
        "icons/dataColumn.svg",
        "Data type",
        DATA_TYPE_EDITOR_ROUTE,
    ),
    AppStateEditor(
        "icons/dbms.svg",
        "App State",
        APP_STATE_EDITOR_ROUTE,
    ),
    FirestoreEditor(
        "icons/cloud_firestore.svg",
        "Firestore",
        FIRESTORE_EDITOR_ROUTE,
    ),
    ApiEditor(
        "icons/http_requests_filetype.svg",
        "API editor",
        API_EDITOR_ROUTE,
    ),
    ThemeEditor(
        "icons/colors.svg",
        "Theme editor",
        THEME_EDITOR_ROUTE,
    ),
    AssetEditor(
        "icons/assets.svg",
        "Assets",
        ASSET_EDITOR_ROUTE,
    ),
    StringEditor(
        "icons/translate.svg",
        "Strings",
        STRING_EDITOR_ROUTE,
    ),
    Settings(
        "icons/settings.svg",
        "Settings",
        SETTINGS_ROUTE,
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
