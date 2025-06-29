package io.composeflow.ai.openrouter.tools

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Interface for all tool argument types.
 * Each implementation should have a unique SerialName annotation matching the tool_name in API responses.
 */
@Serializable
sealed class ToolArgs {

    /**
     * Indicates the execution status of the tool in ComposeFlow app.
     */
    var status: ToolExecutionStatus = ToolExecutionStatus.NotExecuted
    
    /**
     * The result of the tool execution. For tools that return data (like list_app_states),
     * this will contain the returned data. For tools that perform actions, this contains
     * a success/error message.
     */
    var result: String = "Successfully executed."

    @Serializable
    @SerialName("add_compose_node_to_container")
    data class AddComposeNodeArgs(
        val containerNodeId: String,
        val composeNodeYaml: String,
        val indexToDrop: Int = 0
    ) : ToolArgs()

    @Serializable
    @SerialName("remove_compose_node")
    data class RemoveComposeNodeArgs(
        val composeNodeId: String
    ) : ToolArgs()

    @Serializable
    @SerialName("add_modifier")
    data class AddModifierArgs(
        val composeNodeId: String,
        val modifierYaml: String
    ) : ToolArgs()

    @Serializable
    @SerialName("update_modifier")
    data class UpdateModifierArgs(
        val composeNodeId: String,
        val index: Int,
        val modifierYaml: String
    ) : ToolArgs()

    @Serializable
    @SerialName("remove_modifier")
    data class RemoveModifierArgs(
        val composeNodeId: String,
        val index: Int
    ) : ToolArgs()

    @Serializable
    @SerialName("swap_modifiers")
    data class SwapModifiersArgs(
        val composeNodeId: String,
        val fromIndex: Int,
        val toIndex: Int
    ) : ToolArgs()

    @Serializable
    @SerialName("move_compose_node_to_container")
    data class MoveComposeNodeToContainerArgs(
        val composeNodeId: String,
        val containerNodeId: String,
        val index: Int
    ) : ToolArgs()

    /**
     * Represents a task completion result with summary information.
     */
//    @Serializable
//    @SerialName("attempt_completion")
//    data class AttemptCompletionArgs(
//        val result: String,
//        val command: String? = null
//    ) : ToolArgs()

    @Serializable
    @SerialName("add_app_state")
    data class AddAppStateArgs(
        val appStateYaml: String
    ) : ToolArgs()

    @Serializable
    @SerialName("delete_app_state")
    data class DeleteAppStateArgs(
        val appStateId: String
    ) : ToolArgs()

    @Serializable
    @SerialName("update_app_state")
    data class UpdateAppStateArgs(
        val appStateYaml: String
    ) : ToolArgs()

    @Serializable
    @SerialName("update_custom_data_type_list_default_values")
    data class UpdateCustomDataTypeListDefaultValuesArgs(
        val appStateId: String,
        val defaultValuesYaml: String
    ) : ToolArgs()

    @Serializable
    @SerialName("list_app_states")
    data class ListAppStatesArgs(
        val dummy: String = "" // Needed because data classes need at least one parameter
    ) : ToolArgs()

    @Serializable
    @SerialName("get_app_state")
    data class GetAppStateArgs(
        val appStateId: String
    ) : ToolArgs()

    @Serializable
    data class FakeArgs(
        val fakeString: String = "fakeString",
    ) : ToolArgs()
}

enum class ToolExecutionStatus {
    NotExecuted,
    Success,
    Error,
}