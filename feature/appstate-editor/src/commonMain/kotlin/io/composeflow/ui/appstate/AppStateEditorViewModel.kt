package io.composeflow.ui.appstate

import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.state.AppState
import io.composeflow.model.state.copy
import io.composeflow.repository.ProjectRepository
import io.composeflow.util.generateUniqueName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AppStateEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
) : ViewModel() {

    fun onAppStateAdded(appState: AppState<*>) {
        val newName = generateUniqueName(
            appState.name,
            project.globalStateHolder.getStates(project).map { it.name }.toSet(),
        )
        val newState = appState.copy(name = newName)
        project.globalStateHolder.addState(newState)
        saveAppStates()
    }

    fun onAppStateDeleted(appState: AppState<*>) {
        project.globalStateHolder.removeState(appState.id)
        saveAppStates()
    }

    fun onAppStateUpdated(appState: AppState<*>) {
        val newName = generateUniqueName(
            appState.name,
            project.globalStateHolder.getStates(project)
                .filter { it.id != appState.id }
                .map { it.name }.toSet(),
        )
        val newState = appState.copy(name = newName)
        project.globalStateHolder.updateState(newState)
        saveAppStates()
    }

    fun onDataTypeListDefaultValueUpdated(
        appState: AppState<*>,
        defaultValues: List<DataTypeDefaultValue>,
    ) {
        if (appState is AppState.CustomDataTypeListAppState) {
            val newState = appState.copy(defaultValue = defaultValues)
            project.globalStateHolder.updateState(newState)
            saveAppStates()
        }
    }

    private fun saveAppStates() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
