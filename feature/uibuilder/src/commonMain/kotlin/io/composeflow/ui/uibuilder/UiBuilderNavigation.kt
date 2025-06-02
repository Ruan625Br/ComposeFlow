package io.composeflow.ui.uibuilder

import io.composeflow.ai.AiAssistantUiState
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val uiBuilderRoute = "ui_builder_route"

fun RouteBuilder.uiBuilderScreen(
    project: Project,
    aiAssistantUiState: AiAssistantUiState,
    onUpdateProject: (Project) -> Unit,
    navTransition: NavTransition? = null,
) {
    scene(
        route = uiBuilderRoute,
        navTransition = navTransition,
    ) {
        UiBuilderScreen(
            project = project,
            aiAssistantUiState = aiAssistantUiState,
            onUpdateProject = onUpdateProject,
        )
    }
}
