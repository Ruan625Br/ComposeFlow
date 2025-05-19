package io.composeflow.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInWithIdpResponse(
    val federatedId: String,
    val providerId: String,
    val email: String,
    val emailVerified: Boolean,
    val firstName: String? = null,
    val fullName: String,
    val lastName: String? = null,
    val photoUrl: String? = null,
    val localId: String,
    val displayName: String,
    val idToken: String,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val oauthIdToken: String? = null,
    val rawUserInfo: String,
    val isNewUser: Boolean? = null,
    val kind: String,
)
