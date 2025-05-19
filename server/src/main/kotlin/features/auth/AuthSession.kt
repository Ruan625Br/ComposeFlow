package io.composeflow.features.auth

data class AuthSession(
    val state: String,
    val redirectUrl: String,
)
