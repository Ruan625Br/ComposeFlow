package io.composeflow.model.apieditor

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

data class JsonWithJsonPath(
    val jsonPath: String,
    val jsonElement: JsonElement,
)

fun JsonElement.isList(): Boolean = this is JsonArray
fun JsonElement.asDisplayText(): String = when (this) {
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
