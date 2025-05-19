package io.composeflow

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

@Serializable
data class HttpError(
    val error: String,
)

suspend inline fun ApplicationCall.responseInternalServerError(message: String? = null) {
    response.status(HttpStatusCode.InternalServerError)
    respond(HttpError(message ?: "Internal Server Error"))
}

suspend inline fun ApplicationCall.responseBadRequest(message: String? = null) {
    response.status(HttpStatusCode.BadRequest)
    respond(HttpError(message ?: "Bad Request"))
}

suspend inline fun ApplicationCall.responseUnauthorized(message: String? = null) {
    response.status(HttpStatusCode.Unauthorized)
    respond(HttpError(message ?: "Unauthorized"))
}