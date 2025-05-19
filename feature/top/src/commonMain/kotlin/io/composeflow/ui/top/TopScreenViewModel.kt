package io.composeflow.ui.top

import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.asLoadedProjectUiState
import io.composeflow.model.settings.ComposeBuilderSettings
import io.composeflow.model.settings.SettingsRepository
import io.composeflow.platform.CloudProjectSaverRunner
import io.composeflow.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TopScreenViewModel(
    private val firebaseIdToken: FirebaseIdToken,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
    settingsRepository: SettingsRepository = SettingsRepository(),
) : ViewModel() {

    private val _projectUiState: MutableStateFlow<ProjectUiState> =
        MutableStateFlow(ProjectUiState.HasNotSelected.ProjectListLoading)
    val projectListUiState: StateFlow<ProjectUiState> = _projectUiState

    val settings = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ComposeBuilderSettings(),
    )

    init {
        loadProjectList()

        viewModelScope.launch {
            _projectUiState.collect {
                when (it) {
                    is ProjectUiState.HasNotSelected.ProjectListLoaded -> {}
                    ProjectUiState.HasNotSelected.ProjectListLoading -> {}
                    is ProjectUiState.Selected -> {
                        CloudProjectSaverRunner.projectId = it.project.id.toString()
                        CloudProjectSaverRunner.userId = firebaseIdToken.user_id
                        CloudProjectSaverRunner.startSavingProjectPeriodically()
                    }
                }
            }
        }
    }

    private fun loadProjectList() {
        viewModelScope.launch {
            _projectUiState.value = ProjectUiState.HasNotSelected.ProjectListLoading
            _projectUiState.value =
                ProjectUiState.HasNotSelected.ProjectListLoaded(
                    projectRepository.loadProjectIdList().map {
                        projectRepository.loadProject(it).asLoadedProjectUiState(it)
                    },
                )
        }
    }

    fun onCreateProject(projectName: String, packageName: String) {
        viewModelScope.launch {
            val project = projectRepository.createProject(
                projectName = projectName,
                packageName = packageName
            )
            _projectUiState.value = ProjectUiState.Selected(project)
        }
    }

    fun onCreateProjectWithScreens(
        projectName: String,
        packageName: String,
        screens: List<Screen>
    ) {
        viewModelScope.launch {
            val project = projectRepository.createProject(
                projectName = projectName,
                packageName = packageName
            )
            if (screens.isNotEmpty()) {
                project.screenHolder.screens.forEach {
                    project.screenHolder.deleteScreen(it)
                }
                screens.forEach {
                    // Avoid using the same Id
//                    project.screenHolder.addScreen(it.name, it.copy(id = Uuid.random()))
                    project.screenHolder.addScreen(it.name, it)
                }
            }
            projectRepository.updateProject(project)

            _projectUiState.value = ProjectUiState.Selected(project)
        }
    }

    fun onDeleteProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
            loadProjectList()
        }
    }

    fun onProjectSelected(project: Project) {
        _projectUiState.value = ProjectUiState.Selected(project)
    }
}

sealed interface ProjectUiState {

    sealed interface HasNotSelected : ProjectUiState {

        data object ProjectListLoading : HasNotSelected

        data class ProjectListLoaded(val projectList: List<LoadedProjectUiState>) : HasNotSelected
    }

    /**
     * The state where the project is created or the user selected the one project to edit
     */
    data class Selected(val project: Project) : ProjectUiState
}
