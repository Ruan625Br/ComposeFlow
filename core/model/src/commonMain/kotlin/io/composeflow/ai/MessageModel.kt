package io.composeflow.ai

import kotlinx.datetime.Instant

data class MessageModel(
    val messageOwner: MessageOwner,
    val message: String,
    val isFailed: Boolean = false,
    val createdAt: Instant,
)

enum class MessageOwner {
    User,
    Ai,
}
