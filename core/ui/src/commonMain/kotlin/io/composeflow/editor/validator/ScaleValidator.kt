package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object ScaleValidatorInternal : InputValidatorInternal {
    private const val INVALID_FORMAT = "Invalid format"
    private const val MUST_NOT_BE_EMPTY = "Must not be empty"

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Failure(MUST_NOT_BE_EMPTY)
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

class ScaleValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = ScaleValidatorInternal),
) : InputValidator by delegate
