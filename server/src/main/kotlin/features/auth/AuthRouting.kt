package io.composeflow.features.auth

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodeURLQueryComponent
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.plugins.origin
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.sessions.sessions
import kotlinx.serialization.json.Json
import java.math.BigInteger
import java.security.SecureRandom

fun Route.authRouting() {
    val googleClientID =
        environment?.config?.propertyOrNull("composeflow.auth.google.client.id")?.getString()
    val googleClientSecretKey =
        environment?.config?.propertyOrNull("composeflow.auth.google.client.secret")?.getString()

    if (googleClientID == null || googleClientSecretKey == null) {
        throw Exception("Google client ID or secret is not set")
    }

    val signInScopes =
        listOf(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email",
        )

    val firebaseScopes = listOf(
        "https://www.googleapis.com/auth/firebase",
        "https://www.googleapis.com/auth/identitytoolkit",
    )

    route("/auth") {
        route("/google") {
            get("/login") {
                val secureRandom = SecureRandom()
                val state = BigInteger(130, secureRandom).toString(32)
                val nonce = BigInteger(130, secureRandom).toString(32)
                val redirectUrl =
                    "${call.request.origin.scheme}://${call.request.headers["host"]}/auth/google/callback".encodeURLQueryComponent(
                        true
                    )
                val scopes = when (call.request.queryParameters["scopeSet"]) {
                    "firebase" -> signInScopes + firebaseScopes
                    else -> signInScopes
                }
                val scopeString = scopes.joinToString(separator = "+").encodeURLQueryComponent()

                val appRedirectUrl = call.request.queryParameters["redirectUrl"].orEmpty()
                if (!appRedirectUrl.startsWith("http://127.0.0.1:")) {
                    call.respondText(
                        text = "Invalid redirect URL",
                        status = HttpStatusCode.BadRequest
                    )
                    return@get
                }

                val url = "https://accounts.google.com/o/oauth2/v2/auth?" +
                        "response_type=code&" +
                        "client_id=${googleClientID}&" +
                        "scope=${scopeString}&" +
                        "access_type=offline&" +
                        "prompt=consent&" +
                        "redirect_uri=${redirectUrl}&" +
                        "state=${state}&" +
                        "nonce=${nonce}"
                call.sessions.set(
                    "auth_session",
                    AuthSession(state = state, redirectUrl = appRedirectUrl)
                )
                call.application.log.trace("Redirecting to $url")
                call.respondRedirect(url)
            }
            get("/callback") {
                val requestState = call.request.queryParameters["state"]
                val session = call.sessions.get("auth_session") as AuthSession?
                if (session == null) {
                    call.respondText(text = "Invalid state", status = HttpStatusCode.BadRequest)
                    return@get
                }
                val state = session.state
                val code = call.request.queryParameters["code"]
                if (requestState != state) {
                    call.respondText(text = "Invalid state", status = HttpStatusCode.BadRequest)
                    return@get
                }

                if (code == null) {
                    call.respondText(text = "Invalid code", status = HttpStatusCode.BadRequest)
                    return@get
                }

                val client = HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                }
                val url = "https://oauth2.googleapis.com/token"
                val response = client.submitForm(url, formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("client_id", googleClientID)
                    append("client_secret", googleClientSecretKey)
                    append("code", code)
                    append(
                        "redirect_uri",
                        "${call.request.origin.scheme}://${call.request.headers["host"]}/auth/google/callback"
                    )
                })

                call.respondRedirect(
                    "${session.redirectUrl}?res=${
                        response.bodyAsText().encodeURLQueryComponent(true)
                    }"
                )
            }

            post("/token") {
                val params = call.receiveParameters()
                val refreshToken = params["refresh_token"]
                if (refreshToken == null) {
                    call.respondText(
                        text = "Invalid refresh token",
                        status = HttpStatusCode.BadRequest
                    )
                    return@post
                }

                val client = HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                }
                val url = "https://oauth2.googleapis.com/token"
                val response = client.submitForm(url, formParameters = parameters {
                    append("grant_type", "authorization_code")
                    append("client_id", googleClientID)
                    append("client_secret", googleClientSecretKey)
                    append("refresh_token", refreshToken)
                    append("grant_type", "refresh_token")
                })

                call.response.headers.append("Content-Type", "application/json")
                call.respond(response.bodyAsText())
            }
        }
    }
}
