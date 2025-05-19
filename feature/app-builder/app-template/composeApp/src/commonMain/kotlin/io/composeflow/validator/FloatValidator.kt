package io.composeflow.validator

import com.github.michaelbull.result.runCatching
import io.composeflow.Res
import io.composeflow.invalid_float_format
import io.composeflow.must_be_greater_than_or_equal_to
import io.composeflow.must_be_greater_than_zero
import io.composeflow.must_be_smaller_than_or_equal_to

private class FloatValidatorInternal(
    private val allowLessThanZero: Boolean = true,
    private val maxValue: Float? = null,
    private val minValue: Float? = null,
) : InputValidatorInternal {

    override fun validate(
        input: String,
    ) = runCatching {
        val value = input.toFloatOrNull()
        if (value == null) {
            ValidateResult.Failure(Res.string.invalid_float_format)
        } else {
            if (!allowLessThanZero && value <= 0) {
                ValidateResult.Failure(Res.string.must_be_greater_than_zero)
            }
            if (maxValue != null && value > maxValue) {
                ValidateResult.Failure(
                    Res.string.must_be_smaller_than_or_equal_to,
                    listOf(maxValue)
                )
            } else if (minValue != null && value < minValue) {
                ValidateResult.Failure(
                    Res.string.must_be_greater_than_or_equal_to,
                    listOf(minValue)
                )
            } else {
                ValidateResult.Success
            }
        }
    }
}

class FloatValidator(
    allowLessThanZero: Boolean = true,
    maxValue: Float? = null,
    minValue: Float? = null,
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(
            delegate = FloatValidatorInternal(
                allowLessThanZero = allowLessThanZero,
                maxValue = maxValue,
                minValue = minValue,
            ),
        ),
) : InputValidator by delegate