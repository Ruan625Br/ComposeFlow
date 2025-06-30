package io.composeflow.ai

import co.touchlab.kermit.Logger
import io.composeflow.ai.openrouter.tools.ToolArgs
import io.composeflow.model.project.Project
import io.composeflow.ui.EventResult
import io.composeflow.ui.appstate.AppStateEditorOperator
import io.composeflow.ui.uibuilder.UiBuilderOperator

/**
 * Handles dispatching tool execution requests to appropriate operators.
 */
class ToolDispatcher(
    private val uiBuilderOperator: UiBuilderOperator = UiBuilderOperator(),
    private val appStateEditorOperator: AppStateEditorOperator = AppStateEditorOperator(),
) {
    /**
     * Dispatches a tool execution request to the appropriate operator.
     *
     * @param project The project to operate on
     * @param toolArgs The tool arguments containing the operation details
     * @return EventResult indicating success or failure with error messages
     */
    fun dispatchToolResponse(
        project: Project,
        toolArgs: ToolArgs,
    ): EventResult {
        Logger.i("dispatchToolResponse: $toolArgs")

        return when (toolArgs) {
            is ToolArgs.AddComposeNodeArgs -> {
                uiBuilderOperator.onAddComposeNodeToContainerNode(
                    project,
                    toolArgs.containerNodeId,
                    toolArgs.composeNodeYaml,
                    toolArgs.indexToDrop,
                )
            }

            is ToolArgs.AddModifierArgs -> {
                uiBuilderOperator.onAddModifier(
                    project,
                    toolArgs.composeNodeId,
                    toolArgs.modifierYaml,
                )
            }

            is ToolArgs.MoveComposeNodeToContainerArgs -> {
                uiBuilderOperator.onMoveComposeNodeToContainer(
                    project,
                    toolArgs.composeNodeId,
                    toolArgs.containerNodeId,
                    toolArgs.index,
                )
            }

            is ToolArgs.RemoveComposeNodeArgs -> {
                uiBuilderOperator.onRemoveComposeNode(
                    project,
                    toolArgs.composeNodeId,
                )
            }

            is ToolArgs.RemoveModifierArgs -> {
                uiBuilderOperator.onRemoveModifier(
                    project,
                    toolArgs.composeNodeId,
                    toolArgs.index,
                )
            }

            is ToolArgs.SwapModifiersArgs -> {
                uiBuilderOperator.onSwapModifiers(
                    project,
                    toolArgs.composeNodeId,
                    toolArgs.fromIndex,
                    toolArgs.toIndex,
                )
            }

            is ToolArgs.UpdateModifierArgs -> {
                uiBuilderOperator.onUpdateModifier(
                    project,
                    toolArgs.composeNodeId,
                    toolArgs.index,
                    toolArgs.modifierYaml,
                )
            }

            is ToolArgs.AddAppStateArgs -> {
                val result = appStateEditorOperator.onAddAppState(
                    project,
                    toolArgs.appStateYaml,
                )
                if (result.errorMessages.isNotEmpty()) {
                    toolArgs.result = result.errorMessages.joinToString("; ")
                }
                result
            }

            is ToolArgs.DeleteAppStateArgs -> {
                val result = appStateEditorOperator.onDeleteAppState(
                    project,
                    toolArgs.appStateId,
                )
                if (result.errorMessages.isNotEmpty()) {
                    toolArgs.result = result.errorMessages.joinToString("; ")
                }
                result
            }

            is ToolArgs.UpdateAppStateArgs -> {
                val result = appStateEditorOperator.onUpdateAppState(
                    project,
                    toolArgs.appStateYaml,
                )
                if (result.errorMessages.isNotEmpty()) {
                    toolArgs.result = result.errorMessages.joinToString("; ")
                }
                result
            }

            is ToolArgs.UpdateCustomDataTypeListDefaultValuesArgs -> {
                val result = appStateEditorOperator.onUpdateCustomDataTypeListDefaultValues(
                    project,
                    toolArgs.appStateId,
                    toolArgs.defaultValuesYaml,
                )
                if (result.errorMessages.isNotEmpty()) {
                    toolArgs.result = result.errorMessages.joinToString("; ")
                }
                result
            }

            is ToolArgs.ListAppStatesArgs -> {
                try {
                    val appStatesResult = appStateEditorOperator.onListAppStates(project)
                    toolArgs.result = appStatesResult
                    EventResult() // Success
                } catch (e: Exception) {
                    Logger.e(e) { "Error listing app states" }
                    toolArgs.result = "Error listing app states: ${e.message}"
                    EventResult().apply { errorMessages.add("Failed to list app states: ${e.message}") }
                }
            }

            is ToolArgs.GetAppStateArgs -> {
                try {
                    val appStateResult = appStateEditorOperator.onGetAppState(project, toolArgs.appStateId)
                    toolArgs.result = appStateResult
                    EventResult() // Success
                } catch (e: Exception) {
                    Logger.e(e) { "Error getting app state" }
                    toolArgs.result = "Error getting app state: ${e.message}"
                    EventResult().apply { errorMessages.add("Failed to get app state: ${e.message}") }
                }
            }

            is ToolArgs.FakeArgs -> {
                EventResult()
            }
        }
    }
}
