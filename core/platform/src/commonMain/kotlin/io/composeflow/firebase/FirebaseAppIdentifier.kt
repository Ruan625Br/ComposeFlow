package io.composeflow.firebase

import io.composeflow.auth.google.TokenResponse

data class FirebaseAppIdentifier(
    val firebaseProjectId: String,
    // Firebase API calls are expected to use the user's credential obtained by OAuth2,
    // thus, using TokenResponse as the identifier.
    val googleTokenResponse: TokenResponse,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is FirebaseAppIdentifier) return false
        return firebaseProjectId == other.firebaseProjectId &&
            googleTokenResponse.access_token == other.googleTokenResponse.access_token
    }

    override fun hashCode(): Int = firebaseProjectId.hashCode() + googleTokenResponse.access_token.hashCode()
}
