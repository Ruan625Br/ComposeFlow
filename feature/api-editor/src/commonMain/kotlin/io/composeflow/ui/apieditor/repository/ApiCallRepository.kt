package io.composeflow.ui.apieditor.repository

import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.apieditor.Authorization
import io.composeflow.model.apieditor.Method
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.JsonElement

class ApiCallRepository(
    private val client: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    },
                )
            }
        },
) {
    fun makeApiCall(apiDefinition: ApiDefinition): Flow<JsonElement> =
        flow {
            val response =
                client.request(apiDefinition.url) {
                    method =
                        when (apiDefinition.method) {
                            Method.Get -> HttpMethod.Get
                            Method.Post -> HttpMethod.Post
                            Method.Put -> HttpMethod.Put
                            Method.Delete -> HttpMethod.Delete
                            Method.Patch -> HttpMethod.Patch
                        }
                    url {
                        headers {
                            apiDefinition.headers.forEach {
                                append(it.first, it.second.asStringValue())
                            }
                            when (val authorization = apiDefinition.authorization) {
                                is Authorization.BasicAuth -> {
                                    authorization.makeAuthorizationHeader()?.let {
                                        append(HttpHeaders.Authorization, it)
                                    }
                                }
                            }
                        }
                        parameters.apply {
                            apiDefinition.queryParameters.forEach {
                                append(it.first, it.second.asStringValue())
                            }
                        }
                    }
                    if (apiDefinition.method in listOf(Method.Post, Method.Put, Method.Patch)) {
//                setBody(apiDefinition.body) // Assuming `body` is of type `JsonElement`
                    }
                }
            val json: JsonElement = parseToJsonElement(response.bodyAsText())
            emit(json)
        }
}
