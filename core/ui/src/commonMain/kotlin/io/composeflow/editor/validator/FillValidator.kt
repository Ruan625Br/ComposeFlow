package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object FillValidatorInternal : InputValidatorInternal {
    private const val INVALID_FRACTION = "Fraction must be between 0.0 to 1.0"
    private const val INVALID_FORMAT = "Invalid format"

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Success
            } else {
                val value = input.toFloatOrNull()
                if (value == null) {
                    ValidateResult.Failure(INVALID_FORMAT)
                } else if (value < 0.0 || 1.0 < value) {
                    ValidateResult.Failure(INVALID_FRACTION)
                } else {
                    ValidateResult.Success
                }
            }
        }
}

class FillValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = FillValidatorInternal),
) : InputValidator by delegate
