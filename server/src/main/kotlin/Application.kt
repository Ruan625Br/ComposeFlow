package io.composeflow

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuthException
import io.composeflow.features.auth.AuthSession
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.forwardedheaders.ForwardedHeaders
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(FirebaseAuthProvider) {
        firebaseApp =
            if (FirebaseApp.getApps().size > 0) FirebaseApp.getInstance() else FirebaseApp.initializeApp(
                FirebaseOptions.builder().apply {
                    setCredentials(GoogleCredentials.getApplicationDefault())
                }.build()
            )
        authorizationType = FirebaseAuthProvider.AuthorizationType.Bearer
        checkRevoked = false

        whenReject = { _, cause ->
            when (cause) {
                is FirebaseAuthProvider.TokenNotProvidedException -> {
                    // noop
                }

                is FirebaseAuthException -> {
                    // noop
                }

                is IllegalArgumentException -> {
                    // noop
                }

                else -> {
                    // noop
                }
            }
        }
    }
    install(ForwardedHeaders)
    install(Sessions) {
        cookie<AuthSession>("auth_session")
    }
    install(ContentNegotiation) {
        json()
    }
    configureSecurity()
    configureRouting()
}
