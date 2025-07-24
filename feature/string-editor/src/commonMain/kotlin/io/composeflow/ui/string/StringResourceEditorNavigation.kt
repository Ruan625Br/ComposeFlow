package io.composeflow.ui.string

import io.composeflow.model.STRING_EDITOR_ROUTE
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.stringResourceEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = STRING_EDITOR_ROUTE,
        navTransition = navTransition,
    ) {
        StringResourceEditorScreen(
            project = project,
        )
    }
}
