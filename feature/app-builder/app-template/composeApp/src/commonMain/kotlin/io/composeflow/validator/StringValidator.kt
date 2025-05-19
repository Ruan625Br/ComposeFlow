package io.composeflow.validator

import com.github.michaelbull.result.runCatching
import io.composeflow.Res
import io.composeflow.must_be_longer_than_or_equal_to
import io.composeflow.must_be_shorter_than_or_equal_to
import io.composeflow.must_not_be_blank

private class StringValidatorInternal(
    private val allowBlank: Boolean = true,
    private val maxLength: Int? = null,
    private val minLength: Int? = null,
) : InputValidatorInternal {

    override fun validate(
        input: String,
    ) = runCatching {
        if (!allowBlank && input.isBlank()) {
            ValidateResult.Failure(Res.string.must_not_be_blank)
        } else {
            if (maxLength != null && input.length > maxLength) {
                ValidateResult.Failure(
                    Res.string.must_be_shorter_than_or_equal_to,
                    listOf(maxLength)
                )
            } else if (minLength != null && input.length < minLength) {
                ValidateResult.Failure(
                    Res.string.must_be_longer_than_or_equal_to,
                    listOf(minLength)
                )
            } else {
                ValidateResult.Success
            }
        }
    }
}

class StringValidator(
    private val allowBlank: Boolean = true,
    private val maxLength: Int? = null,
    private val minLength: Int? = null,
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(
            delegate = StringValidatorInternal(
                allowBlank = allowBlank, maxLength = maxLength, minLength = minLength
            ),
        ),
) : InputValidator by delegate