package io.composeflow.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import moe.tlaster.precompose.navigation.Navigator

val LocalNavigator = compositionLocalOf<Navigator> { error("No Navigator provided") }

@Composable
fun ProvideNavigator(
    navigator: Navigator,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNavigator providesDefault navigator) {
        content()
    }
}