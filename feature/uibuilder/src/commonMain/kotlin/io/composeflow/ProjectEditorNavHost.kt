package io.composeflow

import androidx.compose.runtime.Composable
import io.composeflow.ui.apieditor.apiEditorScreen
import io.composeflow.ui.appstate.appStateEditorScreen
import io.composeflow.ui.asset.assetEditorScreen
import io.composeflow.ui.datatype.dataTypeEditorScreen
import io.composeflow.ui.firestore.firestoreEditorScreen
import io.composeflow.ui.settings.settingsScreen
import io.composeflow.ui.themeeditor.themeEditorScreen
import io.composeflow.ui.uibuilder.uiBuilderRoute
import io.composeflow.ui.uibuilder.uiBuilderScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ProjectEditorNavHost(
    navigator: Navigator,
    projectId: String,
) {
    NavHost(
        navigator = navigator,
        initialRoute = uiBuilderRoute,
    ) {
        uiBuilderScreen(
            projectId = projectId,
        )
        dataTypeEditorScreen(
            projectId = projectId,
        )
        appStateEditorScreen(
            projectId = projectId,
        )
        firestoreEditorScreen(
            projectId = projectId,
        )
        apiEditorScreen(
            projectId = projectId,
        )
        themeEditorScreen(
            projectId = projectId,
        )
        assetEditorScreen(
            projectId = projectId,
        )
        settingsScreen(
            projectId = projectId,
        )
    }
}
