package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

object NonEmptyStringValidatorInternal : InputValidatorInternal {

    private const val INVALID_EMPTY = "Must not be empty"

    override fun validate(input: String) = runCatching {
        if (input.isEmpty()) {
            ValidateResult.Failure(INVALID_EMPTY)
        } else {
            ValidateResult.Success
        }
    }
}

class NonEmptyStringValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(NonEmptyStringValidatorInternal),
) : InputValidator by delegate
