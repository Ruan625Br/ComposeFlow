package io.composeflow.ui.appstate

import io.composeflow.model.appStateEditorRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.appStateEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = appStateEditorRoute,
        navTransition = navTransition,
    ) {
        AppStateEditor(projectId = projectId)
    }
}
