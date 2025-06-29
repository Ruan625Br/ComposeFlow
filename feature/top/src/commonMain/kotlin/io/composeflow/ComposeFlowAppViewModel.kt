package io.composeflow

import io.composeflow.auth.AuthRepository
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.datastore.LocalFirstProjectSaver
import io.composeflow.datastore.ProjectSaver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ComposeFlowAppViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val projectSaver: ProjectSaver = LocalFirstProjectSaver(),
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val loginResultUiState: StateFlow<LoginResultUiState> =
        authRepository.firebaseIdToken
            .mapLatest {
                when (it) {
                    null -> {
                        LoginResultUiState.NotStarted
                    }

                    else -> {
                        LoginResultUiState.Success(it)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LoginResultUiState.Loading,
            )

    fun onGoogleSignClicked() {
        authRepository.startGoogleSignInFlow()
    }

    suspend fun onLogOut() {
        projectSaver.deleteCacheProjects()
        authRepository.logOut()
    }
}

sealed interface LoginResultUiState {
    data object NotStarted : LoginResultUiState

    data object Loading : LoginResultUiState

    data class Success(
        val firebaseIdToken: FirebaseIdToken,
    ) : LoginResultUiState
}
