package io.composeflow.firebase

import co.touchlab.kermit.Logger
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import io.composeflow.BuildConfig
import io.composeflow.auth.google.TokenResponse
import io.composeflow.di.ServiceLocator
import io.composeflow.json.jsonSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.reflect.KClass

@Serializable
object EmptyRequest

/**
 * Wrapper class to call the Firebase/Google related APIs.
 * This class is a workaround because some of the APIs are not supported by Java SdK doesn't
 * (e.g. managing web apps in a Firebase project).
 */
class FirebaseApiCaller(
    private val okhttpClient: OkHttpClient = OkHttpClient(),
    private val ioDispatcher: CoroutineDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KeyIoDispatcher) {
            Dispatchers.IO
        },
) {
    suspend fun getWebAppConfig(
        firebaseProjectId: String,
        appId: String,
        tokenResponse: TokenResponse,
    ): Result<String?, Throwable> =
        runCatching {
            withContext(ioDispatcher) {
                val refreshedTokenResponse =
                    obtainAccessTokenWithRefreshToken(tokenResponse.refresh_token!!)

                val url =
                    "https://firebase.googleapis.com/v1beta1/projects/$firebaseProjectId/webApps/$appId/config"

                val request =
                    Request
                        .Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer ${refreshedTokenResponse?.access_token}")
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .get()
                        .build()

                okhttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Logger.e("Get web Apps failed. Code: ${response.code}, Body: $errorBody")
                        null
                    } else {
                        val responseBody = response.body?.string()
                        responseBody
                    }
                }
            }
        }

    suspend fun listAndroidApps(identifier: FirebaseAppIdentifier): Result<ListAndroidAppsResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: listAndroidApps")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/androidApps",
        )
    }

    suspend fun getAndroidAppConfig(
        identifier: FirebaseAppIdentifier,
        appId: String,
    ): Result<AppConfig?, Throwable> {
        Logger.i("FirebaseAPICaller: getAndroidAppConfig. appId: $appId")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/androidApps/$appId/config",
        )
    }

    suspend fun createAndroidApp(
        identifier: FirebaseAppIdentifier,
        packageName: String,
        displayName: String,
    ): Result<OperationResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: createAndroidApp. DisplayName: $displayName, PackageName: $packageName")
        return callApi(
            identifier = identifier,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/androidApps",
            requestBody = AndroidAppRequest(packageName = packageName, displayName = displayName),
        )
    }

    suspend fun listIosApps(identifier: FirebaseAppIdentifier): Result<ListIosAppsResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: listIosApps")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/iosApps",
        )
    }

    suspend fun getIosAppConfig(
        identifier: FirebaseAppIdentifier,
        appId: String,
    ): Result<AppConfig?, Throwable> {
        Logger.i("FirebaseAPICaller: getIosAppConfig. appId: $appId")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/iosApps/$appId/config",
        )
    }

    suspend fun createIosApp(
        identifier: FirebaseAppIdentifier,
        bundleId: String,
        displayName: String,
    ): Result<OperationResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: createIosApp. DisplayName: $displayName, BundleId: $bundleId")
        return callApi(
            identifier = identifier,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/iosApps",
            requestBody = IosAppRequest(bundleId = bundleId, displayName = displayName),
        )
    }

    suspend fun listWebApps(identifier: FirebaseAppIdentifier): Result<ListWebAppsResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: listWebApps")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/webApps",
        )
    }

    suspend fun getWebAppConfig(
        identifier: FirebaseAppIdentifier,
        appId: String,
    ): Result<String?, Throwable> {
        Logger.i("FirebaseAPICaller: getWebAppConfig. appId: $appId")
        return callApi(
            identifier = identifier,
            requestBody = EmptyRequest,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/webApps/$appId/config",
        )
    }

    suspend fun createWebApp(
        identifier: FirebaseAppIdentifier,
        displayName: String,
    ): Result<OperationResponse?, Throwable> {
        Logger.i("FirebaseAPICaller: createWebApps. DisplayName: $displayName")
        return callApi(
            identifier = identifier,
            endPoint = "https://firebase.googleapis.com/v1beta1/projects/${identifier.firebaseProjectId}/webApps",
            requestBody = WebAppRequest(displayName = displayName),
        )
    }

    suspend fun checkIdentityPlatformEnabled(identifier: FirebaseAppIdentifier): Result<Boolean?, Throwable> =
        runCatching {
            Logger.i("FirebaseAPICaller: checkIdentityPlatformEnabled")
            val refreshedTokenResponse =
                obtainAccessTokenWithRefreshToken(identifier.googleTokenResponse.refresh_token!!)

            val endPoint =
                "https://identitytoolkit.googleapis.com/admin/v2/projects/${identifier.firebaseProjectId}/config"
            val requestBuilder =
                Request
                    .Builder()
                    .url(endPoint)
                    .addHeader("Authorization", "Bearer ${refreshedTokenResponse?.access_token}")
                    .addHeader("Content-Type", "application/json; charset=utf-8")

            val request = requestBuilder.build()
            withContext(ioDispatcher) {
                okhttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.isSuccessful
                    } else {
                        val errorBody = response.body?.string()
                        Logger.e("Check identity Platform Enabled failed. Code: ${response.code}, Body: $errorBody")
                        false
                    }
                }
            }
        }

    private suspend inline fun <reified Request, reified Response> callApi(
        identifier: FirebaseAppIdentifier,
        requestBody: Request,
        endPoint: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Result<Response?, Throwable> =
        runCatching {
            val refreshedTokenResponse =
                obtainAccessTokenWithRefreshToken(identifier.googleTokenResponse.refresh_token!!)

            val requestBuilder =
                okhttp3.Request
                    .Builder()
                    .url(endPoint)
                    .addHeader("Authorization", "Bearer ${refreshedTokenResponse?.access_token}")
                    .addHeader("Content-Type", "application/json; charset=utf-8")
            if (requestBody !is EmptyRequest) {
                val requestBodyString =
                    jsonSerializer.encodeToString(serializer<Request>(), requestBody)
                val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                requestBuilder.post(requestBodyString.toRequestBody(jsonMediaType))
            }

            val request = requestBuilder.build()
            withContext(dispatcher) {
                okhttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string()
                        Logger.e("API call failed. Code: ${response.code}, Body: $errorBody. Endpoint: $endPoint, Request: $requestBody")
                        errorBody?.let {
                            val operationResponse =
                                jsonSerializer.decodeFromString<OperationResponse>(errorBody)
                            operationResponse.error?.let { error ->
                                throw Exception("Project ID: \"${identifier.firebaseProjectId}\": ${error.message}")
                            }
                        }
                            ?: throw Exception(
                                "Project ID: \"${identifier.firebaseProjectId}\": API call failed. $endPoint, body: $requestBody",
                            )
                    } else {
                        val responseBody = response.body?.string()
                        responseBody?.let {
                            if (isPrimitiveType(Response::class)) {
                                responseBody as Response?
                            } else {
                                jsonSerializer.decodeFromString(serializer<Response>(), responseBody)
                            }
                        }
                    }
                }
            }
        }

    suspend fun obtainAccessTokenWithRefreshToken(refreshToken: String): TokenResponse? {
        val url = "${BuildConfig.AUTH_ENDPOINT}/google/token"

        val requestBody =
            FormBody
                .Builder()
                .add("refresh_token", refreshToken)
                .build()

        val request =
            Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build()

        return withContext(Dispatchers.IO) {
            okhttpClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    Logger.e("Failed to obtain access token. HTTP code: ${response.code}")
                    Logger.e("Response body: $responseBody")
                    return@withContext null
                }

                if (responseBody != null) {
                    val tokenResponse = jsonSerializer.decodeFromString<TokenResponse>(responseBody)
                    tokenResponse
                } else {
                    Logger.e("Response body is null")
                    null
                }
            }
        }
    }

    private fun isPrimitiveType(kClass: KClass<*>): Boolean =
        when (kClass) {
            String::class,
            Int::class,
            Long::class,
            Short::class,
            Byte::class,
            Double::class,
            Float::class,
            Boolean::class,
            Char::class,
            -> true

            else -> false
        }
}
