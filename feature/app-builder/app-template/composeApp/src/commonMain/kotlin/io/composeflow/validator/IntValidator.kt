package io.composeflow.validator

import com.github.michaelbull.result.runCatching
import io.composeflow.Res
import io.composeflow.invalid_int_format
import io.composeflow.must_be_greater_than_or_equal_to
import io.composeflow.must_be_greater_than_zero
import io.composeflow.must_be_smaller_than_or_equal_to

private class IntValidatorInternal(
    private val allowLessThanZero: Boolean = true,
    private val maxValue: Int = Int.MAX_VALUE,
    private val minValue: Int = Int.MIN_VALUE,
) : InputValidatorInternal {
    override fun validate(input: String) =
        runCatching {
            val value = input.toIntOrNull()
            if (value == null) {
                ValidateResult.Failure(Res.string.invalid_int_format)
            } else {
                if (!allowLessThanZero && value <= 0) {
                    ValidateResult.Failure(Res.string.must_be_greater_than_zero)
                } else if (value > maxValue) {
                    ValidateResult.Failure(
                        Res.string.must_be_smaller_than_or_equal_to,
                        listOf(maxValue),
                    )
                } else if (value < minValue) {
                    ValidateResult.Failure(
                        Res.string.must_be_greater_than_or_equal_to,
                        listOf(minValue),
                    )
                } else {
                    ValidateResult.Success
                }
            }
        }
}

class IntValidator(
    allowLessThanZero: Boolean = true,
    maxValue: Int = Int.MAX_VALUE,
    minValue: Int = Int.MIN_VALUE,
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(
            delegate =
                IntValidatorInternal(
                    allowLessThanZero = allowLessThanZero,
                    maxValue = maxValue,
                    minValue = minValue,
                ),
        ),
) : InputValidator by delegate
