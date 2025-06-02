package io.composeflow.ai.openrouter

import io.composeflow.ai.openrouter.tools.OpenRouterToolResult
import kotlinx.serialization.Serializable

@Serializable
data class OpenRouterResponseWrapper(
    val response: OpenRouterResponse,
    val message: String? = null,
    val tool_calls: List<OpenRouterToolResult>? = null,
) {
    fun isConsideredComplete(): Boolean {
        return tool_calls.isNullOrEmpty()
    }
}

@Serializable
data class OpenRouterResponse(
    val id: String,
    val provider: String,
    val model: String,
    val `object`: String,
    val created: Long,
    val choices: List<Choice>,
    val usage: CompletionUsage
)

@Serializable
data class Choice(
    val logprobs: String? = null,
    val finish_reason: String,
    val native_finish_reason: String,
    val index: Int,
    val message: Message
)

@Serializable
data class Message(
    val role: String,
    val content: String? = null,
    val refusal: String? = null,
    val reasoning: String? = null,
    val tool_calls: List<ToolCall>? = null
)

@Serializable
data class ToolCall(
    val id: String,
    val index: Int,
    val type: String,
    val function: FunctionCall
)

@Serializable
data class FunctionCall(
    val name: String,
    val arguments: String
)

@Serializable
data class CompletionUsage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)