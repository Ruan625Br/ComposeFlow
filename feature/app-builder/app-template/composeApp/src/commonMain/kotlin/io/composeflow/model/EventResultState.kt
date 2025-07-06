package io.composeflow.model

sealed interface EventResultState {
    data object NotStarted : EventResultState

    data object Loading : EventResultState

    data class Success(
        val message: String,
    ) : EventResultState

    data class Error(
        val message: String,
    ) : EventResultState
}
