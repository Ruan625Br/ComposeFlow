package io.composeflow.template

import kotlin.uuid.Uuid

private val uuidRegex = Regex(
    pattern = "\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-" +
            "[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
            "[0-9a-fA-F]{12}\\b"
)

fun replaceUuids(input: String): String {
    // Map to store the relationship between old and new UUIDs
    val uuidMapping = mutableMapOf<String, String>()

    val output = uuidRegex.replace(input) { matchResult ->
        val oldUuid = matchResult.value
        val newUuid = uuidMapping.getOrPut(oldUuid) { Uuid.random().toString() }
        newUuid
    }
    return output
}