package io.composeflow.ai

import io.composeflow.ai.openrouter.OpenRouterResponseWrapper
import io.composeflow.ai.openrouter.tools.ToolArgs
import io.composeflow.model.project.appscreen.screen.Screen

sealed interface AiRepositoryResponse

sealed interface ErrorResponse : AiRepositoryResponse {
    var retryCount: Int
    val originalPrompt: String
    val errorMessage: String
    val throwable: Throwable?
}

sealed interface SuccessResponse : AiRepositoryResponse

sealed interface CreateScreenResponse {

    val requestId: String?

    data class BeforeRequest(
        override val requestId: String? = null
    ) : CreateScreenResponse

    data class Success(
        val screen: Screen,
        val message: String,
        override val requestId: String? = null,
    ) : CreateScreenResponse, SuccessResponse

    data class Error(
        override val originalPrompt: String,
        override val errorMessage: String,
        // Represents the yaml content
        val errorContent: String,
        override val throwable: Throwable? = null,
        override val requestId: String? = null,
        override var retryCount: Int = 0,
    ) : ErrorResponse, CreateScreenResponse
}

sealed interface ToolResponse : AiRepositoryResponse {

    val originalPrompt: String
    val message: String
    val previousToolArgs: List<ToolArgs>

    data class Success(
        val response: OpenRouterResponseWrapper,
        override val message: String,
        override val originalPrompt: String,
        override val previousToolArgs: List<ToolArgs> = emptyList(),
    ) : ToolResponse

    data class Error(
        override val originalPrompt: String,
        override val previousToolArgs: List<ToolArgs> = emptyList(),
        override val message: String,
        val throwable: Throwable? = null,
    ) : ToolResponse
}
