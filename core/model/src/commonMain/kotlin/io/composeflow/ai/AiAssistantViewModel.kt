package io.composeflow.ai

import io.composeflow.Res
import io.composeflow.ai.subaction.GeneratedScreenPrompt
import io.composeflow.ai_failed_to_generate_response
import io.composeflow.ai_failed_to_generate_response_timeout
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.postProcessAfterAiGeneration
import io.composeflow.removeLineBreak
import io.composeflow.serializer.yamlSerializer
import io.composeflow.util.toKotlinFileName
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.datetime.Clock
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.getString

class AiAssistantViewModel(
    projectCreationQuery: String = "",
    private val repository: LlmRepository = LlmRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow<AiAssistantUiState>(AiAssistantUiState.Idle)
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages = _messages.asStateFlow()

    init {
        if (projectCreationQuery.isNotBlank()) {
            onSendProjectCreationQuery(projectCreationQuery)
        }
    }

    fun onSendProjectCreationQuery(projectCreationQuery: String) {
        viewModelScope.launch {
            _messages.value +=
                MessageModel(
                    messageOwner = MessageOwner.User,
                    message = projectCreationQuery,
                    createdAt = Clock.System.now(),
                )
            _uiState.value.isGenerating.value = true

            val result = repository.createProject(promptString = projectCreationQuery)

            _messages.value +=
                MessageModel(
                    messageOwner = MessageOwner.Ai,
                    message = result.message,
                    createdAt = Clock.System.now(),
                )
            _uiState.value.isGenerating.value = false

            _uiState.value =
                AiAssistantUiState.Success.ScreenPromptsCreated(
                    projectName = result.projectName,
                    packageName = result.packageName,
                    screenPrompts =
                        result.prompts.map {
                            GeneratedScreenPrompt.BeforeGeneration(
                                id = it.screenName.toKotlinFileName(),
                                screenName = it.screenName,
                                prompt = it.prompt,
                            )
                        },
                )
        }
    }

    fun onScreenTitleUpdated(
        id: String,
        newTitle: String,
    ) {
        when (val state = _uiState.value) {
            is AiAssistantUiState.Success.ScreenPromptsCreated -> {
                val updatedPrompts =
                    state.screenPrompts.map { prompt ->
                        when (prompt) {
                            is GeneratedScreenPrompt.BeforeGeneration -> {
                                if (prompt.screenName == id) {
                                    GeneratedScreenPrompt.BeforeGeneration(
                                        id = prompt.id,
                                        screenName = newTitle,
                                        prompt = prompt.prompt,
                                    )
                                } else {
                                    prompt
                                }
                            }

                            else -> {
                                prompt
                            }
                        }
                    }
                _uiState.value = state.copy(screenPrompts = updatedPrompts.toList())
            }

            else -> {}
        }
    }

    fun onScreenPromptDeleted(id: String) {
        when (val state = _uiState.value) {
            is AiAssistantUiState.Success.ScreenPromptsCreated -> {
                val updatedPrompts =
                    state.screenPrompts.filter { prompt -> prompt.id != id }
                _uiState.value = state.copy(screenPrompts = updatedPrompts.toList())
            }

            else -> {}
        }
    }

    fun onScreenPromptUpdated(
        id: String,
        newPrompt: String,
    ) {
        when (val state = _uiState.value) {
            is AiAssistantUiState.Success.ScreenPromptsCreated -> {
                val updatedPrompts =
                    state.screenPrompts.map { prompt ->
                        when (prompt) {
                            is GeneratedScreenPrompt.BeforeGeneration -> {
                                if (prompt.screenName == id) {
                                    GeneratedScreenPrompt.BeforeGeneration(
                                        id = prompt.id,
                                        screenName = prompt.screenName,
                                        prompt = newPrompt,
                                    )
                                } else {
                                    prompt
                                }
                            }

                            else -> {
                                prompt
                            }
                        }
                    }
                _uiState.value = state.copy(screenPrompts = updatedPrompts.toList())
            }

            else -> {}
        }
    }

    fun onProceedToGenerateScreens() {
        viewModelScope.launch {
            when (val state = _uiState.value) {
                is AiAssistantUiState.Success.ScreenPromptsCreated -> {
                    val originalPrompts = state.screenPrompts
                    val updatedPrompts = originalPrompts.toMutableList()

                    val semaphore = Semaphore(permits = maxConcurrentLlmCalls)

                    val jobs =
                        originalPrompts.mapIndexed { index, prompt ->
                            async {
                                if (prompt is GeneratedScreenPrompt.BeforeGeneration) {
                                    semaphore.withPermit {
                                        // Mark as Generating
                                        updatedPrompts[index] =
                                            GeneratedScreenPrompt.Generating(
                                                id = prompt.id,
                                                screenName = prompt.screenName,
                                                prompt = prompt.prompt,
                                            )
                                        _uiState.value =
                                            state.copy(screenPrompts = updatedPrompts.toList())
                                        _uiState.value.isGenerating.value = true

                                        var result: CreateScreenResponse =
                                            CreateScreenResponse.BeforeRequest(prompt.id)
                                        var promptString = prompt.prompt
                                        var retryCount = 0
                                        do {
                                            try {
                                                result =
                                                    repository.createScreen(
                                                        promptString = promptString,
                                                        retryCount = retryCount,
                                                        requestId = prompt.id,
                                                        projectContext =
                                                            ProjectContext(
                                                                screenContexts =
                                                                    state.screenPrompts.map {
                                                                        ScreenContext(
                                                                            id = it.id,
                                                                            screenName = it.screenName,
                                                                        )
                                                                    },
                                                            ),
                                                    )
                                                if (result is CreateScreenResponse.Error) {
                                                    val indexOfScreen =
                                                        updatedPrompts.indexOfFirst { it.id == result.requestId }
                                                    updatedPrompts[indexOfScreen] =
                                                        GeneratedScreenPrompt.Error(
                                                            id = prompt.id,
                                                            screenName = prompt.screenName,
                                                            prompt = prompt.prompt,
                                                            errorMessage = result.errorMessage,
                                                        )
                                                    retryCount++
                                                    promptString =
                                                        "${result.errorMessage}. Fix the yaml parse error. Previously generated Yaml: ${result.errorContent}"
                                                }
                                            } catch (e: Exception) {
                                                val indexOfScreen =
                                                    updatedPrompts.indexOfFirst { it.id == result.requestId }
                                                updatedPrompts[indexOfScreen] =
                                                    GeneratedScreenPrompt.Error(
                                                        id = prompt.id,
                                                        screenName = prompt.screenName,
                                                        prompt = prompt.prompt,
                                                        withRetry = false,
                                                        errorMessage = e.message ?: "Unknown error",
                                                    )
                                            }
                                        } while (result is CreateScreenResponse.Error && retryCount < maxRetryCount)

                                        when (result) {
                                            is CreateScreenResponse.Success -> {
                                                val indexOfScreen =
                                                    updatedPrompts.indexOfFirst { it.id == result.requestId }
                                                updatedPrompts[indexOfScreen] =
                                                    GeneratedScreenPrompt.ScreenGenerated(
                                                        id = prompt.id,
                                                        screen =
                                                            result.screen.postProcessAfterAiGeneration(
                                                                newId = prompt.id,
                                                            ),
                                                        prompt = prompt.prompt,
                                                        screenName = prompt.screenName,
                                                    )
                                            }

                                            else -> {
                                                // We care only success state here
                                            }
                                        }
                                        _uiState.value =
                                            state.copy(screenPrompts = updatedPrompts.toList())
                                    }
                                }
                            }
                        }

                    jobs.forEach { it.await() }
                    _uiState.value.isGenerating.value = false
                }

                else -> Unit
            }
        }
    }

    fun onRenderedErrorDetected(errorPrompt: GeneratedScreenPrompt.Error) {
        when (val state = _uiState.value) {
            is AiAssistantUiState.Success.ScreenPromptsCreated -> {
                val originalPrompts = state.screenPrompts
                val updatedPrompts = originalPrompts.toMutableList()
                val indexOfScreen =
                    updatedPrompts.indexOfFirst { it.id == errorPrompt.id }
                if (indexOfScreen != -1) {
                    updatedPrompts[indexOfScreen] = errorPrompt
                }
                _uiState.value = state.copy(screenPrompts = updatedPrompts.toList())
            }

            else -> {
                // We only care the ScreenPromptsCreated case. Intentionally using else block
            }
        }
    }

    fun onSendCreateScreenRequest(userInput: String) {
        viewModelScope.launch {
            // If already generated content exists, include it in the prompt.
            val existingContext =
                when (val state = uiState.value) {
                    is AiAssistantUiState.Success.NewScreenCreated -> {
                        yamlSerializer.encodeToString(Screen.serializer(), state.screen) + " "
                    }

                    else -> ""
                }

            _uiState.value.isGenerating.value = true

            _messages.value +=
                MessageModel(
                    messageOwner = MessageOwner.User,
                    message = userInput,
                    createdAt = Clock.System.now(),
                )
            try {
                var result: CreateScreenResponse
                var prompt = existingContext + userInput.removeLineBreak()
                var retryCount = 0
                do {
                    result =
                        repository.createScreen(
                            promptString = prompt,
                            retryCount = retryCount,
                        )
                    if (result is CreateScreenResponse.Error) {
                        _messages.value +=
                            MessageModel(
                                messageOwner = MessageOwner.Ai,
                                message = result.errorMessage,
                                isFailed = true,
                                createdAt = Clock.System.now(),
                            )
                        retryCount++
                        prompt =
                            "${result.errorMessage}. Fix the yaml parse error. Previously generated Yaml: ${result.errorContent}"
                    }
                } while (result is CreateScreenResponse.Error && retryCount < maxRetryCount)

                when (result) {
                    is CreateScreenResponse.Success -> {
                        _messages.value +=
                            MessageModel(
                                messageOwner = MessageOwner.Ai,
                                message = result.message,
                                createdAt = Clock.System.now(),
                            )
                        _uiState.value = AiAssistantUiState.Success.NewScreenCreated(result.screen)
                    }

                    else -> {
                        // We care only the success state here
                    }
                }
                _uiState.value.isGenerating.value = false
            } catch (timeoutException: TimeoutCancellationException) {
                val message = getString(Res.string.ai_failed_to_generate_response_timeout)
                _uiState.value =
                    AiAssistantUiState.Error(message = message)
                _messages.value +=
                    MessageModel(
                        messageOwner = MessageOwner.Ai,
                        message = message,
                        isFailed = true,
                        createdAt = Clock.System.now(),
                    )
            } catch (e: Exception) {
                val message = getString(Res.string.ai_failed_to_generate_response)
                _uiState.value =
                    AiAssistantUiState.Error(message = message)
                _messages.value +=
                    MessageModel(
                        messageOwner = MessageOwner.Ai,
                        message = message,
                        isFailed = true,
                        createdAt = Clock.System.now(),
                    )
            }
        }
    }

    fun onDiscardResult() {
        _uiState.value = AiAssistantUiState.Idle
        _uiState.value.isGenerating.value = false
    }
}
