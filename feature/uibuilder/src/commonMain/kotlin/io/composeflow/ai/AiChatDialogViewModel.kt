package io.composeflow.ai

import co.touchlab.kermit.Logger
import io.composeflow.Res
import io.composeflow.ai.openrouter.tools.ToolArgs
import io.composeflow.ai.openrouter.tools.ToolExecutionStatus
import io.composeflow.ai_failed_to_generate_response
import io.composeflow.ai_failed_to_generate_response_timeout
import io.composeflow.ai_response_stopped_by_user
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.project.Project
import io.composeflow.model.useroperation.OperationHistory
import io.composeflow.model.useroperation.UserOperation
import io.composeflow.removeLineBreak
import io.composeflow.repository.ProjectRepository
import io.composeflow.serializer.yamlSerializer
import io.composeflow.ui.EventResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.getString

class AiChatDialogViewModel(
    private val project: Project,
    private val firebaseIdTokenArg: FirebaseIdToken,
    private val aiAssistantUiState: AiAssistantUiState,
    private val llmRepository: LlmRepository = LlmRepository(),
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdTokenArg),
    private val toolDispatcher: ToolDispatcher = ToolDispatcher(),
    private val onAiAssistantUiStateUpdated: (AiAssistantUiState) -> Unit,
) : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageModel>>(emptyList())
    val messages = _messages.asStateFlow()

    private var generationJob: Job? = null

    fun onSendGeneralRequest(userInput: String) {
        generationJob =
            viewModelScope.launch {
                aiAssistantUiState.isGenerating.value = true
                onAiAssistantUiStateUpdated(aiAssistantUiState)

                _messages.value +=
                    MessageModel(
                        messageOwner = MessageOwner.User,
                        message = userInput,
                        createdAt = Clock.System.now(),
                    )
                try {
                    var result: ToolResponse? = null
                    val prompt = userInput.removeLineBreak()
                    val previousToolArgs = mutableListOf<ToolArgs>()
                    while (isActive && (result as? ToolResponse.Success)?.response?.isConsideredComplete() != true) {
                        result =
                            llmRepository.handleToolRequest(
                                promptString = prompt,
                                projectContext =
                                    yamlSerializer.encodeToString(
                                        Project.serializer(),
                                        project,
                                    ),
                                previousToolArgs = previousToolArgs,
                            )

                        when (result) {
                            is ToolResponse.Error -> {
                                _messages.value +=
                                    MessageModel(
                                        messageOwner = MessageOwner.Ai,
                                        message = result.message,
                                        isFailed = true,
                                        createdAt = Clock.System.now(),
                                    )
                                previousToolArgs.add(
                                    ToolArgs.FakeArgs().apply {
                                        status = ToolExecutionStatus.Error
                                    },
                                )
                            }

                            is ToolResponse.Success -> {
                                Logger.i("Success tool result received:")
                                Logger.i(jsonSerializer.encodeToString(result.response))

                                _messages.value +=
                                    MessageModel(
                                        messageOwner = MessageOwner.Ai,
                                        message = result.message,
                                        createdAt = Clock.System.now(),
                                    )
                                result.response.tool_calls?.forEach {
                                    val toolEventResult = dispatchToolResponse(it.tool_args)
                                    if (toolEventResult.isSuccessful()) {
                                        it.tool_args.status = ToolExecutionStatus.Success
                                        previousToolArgs.add(it.tool_args)
                                        saveProject(project)
                                    } else {
                                        it.tool_args.status = ToolExecutionStatus.Error
                                        it.tool_args.result =
                                            toolEventResult.errorMessages.joinToString("\n")
                                        previousToolArgs.add(it.tool_args)

                                        _messages.value +=
                                            MessageModel(
                                                messageOwner = MessageOwner.Ai,
                                                message =
                                                    toolEventResult.errorMessages.joinToString(
                                                        "\n",
                                                    ),
                                                isFailed = true,
                                                createdAt = Clock.System.now(),
                                            )
                                    }
                                }
                            }
                        }
                    }

                    onAiAssistantUiStateUpdated(aiAssistantUiState)
                } catch (timeoutException: TimeoutCancellationException) {
                    val message =
                        getString(Res.string.ai_failed_to_generate_response_timeout)
                    onAiAssistantUiStateUpdated(AiAssistantUiState.Error(message = message))

                    _messages.value +=
                        MessageModel(
                            messageOwner = MessageOwner.Ai,
                            message = message,
                            isFailed = true,
                            createdAt = Clock.System.now(),
                        )
                } catch (e: Exception) {
                    val message =
                        getString(Res.string.ai_failed_to_generate_response)

                    onAiAssistantUiStateUpdated(AiAssistantUiState.Error(message = message))
                    _messages.value +=
                        MessageModel(
                            messageOwner = MessageOwner.Ai,
                            message = message,
                            isFailed = true,
                            createdAt = Clock.System.now(),
                        )
                } finally {
                    aiAssistantUiState.isGenerating.value = false
                }
            }
    }

    private fun dispatchToolResponse(toolArgs: ToolArgs): EventResult {
        if (toolArgs !is ToolArgs.FakeArgs) {
            recordOperation(
                project,
                UserOperation.ExecuteAiTool(
                    toolArgs = toolArgs,
                ),
            )
        }
        return toolDispatcher.dispatchToolResponse(project, toolArgs)
    }

    fun onStopGeneration() {
        generationJob?.cancel()
        aiAssistantUiState.isGenerating.value = false
        onAiAssistantUiStateUpdated(aiAssistantUiState)

        viewModelScope.launch {
            _messages.value +=
                MessageModel(
                    messageOwner = MessageOwner.Ai,
                    message = getString(Res.string.ai_response_stopped_by_user),
                    isFailed = true,
                    createdAt = Clock.System.now(),
                )
        }
    }

    private fun saveProject(project: Project) {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }

    private fun recordOperation(
        project: Project,
        userOperation: UserOperation,
    ) {
        viewModelScope.launch {
            OperationHistory.record(
                project = project,
                userOperation = userOperation,
            )
        }
    }
}
