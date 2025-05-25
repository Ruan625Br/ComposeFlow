package io.composeflow.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberComponentRectPositionProvider
import io.composeflow.Res
import io.composeflow.ai.subaction.ScreenPromptsCreatedContent
import io.composeflow.ai_generating_response
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.ui.adaptive.ProvideDeviceSizeDp
import io.composeflow.ui.common.AppTheme
import io.composeflow.ui.emptyCanvasNodeCallbacks
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.offsetAt
import moe.tlaster.precompose.LocalWindow
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AiAssistantDialog(
    project: Project,
    onConfirmProjectWithScreens: (projectName: String, packageName: String, screens: List<Screen>) -> Unit,
    callbacks: AiAssistantDialogCallbacks = AiAssistantDialogCallbacks(),
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    projectCreationPrompt: String = "",
) {
    val viewModel =
        viewModel(modelClass = AiAssistantViewModel::class) {
            AiAssistantViewModel(
                projectCreationQuery = projectCreationPrompt
            )
        }
    val uiState by viewModel.uiState.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val updateCallbacks = callbacks.copy(
        onScreenTitleUpdated = viewModel::onScreenTitleUpdated,
        onScreenPromptUpdated = viewModel::onScreenPromptUpdated,
        onScreenPromptDeleted = viewModel::onScreenPromptDeleted,
        onProceedToGenerateScreens = viewModel::onProceedToGenerateScreens,
        onRenderedErrorDetected = viewModel::onRenderedErrorDetected,
        onConfirmProjectWithScreens = onConfirmProjectWithScreens,
    )
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        popupPositionProvider = rememberComponentRectPositionProvider(
            anchor = Alignment.Center,
            alignment = Alignment.Center,
        ),
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        val horizontalPadding = 32.dp
        val verticalPadding = 32.dp
        val windowSize = LocalWindow.current.size
        val widthDp = windowSize.width.dp - horizontalPadding * 2
        val heightDp = windowSize.height.dp - verticalPadding * 2
        Surface(modifier = modifier.size(widthDp, heightDp)) {
            Row(modifier = Modifier.fillMaxSize()) {
                AiConversationArea(
                    callbacks = updateCallbacks,
                    onCloseClick = onCloseClick,
                    onDiscardResult = viewModel::onDiscardResult,
                    messages = messages,
                    uiState = uiState,
                    onSendUserInput = viewModel::onSendUserInput,
                    modifier = Modifier.weight(4f)
                        .fillMaxSize()
                )

                AiWorkspaceArea(
                    project = project,
                    uiState = uiState,
                    callbacks = updateCallbacks,
                    modifier = Modifier.weight(9f)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AiConversationArea(
    callbacks: AiAssistantDialogCallbacks,
    onCloseClick: () -> Unit,
    onDiscardResult: () -> Unit,
    messages: List<MessageModel>,
    uiState: AiAssistantState,
    onSendUserInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var inputValue by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 16.dp,
                bottom = 16.dp,
                end = 8.dp
            )
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) {
                    ChatMessage(it)
                }
            }
            uiState.ActionContent(
                callbacks = callbacks,
                onCloseClick = onCloseClick,
                onDiscardResult = onDiscardResult,
            )

            if (uiState.isGenerating) {
                Text(
                    text = stringResource(Res.string.ai_generating_response),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = inputValue,
                onValueChange = {
                    inputValue = it
                },
                maxLines = 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onKeyEvent {
                        if (it.key == Key.Enter) {
                            onSendUserInput(inputValue)
                            inputValue = ""
                            true
                        } else {
                            false
                        }
                    }
            )
        }
    }
}

@Composable
private fun ChatMessage(
    messageModel: MessageModel,
    modifier: Modifier = Modifier,
) {
    val hhmmFormat = Format {
        hour(Padding.NONE)
        chars(":")
        minute(Padding.ZERO)
    }
    if (messageModel.messageOwner == MessageOwner.User) {
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    messageModel.createdAt.format(
                        hhmmFormat,
                        offset = TimeZone.currentSystemDefault()
                            .offsetAt(messageModel.createdAt)
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
                Column(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 0.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    ).weight(1f)
                ) {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    ).weight(1f)
                ) {
                    SelectionContainer {
                        Text(
                            text = messageModel.message,
                            color = if (messageModel.isFailed) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                Text(
                    messageModel.createdAt.format(
                        hhmmFormat,
                        offset = TimeZone.currentSystemDefault()
                            .offsetAt(messageModel.createdAt)
                    ),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AiWorkspaceArea(
    project: Project,
    uiState: AiAssistantState,
    callbacks: AiAssistantDialogCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                start = 8.dp,
                bottom = 16.dp,
                end = 16.dp
            )
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            when (uiState) {
                is AiAssistantState.Success.NewScreenCreated -> {
                    var deviceSizeDp by remember { mutableStateOf(IntSize.Zero) }
                    val density = LocalDensity.current
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.Transparent),
                    ) {
                        ProvideDeviceSizeDp(deviceSizeDp) {
                            AppTheme {
                                Surface(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                8.dp
                                            )
                                        )
                                        .align(Alignment.CenterHorizontally)
                                        .onGloballyPositioned {
                                            deviceSizeDp = it.size / density.density.toInt()
                                        },
                                ) {
                                    // To catch the RuntimeException when rendering the generated screen.
                                    // There is a little chance that a RuntimeException such as nested
                                    // scrollable layouts are generated even though it's prohibited in the
                                    // prompts
                                    runCatching {
                                        uiState.screen.contentRootNode().RenderedNodeInCanvas(
                                            project = project,
                                            canvasNodeCallbacks = emptyCanvasNodeCallbacks,
                                            paletteRenderParams = PaletteRenderParams(isThumbnail = true),
                                            zoomableContainerStateHolder = ZoomableContainerStateHolder(),
                                            modifier = Modifier
                                                .onClick(enabled = false, onClick = {})
                                                .align(Alignment.CenterHorizontally)
                                                .size(width = 416.dp, height = 886.dp),
                                        )
                                    }.onFailure {
                                        Text(
                                            text = "Failed to render screen prompt. Try generating the screen prompt again. Error: $it. ",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is AiAssistantState.Success.ScreenPromptsCreated -> {
                    ScreenPromptsCreatedContent(
                        callbacks = callbacks,
                        uiState = uiState
                    )
                }

                is AiAssistantState.Error -> {

                }

                AiAssistantState.Idle -> {}
            }
        }
    }
}