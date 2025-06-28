package io.composeflow.ui.appstate

import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.project.Project
import io.composeflow.model.state.AppState
import io.composeflow.repository.ProjectRepository
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AppStateEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
    private val appStateEditorOperator: AppStateEditorOperator = AppStateEditorOperator(),
) : ViewModel() {

    fun onAppStateAdded(appState: AppState<*>) {
        val result = appStateEditorOperator.addAppState(project, appState)
        if (result.errorMessages.isEmpty()) {
            saveAppStates()
        }
    }

    fun onAppStateDeleted(appState: AppState<*>) {
        val result = appStateEditorOperator.deleteAppState(project, appState.id)
        if (result.errorMessages.isEmpty()) {
            saveAppStates()
        }
    }

    fun onAppStateUpdated(appState: AppState<*>) {
        val result = appStateEditorOperator.updateAppState(project, appState)
        if (result.errorMessages.isEmpty()) {
            saveAppStates()
        }
    }

    fun onDataTypeListDefaultValueUpdated(
        appState: AppState<*>,
        defaultValues: List<DataTypeDefaultValue>,
    ) {
        val result = appStateEditorOperator.updateCustomDataTypeListDefaultValues(
            project,
            appState.id,
            defaultValues
        )
        if (result.errorMessages.isEmpty()) {
            saveAppStates()
        }
    }

    private fun saveAppStates() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
