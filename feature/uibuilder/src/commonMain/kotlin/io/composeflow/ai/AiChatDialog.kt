package io.composeflow.ai

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.project.Project
import io.composeflow.ui.modifier.hoverIconClickable
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.offsetAt
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AiChatDialog(
    project: Project,
    aiAssistantUiState: AiAssistantUiState,
    firebaseIdToken: FirebaseIdToken,
    onDismissDialog: () -> Unit,
    onAiAssistantUiStateUpdated: (AiAssistantUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel =
        viewModel(modelClass = AiChatDialogViewModel::class) {
            AiChatDialogViewModel(
                project = project,
                aiAssistantUiState = aiAssistantUiState,
                firebaseIdTokenArg = firebaseIdToken,
                onAiAssistantUiStateUpdated = onAiAssistantUiStateUpdated,
            )
        }
    val messages by viewModel.messages.collectAsState()

    Popup(
        alignment = Alignment.TopStart,
        focusable = true,
        onDismissRequest = onDismissDialog,
        onPreviewKeyEvent = { event ->
            if (event.type == KeyEventType.KeyDown &&
                event.key == Key.K &&
                (event.isMetaPressed || event.isCtrlPressed)
            ) {
                onDismissDialog()
                true
            } else {
                false
            }
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onDismissDialog()
                true
            } else {
                false
            }
        },
    ) {
        Box(
            modifier
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .width(400.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f),
                    shape = MaterialTheme.shapes.small,
                ),
        ) {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                var textFieldValue by remember { mutableStateOf("") }
                val onSendInput = {
                    viewModel.onSendGeneralRequest(textFieldValue)
                    textFieldValue = ""
                }
                TextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    },
                    trailingIcon = {
                        Row {
                            if (aiAssistantUiState.isGenerating.value) {
                                IconButton(
                                    onClick = {
                                        viewModel.onStopGeneration()
                                    },
                                    modifier = Modifier.hoverIconClickable(),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Stop,
                                        contentDescription = "Stop generation",
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (!aiAssistantUiState.isGenerating.value) {
                                        onSendInput()
                                    }
                                },
                                enabled = textFieldValue.isNotEmpty() && !aiAssistantUiState.isGenerating.value,
                            ) {
                                if (aiAssistantUiState.isGenerating.value) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(32.dp),
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send task",
                                    )
                                }
                            }
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .focusRequester(focusRequester)
                            .alpha(0.8f)
                            .then(
                                if (aiAssistantUiState.isGenerating.value) {
                                    Modifier
                                } else {
                                    Modifier.onKeyEvent {
                                        if (it.key == Key.Enter) {
                                            onSendInput()
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                },
                            ),
                )
                Spacer(Modifier.height(8.dp))
                val listState = rememberLazyListState()
                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(messages.lastIndex)
                    }
                }
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.weight(1f),
                ) {
                    items(messages) {
                        ChatMessage(messageModel = it, modifier = Modifier.alpha(0.7f))
                    }
                }
            }
        }
    }
}

private const val TEXT_LENGTH_THRESHOLD = 300

@Composable
private fun ChatMessage(
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
) {
    val hhmmFormat =
        Format {
            hour(Padding.NONE)
            chars(":")
            minute(Padding.ZERO)
        }

    var isExpanded by remember { mutableStateOf(false) }

    // Check if message is long enough to need expansion
    val isLongMessage = messageModel.message.length > TEXT_LENGTH_THRESHOLD

    // Get styled text for display
    val styledText =
        messageModel.getStyledText(isLongMessage = isLongMessage, isExpanded = isExpanded)

    // Determine icon color based on message type
    val iconColor =
        when (messageModel.messageType) {
            MessageType.ToolCall -> MaterialTheme.colorScheme.tertiary
            MessageType.ToolCallError -> MaterialTheme.colorScheme.error
            MessageType.Regular -> MaterialTheme.colorScheme.onSurface
        }

    if (messageModel.messageOwner == MessageOwner.User) {
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    messageModel.createdAt.format(
                        hhmmFormat,
                        offset =
                            TimeZone
                                .currentSystemDefault()
                                .offsetAt(messageModel.createdAt),
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                )
                Column(
                    modifier =
                        Modifier
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape =
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 0.dp,
                                        bottomEnd = 16.dp,
                                        bottomStart = 16.dp,
                                    ),
                            ).weight(1f)
                            .animateContentSize(),
                ) {
                    SelectionContainer {
                        Text(
                            text = styledText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    if (isLongMessage) {
                        TextButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        ) {
                            Text(
                                text = if (isExpanded) "Show less" else "Show more",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription =
                            when (messageModel.messageType) {
                                MessageType.ToolCall -> "Tool call message"
                                MessageType.ToolCallError -> "Tool call error message"
                                MessageType.Regular -> "AI message"
                            },
                        tint = iconColor,
                        modifier =
                            Modifier
                                .size(8.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Column(
                        modifier =
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape =
                                        RoundedCornerShape(
                                            topStart = 0.dp,
                                            topEnd = 16.dp,
                                            bottomEnd = 16.dp,
                                            bottomStart = 16.dp,
                                        ),
                                ).weight(1f)
                                .animateContentSize(),
                    ) {
                        SelectionContainer {
                            Text(
                                text = styledText,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                        if (isLongMessage) {
                            TextButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = if (isExpanded) "Show less" else "Show more",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
                Text(
                    messageModel.createdAt.format(
                        hhmmFormat,
                        offset =
                            TimeZone
                                .currentSystemDefault()
                                .offsetAt(messageModel.createdAt),
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun ChatMessageShortPreview() {
    ChatMessage(
        messageModel =
            MessageModel(
                messageOwner = MessageOwner.Ai,
                message = "This is a short AI message.",
                isFailed = false,
                createdAt = Clock.System.now(),
                messageType = MessageType.Regular,
            ),
    )
}

@Preview
@Composable
fun ChatMessageLongPreview() {
    ChatMessage(
        messageModel =
            MessageModel(
                messageOwner = MessageOwner.Ai,
                message =
                    "This is a very long AI message that exceeds 300 characters. ".repeat(10) +
                        "This should trigger the expand/collapse functionality with a 'Show more' button.",
                isFailed = false,
                createdAt = Clock.System.now(),
                messageType = MessageType.Regular,
            ),
    )
}

@Preview
@Composable
fun ChatMessageToolCallPreview() {
    ChatMessage(
        messageModel =
            MessageModel(
                messageOwner = MessageOwner.Ai,
                message = "Successfully added a new button component to the screen.",
                isFailed = false,
                createdAt = Clock.System.now(),
                messageType = MessageType.ToolCall,
                toolCallSummary = "Tool calls: AddComposeNode, UpdateModifier",
            ),
    )
}

@Preview
@Composable
fun ChatMessageErrorPreview() {
    ChatMessage(
        messageModel =
            MessageModel(
                messageOwner = MessageOwner.Ai,
                message = "Failed to execute the requested operation.",
                isFailed = true,
                createdAt = Clock.System.now(),
                messageType = MessageType.ToolCallError,
            ),
    )
}

@Preview
@Composable
fun ChatMessageUserPreview() {
    ChatMessage(
        messageModel =
            MessageModel(
                messageOwner = MessageOwner.User,
                message = "Can you add a button to the top of the screen?",
                isFailed = false,
                createdAt = Clock.System.now(),
                messageType = MessageType.Regular,
            ),
    )
}

@Preview
@Composable
fun AiChatDialogPreview() {
    // Note: This is a simplified preview as the full dialog requires complex state
    val sampleMessages =
        listOf(
            MessageModel(
                messageOwner = MessageOwner.User,
                message = "Add a button component",
                isFailed = false,
                createdAt = Clock.System.now(),
            ),
            MessageModel(
                messageOwner = MessageOwner.Ai,
                message = "I'll add a button component to your screen.",
                isFailed = false,
                createdAt = Clock.System.now(),
                messageType = MessageType.ToolCall,
                toolCallSummary = "Tool calls: AddComposeNode",
            ),
        )

    // Simplified preview showing just the message layout
    Column {
        sampleMessages.forEach { message ->
            ChatMessage(messageModel = message)
        }
    }
}
