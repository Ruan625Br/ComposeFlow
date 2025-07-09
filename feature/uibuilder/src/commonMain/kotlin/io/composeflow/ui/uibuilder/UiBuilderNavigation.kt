package io.composeflow.ui.uibuilder

import androidx.compose.ui.geometry.Size
import io.composeflow.ai.AiAssistantUiState
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val UI_BUILDER_ROUTE = "ui_builder_route"

fun RouteBuilder.uiBuilderScreen(
    project: Project,
    aiAssistantUiState: AiAssistantUiState,
    onUpdateProject: (Project) -> Unit,
    screenMaxSize: Size,
    navTransition: NavTransition? = null,
) {
    scene(
        route = UI_BUILDER_ROUTE,
        navTransition = navTransition,
    ) {
        UiBuilderScreen(
            project = project,
            aiAssistantUiState = aiAssistantUiState,
            onUpdateProject = onUpdateProject,
            screenMaxSize = screenMaxSize,
        )
    }
}
