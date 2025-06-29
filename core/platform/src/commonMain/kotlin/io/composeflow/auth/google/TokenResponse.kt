package io.composeflow.auth.google

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val access_token: String,
    val id_token: String,
    val token_type: String,
    val refresh_token: String? = null,
    val expires_in: Int? = null,
    val scope: String? = null,
    val error: String? = null,
) {
    fun hasSufficientScope(arg: String): Boolean = scope != null && scope.contains(arg)

    fun hasSufficientScopes(vararg args: String): Boolean = scope != null && args.all { scope.contains(it) }
}
