package io.composeflow.ui.appstate

import io.composeflow.model.appStateEditorRoute
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.appStateEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = appStateEditorRoute,
        navTransition = navTransition,
    ) {
        AppStateEditor(project = project)
    }
}
