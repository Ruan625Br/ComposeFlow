package io.composeflow.ui.asset

import io.composeflow.model.ASSET_EDITOR_ROUTE
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.assetEditorScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = ASSET_EDITOR_ROUTE,
        navTransition = navTransition,
    ) {
        AssetEditorScreen(
            project = project,
        )
    }
}
