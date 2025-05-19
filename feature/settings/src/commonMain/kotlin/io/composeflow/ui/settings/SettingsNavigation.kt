package io.composeflow.ui.settings

import io.composeflow.model.settingsRoute
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.settingsScreen(
    projectId: String,
    navTransition: NavTransition? = null,
) {
    scene(
        route = settingsRoute,
        navTransition = navTransition,
    ) {
        val destination = it.query<String>("destination")
        var initialDestination: SettingsScreenDestination? = null
        SettingsScreenDestination.entries.forEach {
            if (destination == it.name) {
                initialDestination = it
            }
        }
        SettingsScreen(
            projectId = projectId,
            initialDestination = initialDestination
        )
    }
}
