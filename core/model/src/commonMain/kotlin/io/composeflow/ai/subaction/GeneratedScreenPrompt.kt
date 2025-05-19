package io.composeflow.ai.subaction

import io.composeflow.model.project.appscreen.screen.Screen
import kotlin.uuid.Uuid

sealed interface GeneratedScreenPrompt {

    val id: String
    val screenName: String

    data class BeforeGeneration(
        override val id: String = Uuid.random().toString(),
        override val screenName: String,
        val prompt: String,
    ) : GeneratedScreenPrompt

    data class Generating(
        override val id: String,
        override val screenName: String,
        val prompt: String,
    ) : GeneratedScreenPrompt

    data class ScreenGenerated(
        override val id: String,
        override val screenName: String,
        val screen: Screen,
        val prompt: String,
    ) : GeneratedScreenPrompt

    data class Error(
        override val id: String,
        override val screenName: String,
        val prompt: String,
        val errorMessage: String,
        val withRetry: Boolean = true,
    ) : GeneratedScreenPrompt
}
