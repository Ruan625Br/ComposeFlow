package io.composeflow.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import io.composeflow.Res
import io.composeflow.invalid_input

interface InputValidatorInternal {
    fun validate(input: String): Result<ValidateResult, Throwable>
}

interface InputValidator {
    fun validate(input: String): ValidateResult
}

class InputValidatorImpl(
    private val delegate: InputValidatorInternal,
) : InputValidator {
    override fun validate(input: String): ValidateResult =
        delegate.validate(input).mapBoth(
            success = {
                it
            },
            failure = {
                ValidateResult.Failure(messageResource = Res.string.invalid_input)
            },
        )
}
