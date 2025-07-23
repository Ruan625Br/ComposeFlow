package io.composeflow.datastore

import kotlin.time.Instant

@OptIn(kotlin.time.ExperimentalTime::class)
data class ProjectYamlNameWithLastModified(
    val yaml: String,
    val lastModified: Instant?,
)
