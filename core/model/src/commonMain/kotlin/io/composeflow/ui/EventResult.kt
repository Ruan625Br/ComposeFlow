package io.composeflow.ui

import io.composeflow.model.project.issue.TrackableIssue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class EventResult(
    val errorMessages: MutableList<String> = mutableListOf(),
    val consumed: Boolean = true,
    val issues: MutableList<TrackableIssue> = mutableListOf(),
) {
    fun isSuccessful() = errorMessages.isEmpty() && issues.isEmpty()
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
