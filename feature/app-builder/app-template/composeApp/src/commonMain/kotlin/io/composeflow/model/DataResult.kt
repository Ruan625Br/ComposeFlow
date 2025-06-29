package io.composeflow.model

sealed interface DataResult<out T> {
    data object Idle : DataResult<Nothing>

    data object Loading : DataResult<Nothing>

    data class Success<T>(
        val result: T,
    ) : DataResult<T>

    data class Error(
        val message: String,
        val error: Throwable? = null,
    ) : DataResult<Nothing>
}
