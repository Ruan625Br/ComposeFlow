package io.composeflow.ui.appstate

import co.touchlab.kermit.Logger
import io.composeflow.ksp.LlmParam
import io.composeflow.ksp.LlmTool
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.project.Project
import io.composeflow.model.state.AppState
import io.composeflow.model.state.copy
import io.composeflow.serializer.yamlSerializer
import io.composeflow.ui.EventResult
import io.composeflow.util.generateUniqueName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * Handles operations related to app state editor, such as adding, updating, or removing app states.
 * Operations in this class are exposed to the LLM to allow them to call it as tools as well as used
 * from the GUI in ComposeFlow.
 */
class AppStateEditorOperator {
    fun addAppState(
        project: Project,
        appState: AppState<*>,
    ): EventResult {
        val result = EventResult()
        try {
            val newName =
                generateUniqueName(
                    appState.name,
                    project.globalStateHolder
                        .getStates(project)
                        .map { it.name }
                        .toSet(),
                )
            val newState = appState.copy(name = newName)
            project.globalStateHolder.addState(newState)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding app state" }
            result.errorMessages.add("Failed to add app state: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_app_state",
        "Adds a new app state to the project. App states are used to store and manage application-wide data that persists across screens.",
    )
    fun onAddAppState(
        project: Project,
        @LlmParam(description = "The YAML representation of the AppState to be added. The name will be made unique if necessary.")
        appStateYaml: String,
    ): EventResult =
        try {
            val appState = yamlSerializer.decodeFromString<AppState<*>>(appStateYaml)
            addAppState(project, appState)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing app state YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse app state YAML: ${e.message}")
            }
        }

    fun deleteAppState(
        project: Project,
        appStateId: String,
    ): EventResult {
        val result = EventResult()
        try {
            val state = project.globalStateHolder.getStates(project).find { it.id == appStateId }
            if (state == null) {
                result.errorMessages.add("App state with ID $appStateId not found.")
                return result
            }
            project.globalStateHolder.removeState(appStateId)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting app state" }
            result.errorMessages.add("Failed to delete app state: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "delete_app_state",
        description = "Removes an app state from the project by its ID.",
    )
    fun onDeleteAppState(
        project: Project,
        @LlmParam(description = "The ID of the app state to be deleted.")
        appStateId: String,
    ): EventResult = deleteAppState(project, appStateId)

    fun updateAppState(
        project: Project,
        appState: AppState<*>,
    ): EventResult {
        val result = EventResult()
        try {
            val existingState =
                project.globalStateHolder.getStates(project).find { it.id == appState.id }
            if (existingState == null) {
                result.errorMessages.add("App state with ID ${appState.id} not found.")
                return result
            }

            val newName =
                generateUniqueName(
                    appState.name,
                    project.globalStateHolder
                        .getStates(project)
                        .filter { it.id != appState.id }
                        .map { it.name }
                        .toSet(),
                )
            val newState = appState.copy(name = newName)
            project.globalStateHolder.updateState(newState)
        } catch (e: Exception) {
            Logger.e(e) { "Error updating app state" }
            result.errorMessages.add("Failed to update app state: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_app_state",
        description = "Updates an existing app state in the project. The name will be made unique if necessary.",
    )
    fun onUpdateAppState(
        project: Project,
        @LlmParam(description = "The YAML representation of the updated AppState. Must include the ID of the state to update.")
        appStateYaml: String,
    ): EventResult =
        try {
            val appState = yamlSerializer.decodeFromString<AppState<*>>(appStateYaml)
            updateAppState(project, appState)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing app state YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse app state YAML: ${e.message}")
            }
        }

    fun updateCustomDataTypeListDefaultValues(
        project: Project,
        appStateId: String,
        defaultValues: List<DataTypeDefaultValue>,
    ): EventResult {
        val result = EventResult()
        try {
            val appState = project.globalStateHolder.getStates(project).find { it.id == appStateId }
            if (appState == null) {
                result.errorMessages.add("App state with ID $appStateId not found.")
                return result
            }

            if (appState !is AppState.CustomDataTypeListAppState) {
                result.errorMessages.add("App state with ID $appStateId is not a CustomDataTypeListAppState.")
                return result
            }

            val newState = appState.copy(defaultValue = defaultValues)
            project.globalStateHolder.updateState(newState)
        } catch (e: Exception) {
            Logger.e(e) { "Error updating custom data type list default values" }
            result.errorMessages.add("Failed to update default values: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_custom_data_type_list_default_values",
        description = "Updates the default values for a custom data type list app state.",
    )
    fun onUpdateCustomDataTypeListDefaultValues(
        project: Project,
        @LlmParam(description = "The ID of the CustomDataTypeListAppState to update.")
        appStateId: String,
        @LlmParam(description = "The YAML representation of the list of DataTypeDefaultValue objects.")
        defaultValuesYaml: String,
    ): EventResult =
        try {
            val defaultValues =
                yamlSerializer.decodeFromString<List<DataTypeDefaultValue>>(defaultValuesYaml)
            updateCustomDataTypeListDefaultValues(project, appStateId, defaultValues)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing default values YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse default values YAML: ${e.message}")
            }
        }

    @LlmTool(
        name = "list_app_states",
        description = "Lists all app states in the project. Returns a YAML representation of all states.",
    )
    fun onListAppStates(project: Project): String =
        try {
            val states = project.globalStateHolder.getStates(project)
            yamlSerializer.encodeToString(states)
        } catch (e: Exception) {
            Logger.e(e) { "Error listing app states" }
            "Error listing app states: ${e.message}"
        }

    @LlmTool(
        name = "get_app_state",
        description = "Gets a specific app state by its ID. Returns a YAML representation of the state.",
    )
    fun onGetAppState(
        project: Project,
        @LlmParam(description = "The ID of the app state to retrieve.")
        appStateId: String,
    ): String =
        try {
            val state = project.globalStateHolder.getStates(project).find { it.id == appStateId }
            if (state != null) {
                yamlSerializer.encodeToString(state)
            } else {
                "App state with ID $appStateId not found."
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error getting app state" }
            "Error getting app state: ${e.message}"
        }
}
