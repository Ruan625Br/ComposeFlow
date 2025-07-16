package io.composeflow.ai

import io.composeflow.ai.subaction.GeneratedScreenPrompt
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen

data class AiAssistantDialogCallbacks(
    val onAddNewScreen: (Screen) -> Unit = {},
    val onScreenTitleUpdated: (id: String, newName: String) -> Unit = { _, _ -> },
    val onScreenPromptUpdated: (id: String, prompt: String) -> Unit = { _, _ -> },
    val onScreenPromptDeleted: (id: String) -> Unit = {},
    val onProceedToGenerateScreens: () -> Unit = {},
    val onRenderedErrorDetected: (GeneratedScreenPrompt.Error) -> Unit = { _ -> },
    val onConfirmProjectWithScreens: (project: Project, screens: List<Screen>) -> Unit = { _, _ -> },
)
