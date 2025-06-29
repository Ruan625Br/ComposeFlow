package io.composeflow.ksp

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Transforms the LlmToolInfo into the desired JSON format for MCP tools.
 */
object LlmToolJsonTransformer {
    private val json =
        Json {
            prettyPrint = true
            encodeDefaults = true
        }

    /**
     * Transforms LlmToolInfo into the MCP tool JSON format.
     */
    fun transform(toolInfo: LlmToolInfo): String {
        val mcpToolJson =
            buildJsonObject {
                // Add name and description
                put("name", toolInfo.name)
                put("description", toolInfo.description)

                // Create input_schema
                putJsonObject("input_schema") {
                    put("type", "object")

                    // Create properties object
                    putJsonObject("properties") {
                        // Add each parameter as a property (excluding "project" parameter)
                        toolInfo.parameters.forEach { param ->
                            if (param.name != "project") {
                                putJsonObject(param.name) {
                                    put("type", mapKotlinTypeToJsonType(param.type))
                                    put("description", param.description)
                                }
                            }
                        }
                    }

                    // Add required array
                    putJsonArray("required") {
                        toolInfo.parameters.forEach { param ->
                            if (param.required && param.name != "project") {
                                add(JsonPrimitive(param.name))
                            }
                        }
                    }
                }
            }

        return json.encodeToString(mcpToolJson)
    }

    /**
     * Maps Kotlin types to JSON schema types.
     */
    private fun mapKotlinTypeToJsonType(kotlinType: String): String =
        when {
            kotlinType.contains("String") -> "string"
            kotlinType.contains("Int") ||
                kotlinType.contains("Long") ||
                kotlinType.contains("Float") ||
                kotlinType.contains("Double") -> "number"
            kotlinType.contains("Boolean") -> "boolean"
            kotlinType.contains("List") ||
                kotlinType.contains("Array") ||
                kotlinType.contains("Set") -> "array"
            else -> "object"
        }
}

/**
 * Modified version of the generateJsonFile function in LlmToolProcessor.
 * This version uses the LlmToolJsonTransformer to generate the MCP tool JSON format.
 */
fun generateMcpToolJsonFile(
    toolInfo: LlmToolInfo,
    outputDir: String,
) {
    val fileName = "${toolInfo.name.replace(" ", "_")}_mcp_tool"
    val fileContent = LlmToolJsonTransformer.transform(toolInfo)

    val outputDirFile = java.io.File(outputDir)
    outputDirFile.mkdirs()

    val file = outputDirFile.resolve("$fileName.json")
    file.writeText(fileContent)
}
