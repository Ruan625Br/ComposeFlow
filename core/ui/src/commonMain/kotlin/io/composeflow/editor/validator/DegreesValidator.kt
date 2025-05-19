package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object DegreesValidatorInternal : InputValidatorInternal {

    private const val INVALID_FORMAT = "Invalid format"

    override fun validate(input: String) = runCatching {
        if (input.isEmpty()) {
            ValidateResult.Success
        } else {
            val value = input.toFloatOrNull()
            if (value == null) {
                ValidateResult.Failure(INVALID_FORMAT)
            } else {
                ValidateResult.Success
            }
        }
    }
}

class DegreesValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = DegreesValidatorInternal),
) : InputValidator by delegate
