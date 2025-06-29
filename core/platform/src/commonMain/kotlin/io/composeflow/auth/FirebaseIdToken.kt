package io.composeflow.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import io.composeflow.auth.google.TokenResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class FirebaseIdToken(
    val name: String,
    val picture: String,
    val iss: String,
    val aud: String,
    val auth_time: Long,
    val user_id: String,
    val sub: String,
    val iat: Long,
    val exp: Long,
    val email: String,
    val email_verified: Boolean,
    val firebase: JsonElement, // TODO: Define appropriate scheme for each ID provider
    val googleTokenResponse: TokenResponse? = null,
    val rawToken: String? = null,
)

val LocalFirebaseIdToken =
    staticCompositionLocalOf<FirebaseIdToken> {
        throw IllegalStateException("No FirebaseUserInfo provided")
    }

@Composable
fun ProvideFirebaseIdToken(
    firebaseIdToken: FirebaseIdToken,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalFirebaseIdToken provides firebaseIdToken) {
        content()
    }
}
