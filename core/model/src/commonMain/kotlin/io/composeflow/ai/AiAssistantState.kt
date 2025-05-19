package io.composeflow.ai

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
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

sealed class AiAssistantState {

    data object Idle : AiAssistantState()

    var isGenerating: Boolean = false

    @Composable
    open fun ActionContent(
        callbacks: AiAssistantDialogCallbacks,
        onCloseClick: () -> Unit,
        onDiscardResult: () -> Unit,
    ) {
    }

    sealed class Success : AiAssistantState() {

        data class NewScreenCreated(
            val screen: Screen,
        ) : Success() {

            @Composable
            override fun ActionContent(
                callbacks: AiAssistantDialogCallbacks,
                onCloseClick: () -> Unit,
                onDiscardResult: () -> Unit,
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            callbacks.onAddNewScreen(screen)
                            onDiscardResult()
                            onCloseClick()
                        }
                    ) {
                        Text(
                            stringResource(Res.string.ai_action_add_screen)
                        )
                    }

                    Spacer(Modifier.size(16.dp))
                    OutlinedButton(onClick = {
                        onDiscardResult()
                    }) {
                        Text(
                            stringResource(Res.string.ai_action_discard_result)
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

                Row(modifier = Modifier.padding(16.dp)) {
                    if (screenPrompts.all { it is GeneratedScreenPrompt.BeforeGeneration }) {
                        Button(
                            onClick = {
                                callbacks.onProceedToGenerateScreens()
                            }
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_proceed_to_generate_screens)
                            )
                        }
                    } else if (screenPrompts.all {
                            it is GeneratedScreenPrompt.ScreenGenerated || (it is GeneratedScreenPrompt.Error && !it.withRetry)
                        }) {
                        Button(
                            onClick = {
                                callbacks.onConfirmProjectWithScreens(
                                    projectName,
                                    packageName,
                                    screenPrompts.mapNotNull {
                                        (it as? GeneratedScreenPrompt.ScreenGenerated)?.screen
                                    }
                                )
                            }
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_create_project)
                            )
                        }
                    } else if (screenPrompts.any { it is GeneratedScreenPrompt.ScreenGenerated }) {
                        Button(
                            onClick = {
                                openCreateProjectDialog = true
                            }
                        ) {
                            Text(
                                stringResource(Res.string.ai_action_create_project)
                            )
                        }
                    }

                    Spacer(Modifier.size(16.dp))
                    OutlinedButton(onClick = {
                        onDiscardResult()
                    }) {
                        Text(
                            stringResource(Res.string.ai_action_discard_result)
                        )
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
                                }
                            )
                        },
                        onCloseClick = {
                            openCreateProjectDialog = false
                        }
                    )
                }
            }
        }

    }

    data class Error(val message: String) : AiAssistantState()
}