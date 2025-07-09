package io.composeflow.ui.settings

import io.composeflow.model.SETTINGS_ROUTE
import io.composeflow.model.project.Project
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.transition.NavTransition

fun RouteBuilder.settingsScreen(
    project: Project,
    navTransition: NavTransition? = null,
) {
    scene(
        route = SETTINGS_ROUTE,
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
            project = project,
            initialDestination = initialDestination,
        )
    }
}
