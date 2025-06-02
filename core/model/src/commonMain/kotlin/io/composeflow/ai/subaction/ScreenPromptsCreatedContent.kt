package io.composeflow.ai.subaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.composeflow.ai.AiAssistantDialogCallbacks
import io.composeflow.ai.AiAssistantUiState
import io.composeflow.model.palette.PaletteRenderParams
import io.composeflow.model.project.Project
import io.composeflow.ui.adaptive.ProvideDeviceSizeDp
import io.composeflow.ui.common.AppTheme
import io.composeflow.ui.common.ProvideAppThemeTokens
import io.composeflow.ui.emptyCanvasNodeCallbacks
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.placeholder.PlaceholderContainer
import io.composeflow.ui.text.EditableText
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder

@Composable
fun ScreenPromptsCreatedContent(
    callbacks: AiAssistantDialogCallbacks,
    uiState: AiAssistantUiState.Success.ScreenPromptsCreated,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(400.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(uiState.screenPrompts) { screenPrompt ->
                ScreenPromptItem(screenPrompt, callbacks)
            }
        }
    }
}

@Composable
private fun ScreenPromptItem(
    screenPrompt: GeneratedScreenPrompt,
    callbacks: AiAssistantDialogCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (screenPrompt) {
            is GeneratedScreenPrompt.BeforeGeneration -> {
                ScreenPromptItemBeforeGeneration(
                    id = screenPrompt.id,
                    screenName = screenPrompt.screenName,
                    prompt = screenPrompt.prompt,
                    callbacks = callbacks,
                )
            }

            is GeneratedScreenPrompt.Error -> {
                ScreenPromptItemBeforeGeneration(
                    id = screenPrompt.id,
                    screenName = screenPrompt.screenName,
                    prompt = screenPrompt.prompt,
                    callbacks = callbacks,
                    isError = true,
                    isGenerating = screenPrompt.withRetry,
                )
            }

            is GeneratedScreenPrompt.Generating -> {
                ScreenPromptItemBeforeGeneration(
                    id = screenPrompt.id,
                    screenName = screenPrompt.screenName,
                    prompt = screenPrompt.prompt,
                    callbacks = callbacks,
                    isGenerating = true,
                )
            }

            is GeneratedScreenPrompt.ScreenGenerated -> {
                ScreenPromptItemScreenGenerated(
                    screenPrompt = screenPrompt,
                    callbacks = callbacks,
                )
            }
        }
    }
}

@Composable
private fun ScreenPromptItemBeforeGeneration(
    id: String,
    screenName: String,
    prompt: String,
    callbacks: AiAssistantDialogCallbacks,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    isGenerating: Boolean = false,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                shape = MaterialTheme.shapes.small
            )
            .padding(8.dp)
            .height(640.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            EditableText(
                initialText = screenName,
                onValueChange = {
                    callbacks.onScreenPromptUpdated(id, it)
                }
            )
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.weight(1f))
            ComposeFlowIconButton(onClick = {
                callbacks.onScreenPromptDeleted(id)
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "delete the screen prompt",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }

        Spacer(Modifier.size(16.dp))
        Box {
            if (!isError) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = {
                        callbacks.onScreenPromptUpdated(id, it)
                    },
                    label = {
                        Text("Description")
                    },
                    enabled = !isGenerating,
                    modifier = Modifier.alpha(
                        if (isGenerating) 0.4f else 1f
                    )
                )
            } else {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (isGenerating) {
                PlaceholderContainer()
            }
        }
    }
}

@Composable
private fun ScreenPromptItemScreenGenerated(
    screenPrompt: GeneratedScreenPrompt.ScreenGenerated,
    callbacks: AiAssistantDialogCallbacks,
    modifier: Modifier = Modifier,
) {

    Column(modifier = modifier) {
        var deviceSizeDp by remember { mutableStateOf(IntSize.Zero) }
        val density = LocalDensity.current
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
                .background(Color.Transparent),
        ) {
            ProvideDeviceSizeDp(deviceSizeDp) {
                ProvideAppThemeTokens(
                    isDarkTheme = false,
                ) {
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
                            screenPrompt.screen.contentRootNode().RenderedNodeInCanvas(
                                // Workaround to pass a project
                                project = Project(),
                                canvasNodeCallbacks = emptyCanvasNodeCallbacks,
                                paletteRenderParams = PaletteRenderParams(isThumbnail = true),
                                zoomableContainerStateHolder = ZoomableContainerStateHolder(),
                                modifier = Modifier
                                    .onClick(enabled = false, onClick = {})
                                    .align(Alignment.CenterHorizontally)
                                    .size(width = 416.dp, height = 886.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}