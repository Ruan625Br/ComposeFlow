package io.composeflow.validator

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface ValidateResult {
    data object Success : ValidateResult
    data class Failure(
        val messageResource: StringResource,
        val formatArgs: List<Any> = emptyList(),
    ) : ValidateResult
}

fun ValidateResult.isSuccess(): Boolean = this is ValidateResult.Success

@Composable
fun ValidateResult.asErrorMessage(): String? =
    when (this) {
        ValidateResult.Success -> null
        is ValidateResult.Failure -> stringResource(messageResource, *formatArgs.toTypedArray())
    }