package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching
import io.composeflow.editor.validator.KotlinIdentifierValidator.CONTAINS_INVALID_CHARS
import io.composeflow.editor.validator.KotlinIdentifierValidator.MUST_NOT_BE_EMPTY

object KotlinIdentifierValidator {
    const val CONTAINS_INVALID_CHARS = "Contains invalid character(s)"
    const val MUST_NOT_BE_EMPTY = "Must not be empty"
}

private object KotlinVariableNameValidatorInternal : InputValidatorInternal {
    private val kotlinVariableNameRegex = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Failure(MUST_NOT_BE_EMPTY)
            } else if (!input.matches(kotlinVariableNameRegex)) {
                ValidateResult.Failure(CONTAINS_INVALID_CHARS)
            } else {
                ValidateResult.Success
            }
        }
}

class KotlinVariableNameValidator(
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(delegate = KotlinVariableNameValidatorInternal),
) : InputValidator by delegate

private object KotlinClassNameValidatorInternal : InputValidatorInternal {
    private val kotlinClassNameRegex = Regex("^[a-zA-Z][a-zA-Z0-9_]*$")

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Failure(MUST_NOT_BE_EMPTY)
            } else if (!input.matches(kotlinClassNameRegex)) {
                ValidateResult.Failure(CONTAINS_INVALID_CHARS)
            } else {
                ValidateResult.Success
            }
        }
}

class KotlinClassNameValidator(
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(delegate = KotlinClassNameValidatorInternal),
) : InputValidator by delegate

private class KotlinPackageNameValidatorInternal(
    private val allowEmpty: Boolean,
) : InputValidatorInternal {
    private val kotlinClassNameRegex =
        Regex("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*\$")

    override fun validate(input: String) =
        runCatching {
            if (allowEmpty && input.isEmpty()) {
                ValidateResult.Success
            } else {
                if (input.isEmpty()) {
                    ValidateResult.Failure(MUST_NOT_BE_EMPTY)
                } else if (!input.matches(kotlinClassNameRegex)) {
                    ValidateResult.Failure(CONTAINS_INVALID_CHARS)
                } else {
                    ValidateResult.Success
                }
            }
        }
}

class KotlinPackageNameValidator(
    allowEmpty: Boolean = true,
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(delegate = KotlinPackageNameValidatorInternal(allowEmpty)),
) : InputValidator by delegate
