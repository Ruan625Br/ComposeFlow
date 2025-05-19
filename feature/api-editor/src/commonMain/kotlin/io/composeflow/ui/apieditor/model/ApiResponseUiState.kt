package io.composeflow.ui.apieditor.model

import kotlinx.serialization.json.JsonElement

sealed class ApiResponseUiState {

    data object NotStarted : ApiResponseUiState()
    data object Loading : ApiResponseUiState()
    data class Success(val data: JsonElement) : ApiResponseUiState()
    data class Error(val throwable: Throwable?) : ApiResponseUiState()
}
