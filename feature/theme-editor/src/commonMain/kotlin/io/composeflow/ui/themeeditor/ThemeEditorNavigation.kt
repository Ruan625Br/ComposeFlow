package io.composeflow.ui.themeeditor

import io.composeflow.model.project.Project
import io.composeflow.model.themeEditorRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.themeEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = themeEditorRoute,
        navTransition = navTransition,
    ) {
        ThemeEditorScreen(
            project = project,
        )
    }
}
