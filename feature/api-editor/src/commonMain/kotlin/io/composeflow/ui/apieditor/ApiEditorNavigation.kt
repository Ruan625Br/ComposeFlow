package io.composeflow.ui.apieditor

import io.composeflow.model.API_EDITOR_ROUTE
import io.composeflow.model.project.Project
import io.composeflow.ui.apieditor.ui.ApiEditorScreen
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.apiEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = API_EDITOR_ROUTE,
        navTransition = navTransition,
    ) {
        ApiEditorScreen(project = project)
    }
}
