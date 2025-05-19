package io.composeflow.ui.login

import io.composeflow.ui.jewel.TitleBarContent
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.transition.NavTransition

const val loginRoute = "login_route"

fun RouteBuilder.loginScreen(
    onGoogleSignInClicked: () -> Unit,
    onLogOut: () -> Unit,
    onTitleBarContentSet: (TitleBarContent) -> Unit = {},
    navTransition: NavTransition? = null,
) {
    scene(
        route = loginRoute,
        navTransition = navTransition,
    ) {
        LoginScreen(
            onGoogleSignInClicked = onGoogleSignInClicked,
            onLogOut = onLogOut,
            onTitleBarContentSet = onTitleBarContentSet,
        )
    }
}
