package io.composeflow.ai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.ai.claude.ToolResponseWrapper
import io.composeflow.ai.subaction.GeneratedScreenPrompt
import io.composeflow.ai_action_add_screen
import io.composeflow.ai_action_confirm_to_create_project
import io.composeflow.ai_action_create_project
import io.composeflow.ai_action_discard_result
import io.composeflow.ai_action_proceed_to_generate_screens
import io.composeflow.create
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.ui.popup.SimpleConfirmationDialog
import org.jetbrains.compose.resources.stringResource

sealed class AiAssistantUiState {
    data object Idle : AiAssistantUiState()

    var isGenerating: MutableState<Boolean> = mutableStateOf(false)

    @Composable
    open fun ActionContent(
        callbacks: AiAssistantDialogCallbacks,
        onCloseClick: () -> Unit,
        onDiscardResult: () -> Unit,
    ) {
    }

    sealed class Success : AiAssistantUiState() {
        data class NewScreenCreated(
            val screen: Screen,
        ) : Success() {
            @Composable
            override fun ActionContent(
                callbacks: AiAssistantDialogCallbacks,
                onCloseClick: () -> Unit,
                onDiscardResult: () -> Unit,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val buttonModifier = Modifier.weight(1f).fillMaxHeight()
                    OutlinedButton(
                        onClick = {
                            onDiscardResult()
                        },
                        modifier = buttonModifier,
                    ) {
                        Text(
                            stringResource(Res.string.ai_action_discard_result),
                        )
                    }
                    Button(
                        onClick = {
                            callbacks.onAddNewScreen(screen)
                            onDiscardResult()
                            onCloseClick()
                        },
                        modifier = buttonModifier,
                    ) {
                        Text(
                            stringResource(Res.string.ai_action_add_screen),
                        )
                    }
                }
            }
        }

        data class ScreenPromptsCreated(
            val projectName: String,
            val packageName: String,
            val screenPrompts: List<GeneratedScreenPrompt>,
        ) : Success() {
            @Composable
            override fun ActionContent(
                callbacks: AiAssistantDialogCallbacks,
                onCloseClick: () -> Unit,
                onDiscardResult: () -> Unit,
            ) {
                var openCreateProjectDialog by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier.padding(16.dp).height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val buttonModifier = Modifier.weight(1f).fillMaxHeight()
                    OutlinedButton(
                        onClick = {
                            onDiscardResult()
                        },
                        modifier = buttonModifier,
                    ) {
                        Text(
                            stringResource(Res.string.ai_action_discard_result),
                        )
                    }

                    if (screenPrompts.all { it is GeneratedScreenPrompt.BeforeGeneration }) {
                        Button(
                            onClick = {
                                callbacks.onProceedToGenerateScreens()
                            },
                            modifier = buttonModifier,
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_proceed_to_generate_screens),
                            )
                        }
                    } else if (screenPrompts.all {
                            it is GeneratedScreenPrompt.ScreenGenerated || (it is GeneratedScreenPrompt.Error && !it.withRetry)
                        }
                    ) {
                        Button(
                            onClick = {
                                callbacks.onConfirmProjectWithScreens(
                                    projectName,
                                    packageName,
                                    screenPrompts.mapNotNull {
                                        (it as? GeneratedScreenPrompt.ScreenGenerated)?.screen
                                    },
                                )
                            },
                            modifier = buttonModifier,
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_create_project),
                            )
                        }
                    } else if (screenPrompts.any { it is GeneratedScreenPrompt.ScreenGenerated }) {
                        Button(
                            onClick = {
                                openCreateProjectDialog = true
                            },
                            modifier = buttonModifier,
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_create_project),
                            )
                        }
                    }
                }

                if (openCreateProjectDialog) {
                    SimpleConfirmationDialog(
                        text = stringResource(Res.string.ai_action_confirm_to_create_project),
                        positiveText = stringResource(Res.string.create),
                        positiveButtonColor = MaterialTheme.colorScheme.primary,
                        onConfirmClick = {
                            callbacks.onConfirmProjectWithScreens(
                                projectName,
                                packageName,
                                screenPrompts.mapNotNull {
                                    (it as? GeneratedScreenPrompt.ScreenGenerated)?.screen
                                },
                            )
                        },
                        onCloseClick = {
                            openCreateProjectDialog = false
                        },
                    )
                }
            }
        }

        data class ToolResponseProcessed(
            val result: ToolResponseWrapper,
        ) : Success()
    }

    data class Error(
        val message: String,
    ) : AiAssistantUiState()
}
