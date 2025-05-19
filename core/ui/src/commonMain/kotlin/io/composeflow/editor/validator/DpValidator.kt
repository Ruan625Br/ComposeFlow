package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private const val INVALID_PADDING = "Must be non-negative"
private const val INVALID_RANGE = "Must be smaller than 1000"

private class DpValidatorInternal(
    private val allowEmpty: Boolean = false,
) : InputValidatorInternal {

    override fun validate(input: String) = runCatching {
        if (input.isEmpty()) {
            if (allowEmpty) {
                ValidateResult.Success
            } else {
                ValidateResult.Failure(MUST_NOT_BE_EMPTY)
            }
        } else {
            val value = input.toIntOrNull()
            if (value == null) {
                ValidateResult.Failure(INVALID_FORMAT)
            } else if (value < 0) {
                ValidateResult.Failure(INVALID_PADDING)
            } else if (value >= 1000) {
                ValidateResult.Failure(INVALID_RANGE)
            } else {
                ValidateResult.Success
            }
        }
    }
}

class DpValidator(
    private val allowEmpty: Boolean = false,
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(delegate = DpValidatorInternal(allowEmpty = allowEmpty)),
) : InputValidator by delegate
