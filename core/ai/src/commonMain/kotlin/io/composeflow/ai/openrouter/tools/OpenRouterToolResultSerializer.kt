package io.composeflow.ai.openrouter.tools

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * A custom serializer for OpenApiRouterToolResult that properly handles tool_args that are sent as JSON strings.
 */
object OpenRouterToolResultSerializer : KSerializer<OpenRouterToolResult> {
    private val json = Json { ignoreUnknownKeys = true }

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("OpenApiRouterToolResult") {
            element<String>("tool_name")
            element<String>("tool_call_id")
            element<JsonElement>("tool_args")
        }

    override fun serialize(encoder: Encoder, value: OpenRouterToolResult) {
        if (encoder !is JsonEncoder) {
            throw SerializationException("This serializer only works with JSON")
        }

        val jsonObject = buildJsonObject {
            put("tool_name", value.tool_name)
            put("tool_call_id", value.tool_call_id)

            // Serialize tool_args based on its type
            when (value) {
                is OpenRouterToolResult.AddComposeNodeArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.AddComposeNodeArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.RemoveComposeNodeArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.RemoveComposeNodeArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.AddModifierArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.AddModifierArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.UpdateModifierArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.UpdateModifierArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.RemoveModifierArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.RemoveModifierArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.SwapModifiersArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.SwapModifiersArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.MoveComposeNodeToContainerArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.MoveComposeNodeToContainerArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.AddAppStateArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.AddAppStateArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.DeleteAppStateArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.DeleteAppStateArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.UpdateAppStateArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.UpdateAppStateArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.UpdateCustomDataTypeListDefaultValuesArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.UpdateCustomDataTypeListDefaultValuesArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.ListAppStatesArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.ListAppStatesArgs.serializer(),
                            value.tool_args
                        )
                    )

                is OpenRouterToolResult.GetAppStateArgs ->
                    put(
                        "tool_args", json.encodeToJsonElement(
                            ToolArgs.GetAppStateArgs.serializer(),
                            value.tool_args
                        )
                    )
            }
        }

        encoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): OpenRouterToolResult {
        if (decoder !is JsonDecoder) {
            throw SerializationException("This serializer only works with JSON")
        }

        val element = decoder.decodeJsonElement()
        val jsonObject = element.jsonObject

        val toolName = jsonObject["tool_name"]?.jsonPrimitive?.content
            ?: throw SerializationException("Missing tool_name field")
        val toolCallId = jsonObject["tool_call_id"]?.jsonPrimitive?.content
            ?: throw SerializationException("Missing tool_call_id field")

        // Get the tool_args element
        val toolArgsElement = jsonObject["tool_args"]
            ?: throw SerializationException("Missing tool_args field")

        // Process the tool_args - handle if it's a JSON string
        val processedToolArgsElement =
            if (toolArgsElement is JsonPrimitive && toolArgsElement.isString) {
                try {
                    json.parseToJsonElement(toolArgsElement.content)
                } catch (e: Exception) {
                    throw SerializationException("Failed to parse tool_args JSON string: ${e.message}")
                }
            } else {
                toolArgsElement
            }

        // Create the appropriate implementation based on tool_name
        return when (toolName) {
            "add_compose_node_to_container" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.AddComposeNodeArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.AddComposeNodeArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "remove_compose_node" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.RemoveComposeNodeArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.RemoveComposeNodeArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "add_modifier" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.AddModifierArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.AddModifierArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "update_modifier" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.UpdateModifierArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.UpdateModifierArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "remove_modifier" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.RemoveModifierArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.RemoveModifierArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "swap_modifiers" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.SwapModifiersArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.SwapModifiersArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "move_compose_node_to_container" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.MoveComposeNodeToContainerArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.MoveComposeNodeToContainerArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "add_app_state" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.AddAppStateArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.AddAppStateArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "delete_app_state" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.DeleteAppStateArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.DeleteAppStateArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "update_app_state" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.UpdateAppStateArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.UpdateAppStateArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "update_custom_data_type_list_default_values" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.UpdateCustomDataTypeListDefaultValuesArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.UpdateCustomDataTypeListDefaultValuesArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "list_app_states" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.ListAppStatesArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.ListAppStatesArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            "get_app_state" -> {
                val toolArgs = json.decodeFromJsonElement(
                    ToolArgs.GetAppStateArgs.serializer(),
                    processedToolArgsElement
                )
                OpenRouterToolResult.GetAppStateArgs(
                    tool_name = toolName,
                    tool_call_id = toolCallId,
                    tool_args = toolArgs,
                )
            }

            else -> throw SerializationException("Unknown tool type: $toolName")
        }
    }
}