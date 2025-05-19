package io.composeflow

import androidx.compose.runtime.Composable
import io.composeflow.ui.jewel.TitleBarContent
import io.composeflow.ui.login.loginRoute
import io.composeflow.ui.login.loginScreen
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ComposeBuilderNavHost(
    navigator: Navigator,
    onGoogleSignInClicked: () -> Unit,
    onLogOut: () -> Unit,
    onTitleBarContentSet: (TitleBarContent) -> Unit = {},
) {
    NavHost(
        navigator = navigator,
        initialRoute = loginRoute,
    ) {
        loginScreen(
            onGoogleSignInClicked = onGoogleSignInClicked,
            onLogOut = onLogOut,
            onTitleBarContentSet = onTitleBarContentSet,
        )
    }
}
