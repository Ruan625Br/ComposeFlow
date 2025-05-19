package io.composeflow.ui.statusbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import moe.tlaster.precompose.viewmodel.ViewModel

class StatusBarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<StatusBarUiState>(StatusBarUiState.Normal)
    val uiState: StateFlow<StatusBarUiState> = _uiState

    fun onStatusBarUiStateChanged(newState: StatusBarUiState) {
        when (newState) {
            is StatusBarUiState.Failure -> {
                _uiState.value = newState
            }

            is StatusBarUiState.Loading -> {
                when (uiState.value) {
                    is StatusBarUiState.Failure -> {}
                    is StatusBarUiState.Loading -> {
                        _uiState.value = newState
                    }

                    StatusBarUiState.Normal -> {
                        _uiState.value = newState
                    }

                    is StatusBarUiState.Success -> {}
                    is StatusBarUiState.JsBrowserRunSuccess -> {}
                }
            }

            StatusBarUiState.Normal -> {
                _uiState.value = newState
            }

            is StatusBarUiState.Success -> {
                _uiState.value = newState
            }

            is StatusBarUiState.JsBrowserRunSuccess -> {
                _uiState.value = newState
            }
        }
    }
}
