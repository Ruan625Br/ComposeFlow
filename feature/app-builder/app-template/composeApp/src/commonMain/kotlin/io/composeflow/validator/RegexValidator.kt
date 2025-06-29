package io.composeflow.validator

import com.github.michaelbull.result.runCatching
import io.composeflow.Res
import io.composeflow.invalid_email_address
import io.composeflow.invalid_input
import org.jetbrains.compose.resources.StringResource

private data class RegexValidatorInternal(
    private val regex: Regex,
    private val errorMessageInput: Pair<StringResource, List<Any>>,
) : InputValidatorInternal {
    override fun validate(input: String) =
        runCatching {
            if (!input.matches(regex)) {
                ValidateResult.Failure(errorMessageInput.first, errorMessageInput.second)
            } else {
                ValidateResult.Success
            }
        }
}

class EmailValidator(
    regexString: String = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
    errorMessageInput: Pair<StringResource, List<Any>> = Res.string.invalid_email_address to emptyList<String>(),
) : RegexValidator(regexString, errorMessageInput)

open class RegexValidator(
    private val regexString: String,
    private val errorMessageInput: Pair<StringResource, List<Any>> = Res.string.invalid_input to emptyList<String>(),
    private val delegate: InputValidatorImpl =
        InputValidatorImpl(
            delegate =
                RegexValidatorInternal(
                    regex = Regex(regexString),
                    errorMessageInput = errorMessageInput,
                ),
        ),
) : InputValidator by delegate
