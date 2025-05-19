package io.composeflow

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import io.ktor.http.HttpHeaders
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.application.call
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelineContext

internal val DecodedTokenKey: AttributeKey<FirebaseToken> = AttributeKey("FirebaseAuthenticationDecodedTokenKey")

class FirebaseAuthProvider(configuration: Configuration) {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, FirebaseAuthProvider> {
        override val key = AttributeKey<FirebaseAuthProvider>("FirebaseAuthProvider")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): FirebaseAuthProvider {
            val plugin = FirebaseAuthProvider(Configuration().apply(configure))

            pipeline.intercept(ApplicationCallPipeline.Monitoring) {
                plugin.intercept(this)
            }
            return plugin
        }
    }

    private val auth = FirebaseAuth.getInstance(configuration.firebaseApp ?: FirebaseApp.getInstance())
    private val authorizationType = configuration.authorizationType
    private val checkRevoked = configuration.checkRevoked
    private val skipWhen = configuration.skipWhen
    private val whenReject = configuration.whenReject

    class Configuration {
        var firebaseApp: FirebaseApp? = null

        var authorizationType = AuthorizationType.Bearer
        var checkRevoked = false
        var skipWhen: (suspend (ApplicationCall) -> Boolean)? = null
        var whenReject: (suspend (ApplicationCall, Throwable) -> Unit)? = null
    }

    sealed class AuthorizationType {
        abstract val getIdToken: (ApplicationCall) -> String?

        data object Bearer : AuthorizationType() {
            override val getIdToken: (ApplicationCall) -> String? = { it ->
                it.request.headers[HttpHeaders.Authorization]?.trim()?.split(" ")?.let { authorization ->
                    val (phrase, token) = authorization.let { it.getOrNull(0) to it.getOrNull(1) }
                    phrase?.lowercase()?.equals("bearer")?.takeIf { it }?.let { token?.trim() }
                }
            }
        }
    }

    private suspend fun intercept(context: PipelineContext<Unit, ApplicationCall>) {
        with(context.call) {
            if (skipWhen?.invoke(this) == true) {
                return@with
            }

            kotlin.runCatching {
                val idToken = authorizationType.getIdToken(this)
                if (idToken.isNullOrBlank()) {
                    throw TokenNotProvidedException()
                }
                val decodedToken = auth.verifyIdToken(idToken, checkRevoked)
                attributes.put(DecodedTokenKey, decodedToken)
            }.onFailure {
                whenReject?.invoke(this, it)
            }
        }
    }

    internal class InvalidTokenException : Exception()
    internal class TokenNotProvidedException : Exception()
}

fun ApplicationCall.getDecodedToken(): FirebaseToken? = attributes.getOrNull(DecodedTokenKey).also { Result }