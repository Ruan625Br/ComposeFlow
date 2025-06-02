package io.composeflow.ui.asset

import io.composeflow.model.assetEditorRoute
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.assetEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = assetEditorRoute,
        navTransition = navTransition,
    ) {
        AssetEditorScreen(
            project = project,
        )
    }
}
