package io.composeflow.ui.apieditor

import io.composeflow.model.apiEditorRoute
import io.composeflow.ui.apieditor.ui.ApiEditorScreen
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.apiEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = apiEditorRoute,
        navTransition = navTransition,
    ) {
        ApiEditorScreen(projectId = projectId)
    }
}
