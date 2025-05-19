package io.composeflow.ui.uibuilder

import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val uiBuilderRoute = "ui_builder_route"

fun RouteBuilder.uiBuilderScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = uiBuilderRoute,
        navTransition = navTransition,
    ) {
        UiBuilderScreen(
            projectId = projectId,
        )
    }
}
