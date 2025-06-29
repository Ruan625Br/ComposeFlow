package io.composeflow.editor.validator

import com.github.michaelbull.result.runCatching

private object RatioValidatorInternal : InputValidatorInternal {
    private const val INVALID_FORMAT = "Invalid format"
    private const val INVALID_ASPECT_RATIO = "aspectRatio must be > 0"

    override fun validate(input: String) =
        runCatching {
            if (input.isEmpty()) {
                ValidateResult.Failure(INVALID_ASPECT_RATIO)
            } else {
                val value = input.toFloatOrNull()
                if (value == null) {
                    ValidateResult.Failure(INVALID_FORMAT)
                } else if (value <= 0) {
                    ValidateResult.Failure(INVALID_ASPECT_RATIO)
                } else {
                    ValidateResult.Success
                }
            }
        }
}

class RatioValidator(
    private val delegate: InputValidatorImpl = InputValidatorImpl(delegate = RatioValidatorInternal),
) : InputValidator by delegate
