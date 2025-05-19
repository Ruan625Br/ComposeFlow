package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object AlphaValidatorInternal : InputValidatorInternal {

    private const val INVALID_FRACTION = "Alpha must be between 0.0 to 1.0"

    override fun validate(input: String) = runCatching {
        if (input.isEmpty()) {
            ValidateResult.Success
        } else {
            val value = input.toFloatOrNull()
            if (value == null) {
                ValidateResult.Failure(INVALID_FRACTION)
            } else if (value < 0.0 || 1.0 < value) {
                ValidateResult.Failure(INVALID_FRACTION)
            } else {
                ValidateResult.Success
            }
        }
    }
}

class AlphaValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = AlphaValidatorInternal),
) : InputValidator by delegate
