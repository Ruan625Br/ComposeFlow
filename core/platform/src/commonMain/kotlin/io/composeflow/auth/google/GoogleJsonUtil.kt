package io.composeflow.auth.google

import io.composeflow.json.jsonSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Extract the client_id from the google-services.json file.
 * The client_id is expected to be used for the server's client ID for Google Sign-In as explained here:
 * https://firebase.google.com/docs/auth/android/google-signin
 */
fun extractClientIdFromGoogleServicesJson(
    json: String,
    packageName: String,
): String? {
    val rootJson = jsonSerializer.parseToJsonElement(json) as JsonObject
    val clients = rootJson["client"] as JsonArray
    val targetClient =
        clients.firstOrNull {
            val clientInfo = (it as? JsonObject)?.get("client_info") as? JsonObject
            clientInfo
                ?.get("android_client_info")
                ?.jsonObject
                ?.get("package_name")
                ?.toString()
                ?.replace("\"", "") == packageName
        }
    val targetOauthClient =
        (targetClient?.jsonObject?.get("oauth_client") as? JsonArray)?.firstOrNull {
            (it as? JsonObject)?.get("client_type")?.toString() == "3"
        }
    val clientId = targetOauthClient?.jsonObject?.get("client_id")?.toString()
    return clientId
}
