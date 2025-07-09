package io.composeflow.ui.themeeditor

import io.composeflow.model.THEME_EDITOR_ROUTE
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.themeEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = THEME_EDITOR_ROUTE,
        navTransition = navTransition,
    ) {
        ThemeEditorScreen(
            project = project,
        )
    }
}
