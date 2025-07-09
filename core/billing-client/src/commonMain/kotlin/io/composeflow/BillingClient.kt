package io.composeflow

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import io.composeflow.auth.AuthRepository
import io.composeflow.di.ServiceLocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody

class BillingClient(
    private val authRepository: AuthRepository = AuthRepository(),
    private val okHttpClient: OkHttpClient = OkHttpClient(),
    private val ioDispatcher: CoroutineDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KEY_IO_DISPATCHER) {
            Dispatchers.IO
        },
    private val endpoint: String = BuildConfig.BILLING_ENDPOINT,
) {
    suspend fun createPricingTableLink(): Result<String, Throwable> =
        runCatching {
            withContext(ioDispatcher) {
                val token =
                    authRepository.firebaseIdToken
                        .take(1)
                        .last()
                        ?.rawToken
                        ?: throw Exception("No token")

                val requestBuilder =
                    okhttp3.Request
                        .Builder()
                        .url("$endpoint/createPricingTableLink")
                        .addHeader("Authorization", "Bearer $token")
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                val requestBodyString = "{}"
                val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                requestBuilder.post(requestBodyString.toRequestBody(jsonMediaType))

                val request = requestBuilder.build()

                okHttpClient.newCall(request).execute().use { response ->
                    response.body?.string()?.let { body ->
                        val jsonElement = Json.parseToJsonElement(body)

                        val error = jsonElement.jsonObject["error"]
                        if (error != null) {
                            val code = error.jsonObject["code"]
                            val message = error.jsonObject["message"]
                            throw Exception("code: $code, message: $message")
                        }

                        val url =
                            jsonElement.jsonObject["pricingTableUrl"]?.jsonPrimitive?.content
                        return@withContext url
                    }
                }
            }?.toString() ?: throw Exception("No res")
        }
}
