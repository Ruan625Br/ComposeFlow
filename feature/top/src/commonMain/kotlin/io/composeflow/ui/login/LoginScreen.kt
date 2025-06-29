package io.composeflow.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import io.composeflow.ComposeFlowAppViewModel
import io.composeflow.LoginResultUiState
import io.composeflow.Res
import io.composeflow.auth.ProvideFirebaseIdToken
import io.composeflow.composeflow_main_logo
import io.composeflow.sign_in_with_google
import io.composeflow.ui.common.ComposeFlowTheme
import io.composeflow.ui.jewel.TitleBarContent
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.top.TopScreen
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onGoogleSignInClicked: () -> Unit,
    onLogOut: () -> Unit,
    onTitleBarRightContentSet: (TitleBarContent) -> Unit,
    onTitleBarLeftContentSet: (TitleBarContent) -> Unit,
) {
    val appViewModel =
        viewModel(modelClass = ComposeFlowAppViewModel::class) { ComposeFlowAppViewModel() }
    val loginUiState by appViewModel.loginResultUiState.collectAsState()
    ComposeFlowTheme(useDarkTheme = true) {
        Surface {
            when (val state = loginUiState) {
                LoginResultUiState.NotStarted -> {
                    InitialContent(
                        onGoogleSignInClicked = onGoogleSignInClicked,
                    )
                }

                LoginResultUiState.Loading -> LoadingContent()
                is LoginResultUiState.Success -> {
                    ProvideFirebaseIdToken(state.firebaseIdToken) {
                        TopScreen(
                            onLogOut = onLogOut,
                            onTitleBarRightContentSet = onTitleBarRightContentSet,
                            onTitleBarLeftContentSet = onTitleBarLeftContentSet,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InitialContent(onGoogleSignInClicked: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        val density = LocalDensity.current
        val scale = density.density / 2
        Image(
            bitmap =
                useResource("ComposeFlow_inverted_800x600.png") {
                    loadImageBitmap(it)
                },
            contentDescription = stringResource(Res.string.composeflow_main_logo),
            modifier = Modifier.scale(scale),
        )
        Image(
            bitmap =
                useResource("btn_google_signin_dark.png") {
                    loadImageBitmap(it)
                },
            contentDescription = stringResource(Res.string.sign_in_with_google),
            modifier =
                Modifier
                    .scale(scale * 1.7f)
                    .hoverIconClickable()
                    .clickable {
                        onGoogleSignInClicked()
                    },
        )
    }
}

@Composable
private fun LoadingContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        CircularProgressIndicator()
    }
}
