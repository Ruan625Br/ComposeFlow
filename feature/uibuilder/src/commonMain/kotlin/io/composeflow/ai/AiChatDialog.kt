package io.composeflow.ai

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
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.offsetAt
import moe.tlaster.precompose.viewmodel.viewModel

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
                            ).weight(1f),
                ) {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
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
                            ).weight(1f),
                ) {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            color =
                                if (messageModel.isFailed) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp),
                        )
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
