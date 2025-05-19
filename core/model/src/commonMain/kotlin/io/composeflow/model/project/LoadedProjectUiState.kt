package io.composeflow.model.project

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

sealed interface LoadedProjectUiState {
    data object Loading : LoadedProjectUiState
    data object NotFound : LoadedProjectUiState
    data class Success(val project: Project) : LoadedProjectUiState
    data class Error(
        val yamlFileName: String,
        val throwable: Throwable,
    ) : LoadedProjectUiState
}

fun Result<Project?, Throwable>.asLoadedProjectUiState(
    projectId: String,
): LoadedProjectUiState =
    mapBoth(
        success = {
            it?.let { project ->
                LoadedProjectUiState.Success(project)
            } ?: LoadedProjectUiState.NotFound
        },
        failure = {
            LoadedProjectUiState.Error(
                yamlFileName = projectId,
                throwable = it,
            )
        },
    )

@OptIn(ExperimentalCoroutinesApi::class)
fun StateFlow<LoadedProjectUiState>.asProjectStateFlow(scope: CoroutineScope): StateFlow<Project> =
    mapLatest {
        when (it) {
            is LoadedProjectUiState.Success -> {
                it.project
            }

            else -> {
                Project()
            }
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Project(),
    )
