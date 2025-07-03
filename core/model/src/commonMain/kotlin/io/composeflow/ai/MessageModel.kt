package io.composeflow.ai

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.datetime.Instant

data class MessageModel(
    val messageOwner: MessageOwner,
    val message: String,
    val isFailed: Boolean = false,
    val createdAt: Instant,
    val messageType: MessageType = MessageType.Regular,
    val toolCallSummary: String? = null,
) {
    /**
     * Creates an AnnotatedString with proper styling for tool call summaries and message content.
     * Tool call summaries are emphasized with bold font weight, and the rest of the content
     * has normal styling for visual hierarchy.
     */
    @Composable
    fun getStyledText(
        isLongMessage: Boolean = false,
        isExpanded: Boolean = true,
    ): AnnotatedString =
        buildAnnotatedString {
            // Add tool call prefix with emphasis if present
            if (messageType == MessageType.ToolCall && !toolCallSummary.isNullOrEmpty()) {
                withStyle(
                    style =
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                        ),
                ) {
                    append(toolCallSummary)
                }
                append("\n\n")
            }

            // Add main message content (normal styling - alpha will be handled by Text color)
            val contentToShow =
                if (isLongMessage && !isExpanded) {
                    message.take(300) + "..."
                } else {
                    message
                }
            when (messageType) {
                MessageType.Regular -> {
                    append(contentToShow)
                }

                MessageType.ToolCall -> {
                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.secondary),
                    ) {
                        append(contentToShow)
                    }
                }

                MessageType.ToolCallError -> {
                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.error),
                    ) {
                        append(contentToShow)
                    }
                }
            }
        }
}

enum class MessageOwner {
    User,
    Ai,
}

enum class MessageType {
    Regular,
    ToolCall,
    ToolCallError,
}
