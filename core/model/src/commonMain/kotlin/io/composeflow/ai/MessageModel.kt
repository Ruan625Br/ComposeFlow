package io.composeflow.ai

import kotlinx.datetime.Instant

data class MessageModel(
    val messageOwner: MessageOwner,
    val message: String,
    val isFailed: Boolean = false,
    val createdAt: Instant,
    val messageType: MessageType = MessageType.Regular,
    val toolCallSummary: String? = null,
)

enum class MessageOwner {
    User,
    Ai,
}

enum class MessageType {
    Regular,
    ToolCall,
    ToolCallError,
}
