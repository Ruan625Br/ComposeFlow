package io.composeflow.ui.asset

import io.composeflow.model.assetEditorRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.assetEditorScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = assetEditorRoute,
        navTransition = navTransition,
    ) {
        AssetEditorScreen(
            projectId = projectId,
        )
    }
}
