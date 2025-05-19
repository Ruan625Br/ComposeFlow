package io.composeflow.ui.themeeditor

import io.composeflow.model.themeEditorRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.themeEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = themeEditorRoute,
        navTransition = navTransition,
    ) {
        ThemeEditorScreen(
            projectId = projectId,
        )
    }
}
