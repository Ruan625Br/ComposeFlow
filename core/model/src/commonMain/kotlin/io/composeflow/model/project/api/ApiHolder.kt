package io.composeflow.model.project.api

import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.project.issue.TrackableIssue
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.LocationAwareFallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ApiHolder")
data class ApiHolder(
    @Serializable(with = LocationAwareFallbackMutableStateListSerializer::class)
    val apiDefinitions: MutableList<ApiDefinition> = mutableStateListEqualsOverrideOf(),
) {
    fun getValidApiDefinitions(): List<ApiDefinition> = apiDefinitions.filter { it.isValid() }

    fun findApiDefinitionOrNull(sourceId: String): ApiDefinition? = apiDefinitions.firstOrNull { it.id == sourceId }

    fun generateTrackableIssues(): List<TrackableIssue> = apiDefinitions.flatMap { it.generateTrackableIssue() }
}
