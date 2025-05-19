package io.composeflow.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class EventResult(
    val messages: MutableList<String> = mutableListOf(),
    val consumed: Boolean = true,
)

fun EventResult.handleMessages(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    coroutineScope: CoroutineScope,
) {
    messages.forEach {
        coroutineScope.launch {
            onShowSnackbar(it, null)
        }
    }
}
