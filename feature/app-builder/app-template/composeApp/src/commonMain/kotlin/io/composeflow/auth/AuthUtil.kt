package io.composeflow.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

val LocalAuthenticatedUser = compositionLocalOf<FirebaseUserWrapper?> { null }

@Composable
fun ProvideAuthenticatedUser(
    content: @Composable () -> Unit,
) {
    // For some reason, authStateChanged is constantly invoked on android.
    // To prevent constant recomposition, create a wrapper for FirebaseUser
    val authenticatedUser = Firebase.auth.authStateChanged
        .map { it?.toWrapper() }
        .distinctUntilChanged()
        .collectAsState(null)
    CompositionLocalProvider(LocalAuthenticatedUser provides authenticatedUser.value) {
        content()
    }
}