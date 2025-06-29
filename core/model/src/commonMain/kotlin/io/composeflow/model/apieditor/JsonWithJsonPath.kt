package io.composeflow.model.apieditor

import io.composeflow.serializer.JsonAsStringSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
@SerialName("JsonWithJsonPath")
data class JsonWithJsonPath(
    val jsonPath: String,
    @Serializable(with = JsonAsStringSerializer::class)
    val jsonElement: JsonElement,
    val displayName: String? = null,
)

fun JsonElement.isList(): Boolean = this is JsonArray

fun JsonElement.asDisplayText(): String =
    when (this) {
        is JsonArray -> "List<JsonElement>"
        is JsonObject -> "JsonElement"
        is JsonPrimitive -> {
            if (isString) {
                "String"
            } else {
                "primitive"
            }
        }

        JsonNull -> "null"
    }
