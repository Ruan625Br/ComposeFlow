package io.composeflow.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class EventResult(
    val errorMessages: MutableList<String> = mutableListOf(),
    val consumed: Boolean = true,
) {
    fun isSuccessful() = errorMessages.isEmpty()
}

fun EventResult.handleMessages(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    coroutineScope: CoroutineScope,
) {
    errorMessages.forEach {
        coroutineScope.launch {
            onShowSnackbar(it, null)
        }
    }
}
