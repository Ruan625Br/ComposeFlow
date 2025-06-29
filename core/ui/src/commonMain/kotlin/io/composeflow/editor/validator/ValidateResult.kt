package io.composeflow.editor.validator

sealed interface ValidateResult {
    data object Success : ValidateResult

    data class Failure(
        val message: String,
    ) : ValidateResult
}
