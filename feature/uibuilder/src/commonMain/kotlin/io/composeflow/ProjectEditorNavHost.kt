package io.composeflow

import androidx.compose.runtime.Composable
import io.composeflow.ai.AiAssistantUiState
import io.composeflow.model.project.Project
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
    project: Project,
    aiAssistantUiState: AiAssistantUiState,
    onUpdateProject: (Project) -> Unit,
) {
    NavHost(
        navigator = navigator,
        initialRoute = uiBuilderRoute,
    ) {
        uiBuilderScreen(
            project = project,
            aiAssistantUiState = aiAssistantUiState,
            onUpdateProject = onUpdateProject,
        )
        dataTypeEditorScreen(
            project = project,
        )
        appStateEditorScreen(
            project = project,
        )
        firestoreEditorScreen(
            project = project,
        )
        apiEditorScreen(
            project = project,
        )
        themeEditorScreen(
            project = project,
        )
        assetEditorScreen(
            project = project,
        )
        settingsScreen(
            project = project,
        )
    }
}
