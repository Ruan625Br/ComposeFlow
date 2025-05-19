package io.composeflow.ai

import kotlinx.serialization.Serializable

@Serializable
data class ClaudeResponse(
    val response: ClaudeResponseDetail,
)

@Serializable
data class ClaudeResponseDetail(
    val id: String,
    val type: String,
    val role: String,
    val model: String,
    val content: List<Content>,
    val stop_reason: String,
    val stop_sequence: String? = null,
    val usage: Usage
)

@Serializable
data class Content(
    val type: String,
    val text: String
)

@Serializable
data class Usage(
    val input_tokens: Int,
    val cache_creation_input_tokens: Int,
    val cache_read_input_tokens: Int,
    val output_tokens: Int
)