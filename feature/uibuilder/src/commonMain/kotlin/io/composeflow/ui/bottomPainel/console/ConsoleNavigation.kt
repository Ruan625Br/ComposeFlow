package io.composeflow.ui.bottomPainel.console

import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val CONSOLE_ROUTE = "console_route"

fun RouteBuilder.consolePanel(navTransition: NavTransition? = null) {
    scene(
        route = CONSOLE_ROUTE,
        navTransition = navTransition,
    ) {
        ConsolePanel()
    }
}
