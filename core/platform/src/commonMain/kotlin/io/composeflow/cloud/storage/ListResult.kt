package io.composeflow.cloud.storage

data class ListResult(
    val prefixes: MutableList<Reference>,
    val items: MutableList<Reference>,
    val pageToken: String? = null,
)
