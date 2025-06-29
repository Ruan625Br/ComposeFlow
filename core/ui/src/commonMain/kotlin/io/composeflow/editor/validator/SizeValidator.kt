package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object SizeValidatorInternal : InputValidatorInternal {
    private const val INVALID_VALUE = "Must be non-negative"
    private const val INVALID_FORMAT = "Invalid format"
    private const val INVALID_RANGE = "Must be smaller than 10000"

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Failure(INVALID_VALUE)
            } else {
                val value = input.toIntOrNull()
                if (value == null) {
                    ValidateResult.Failure(INVALID_FORMAT)
                } else if (value >= 10000) {
                    ValidateResult.Failure(INVALID_RANGE)
                } else {
                    ValidateResult.Success
                }
            }
        }
}

class SizeValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = SizeValidatorInternal),
) : InputValidator by delegate
