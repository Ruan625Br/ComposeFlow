package io.composeflow.ui.appstate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.project.asLoadedProjectUiState
import io.composeflow.model.project.asProjectStateFlow
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
    projectId: String,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
) : ViewModel() {
    private val _projectUiState: MutableStateFlow<LoadedProjectUiState> =
        MutableStateFlow(LoadedProjectUiState.Success(Project()))
    val projectUiState: StateFlow<LoadedProjectUiState> = _projectUiState

    private var project by mutableStateOf(_projectUiState.asProjectStateFlow(viewModelScope).value)

    private var focusedDataTypeIndex by mutableStateOf<Int?>(null)

    init {
        viewModelScope.launch {
            _projectUiState.value = LoadedProjectUiState.Loading
            _projectUiState.value =
                projectRepository.loadProject(projectId).asLoadedProjectUiState(projectId)
            when (val state = _projectUiState.value) {
                is LoadedProjectUiState.Success -> {
                    project = state.project
                    if (project.dataTypeHolder.dataTypes.size > 0) {
                        focusedDataTypeIndex = 0
                    }
                }

                else -> {}
            }
        }
    }

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
