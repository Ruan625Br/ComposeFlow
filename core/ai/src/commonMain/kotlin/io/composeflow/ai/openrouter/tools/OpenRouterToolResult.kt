package io.composeflow.ai.openrouter.tools

import kotlinx.serialization.Serializable

/**
 * Interface for all tool result types.
 * Each implementation should have a unique SerialName annotation matching the tool_name in API responses.
 */
@Serializable(with = OpenRouterToolResultSerializer::class)
sealed interface OpenRouterToolResult {
    val tool_name: String
    val tool_call_id: String
    val tool_args: ToolArgs

    @Serializable
    data class AddComposeNodeArgs(
        override val tool_name: String = "add_compose_node_to_container",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.AddComposeNodeArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class RemoveComposeNodeArgs(
        override val tool_name: String = "remove_compose_node",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.RemoveComposeNodeArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class AddModifierArgs(
        override val tool_name: String = "add_modifier",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.AddModifierArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class UpdateModifierArgs(
        override val tool_name: String = "update_modifier",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.UpdateModifierArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class RemoveModifierArgs(
        override val tool_name: String = "remove_modifier",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.RemoveModifierArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class SwapModifiersArgs(
        override val tool_name: String = "swap_modifiers",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.SwapModifiersArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class MoveComposeNodeToContainerArgs(
        override val tool_name: String = "move_compose_node_to_container",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.MoveComposeNodeToContainerArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class AddAppStateArgs(
        override val tool_name: String = "add_app_state",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.AddAppStateArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class DeleteAppStateArgs(
        override val tool_name: String = "delete_app_state",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.DeleteAppStateArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class UpdateAppStateArgs(
        override val tool_name: String = "update_app_state",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.UpdateAppStateArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class UpdateCustomDataTypeListDefaultValuesArgs(
        override val tool_name: String = "update_custom_data_type_list_default_values",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.UpdateCustomDataTypeListDefaultValuesArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class ListAppStatesArgs(
        override val tool_name: String = "list_app_states",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.ListAppStatesArgs,
    ) : OpenRouterToolResult

    @Serializable
    data class GetAppStateArgs(
        override val tool_name: String = "get_app_state",
        override val tool_call_id: String,
        override val tool_args: ToolArgs.GetAppStateArgs,
    ) : OpenRouterToolResult
}