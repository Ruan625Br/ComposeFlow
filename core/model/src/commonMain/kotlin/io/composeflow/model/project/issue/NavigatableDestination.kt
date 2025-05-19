package io.composeflow.model.project.issue

import io.composeflow.model.InspectorTabDestination
import io.composeflow.model.TopLevelDestination
import io.composeflow.model.apieditor.ApiId
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigatableDestination {

    @Serializable
    data class UiBuilderScreen(
        val inspectorTabDestination: InspectorTabDestination? = null,
    ) : NavigatableDestination {
        override fun destinationToNavigate() = TopLevelDestination.UiBuilder
    }

    @Serializable
    data object ApiEditorScreen : NavigatableDestination {
        override fun destinationToNavigate() = TopLevelDestination.ApiEditor
    }

    fun destinationToNavigate(): TopLevelDestination
}

@Serializable
sealed interface DestinationContext {

    @Serializable
    data class UiBuilderScreen(
        val canvasEditableId: String,
        val composeNodeId: String,
    ) : DestinationContext

    @Serializable
    data class ApiEditorScreen(
        val apiId: ApiId
    ) : DestinationContext
}