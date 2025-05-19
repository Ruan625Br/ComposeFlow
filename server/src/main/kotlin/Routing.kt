package io.composeflow

import io.composeflow.features.auth.authRouting
import io.composeflow.features.billing.billingRouting
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        authRouting()
        billingRouting()

        // Cloud Run health check
        get("/") {
            call.respondText("ok")
        }
    }
}
