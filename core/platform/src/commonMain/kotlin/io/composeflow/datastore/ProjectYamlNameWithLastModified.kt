package io.composeflow.datastore

import kotlinx.datetime.Instant

data class ProjectYamlNameWithLastModified(
    val yaml: String,
    val lastModified: Instant?,
)
