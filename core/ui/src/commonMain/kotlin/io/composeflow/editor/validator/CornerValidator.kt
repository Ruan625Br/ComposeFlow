package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object CornerValidatorInternal : InputValidatorInternal {

    private const val INVALID_CORNER = "Must be non negative"
    private const val INVALID_FORMAT = "Must be integer"

    override fun validate(input: String) = runCatching {
        if (input.isEmpty()) {
            ValidateResult.Failure(INVALID_FORMAT)
        } else {
            val value = input.toIntOrNull()
            if (value == null) {
                ValidateResult.Failure(INVALID_FORMAT)
            } else if (value < 0) {
                ValidateResult.Failure(INVALID_CORNER)
            } else {
                ValidateResult.Success
            }
        }
    }
}

class CornerValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = CornerValidatorInternal),
) : InputValidator by delegate
