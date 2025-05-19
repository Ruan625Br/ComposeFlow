package io.composeflow.api

import arrow.optics.Optional
import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.apieditor.Method
import io.github.nomisrev.JsonPath
import io.github.nomisrev.select
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.Json.Default.decodeFromString
import kotlinx.serialization.json.JsonElement

private val httpClient: HttpClient =
    HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
    }

suspend fun callApi(apiDefinition: ApiDefinition, jsonPath: String): JsonElement {
    val response = httpClient.request(apiDefinition.url) {
        method = when (apiDefinition.method) {
            Method.Get -> HttpMethod.Get
            Method.Post -> HttpMethod.Post
            Method.Delete -> HttpMethod.Delete
            Method.Put -> HttpMethod.Put
            Method.Patch -> HttpMethod.Patch
        }
        headers {
            apiDefinition.headers.forEach { (key, value) ->
                append(key, value)
            }
        }
        url {
            apiDefinition.queryParameters.forEach { (key, value) ->
                parameters.append(key, value)
            }
        }
    }
    val json: JsonElement = decodeFromString(response.bodyAsText())
    val results: Optional<JsonElement, JsonElement> = JsonPath.select(jsonPath)
    return results.getOrNull(json)
        ?: throw IllegalStateException("Invalid json path: $jsonPath for API: ${apiDefinition.name}")
}