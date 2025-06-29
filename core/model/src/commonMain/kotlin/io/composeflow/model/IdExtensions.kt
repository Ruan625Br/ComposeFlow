package io.composeflow.model

import kotlin.uuid.Uuid

typealias IdMap = MutableMap<String, String>

fun IdMap.createNewIdIfNotPresent(existingId: String): String {
    val newId =
        if (contains(existingId)) {
            get(existingId)
        } else {
            set(existingId, Uuid.random().toString())
            get(existingId)
        }
    return newId!!
}
