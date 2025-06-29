package io.composeflow.ui.jsonpath

import io.composeflow.model.apieditor.JsonWithJsonPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.jetbrains.jewel.foundation.lazy.tree.Tree
import org.jetbrains.jewel.foundation.lazy.tree.TreeGeneratorScope
import org.jetbrains.jewel.foundation.lazy.tree.buildTree

fun createJsonTreeWithJsonPath(json: String): Tree<JsonWithJsonPath> =
    buildTree {
        jsonNode(
            key = null,
            jsonElement = JsonWithJsonPath("", Json.parseToJsonElement(json)),
        )
    }

private fun TreeGeneratorScope<JsonWithJsonPath>.jsonNode(
    key: String?,
    jsonElement: JsonWithJsonPath,
) {
    when (jsonElement.jsonElement) {
        is JsonNull -> jsonPrimitiveNode(key, jsonElement)
        is JsonPrimitive -> jsonPrimitiveNode(key, jsonElement)
        is JsonObject -> jsonObjectNode(key, jsonElement)
        is JsonArray -> jsonArrayNode(key, jsonElement)
    }
}

private fun TreeGeneratorScope<JsonWithJsonPath>.jsonPrimitiveNode(
    key: String?,
    jsonPrimitive: JsonWithJsonPath,
) {
    addLeaf(
        data =
            jsonPrimitive
                .copy(displayName = "${getFormattedKey(key)}${getFormattedValue(jsonPrimitive.jsonElement as JsonPrimitive)}"),
    )
}

private fun TreeGeneratorScope<JsonWithJsonPath>.jsonObjectNode(
    key: String?,
    jsonObject: JsonWithJsonPath,
) {
    addNode(
        data = jsonObject.copy(displayName = "${getFormattedKey(key)}{object}"),
    ) {
        (jsonObject.jsonElement as JsonObject).entries.forEach { (key, jsonElement) ->
            val parentPath = if (jsonObject.jsonPath.isEmpty()) "" else "${jsonObject.jsonPath}."
            val path = parentPath + key
            this.jsonNode(key, JsonWithJsonPath(path, jsonElement))
        }
    }
}

private fun TreeGeneratorScope<JsonWithJsonPath>.jsonArrayNode(
    key: String?,
    jsonArray: JsonWithJsonPath,
) {
    addNode(
        data = jsonArray.copy(displayName = "${getFormattedKey(key)}[array]"),
    ) {
        (jsonArray.jsonElement as JsonArray).forEachIndexed { index, jsonElement ->
            val path = "${jsonArray.jsonPath}[$index]"
            this.jsonNode(index.toString(), JsonWithJsonPath(path, jsonElement))
        }
    }
}

private fun getFormattedKey(key: String?) =
    if (key.isNullOrBlank()) {
        ""
    } else {
        "$key: "
    }

private fun getFormattedValue(jsonPrimitive: JsonPrimitive) =
    if (jsonPrimitive.isString) {
        "\"${jsonPrimitive.contentOrNull}\""
    } else {
        jsonPrimitive.contentOrNull
    }
