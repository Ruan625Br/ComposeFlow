package io.composeflow.ai

import co.touchlab.kermit.Logger
import com.github.michaelbull.result.mapBoth
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.createCopyOfNewName
import io.composeflow.serializer.yamlSerializer
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.decodeFromString
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class LlmRepository(
    private val client: LlmClient = LlmClient()
) {
    suspend fun createProject(
        promptString: String,
        retryCount: Int = 0,
    ): CreateProjectResult {
        return withTimeout(5.minutes) {
            if (retryCount >= 4) {
                Logger.e("Failed to generate screen. Tried maximum number of attempts.")
                throw IllegalStateException("Failed to generate screen. Tried maximum number of attempts.")
            }

            Logger.i("$promptString,  Retry count: $retryCount")
            val response = client.invokeCreateProject(
                promptString
            )
            response.mapBoth(
                success = {
                    CreateProjectResult(
                        message = it.message,
                        projectName = it.createProject?.projectName ?: "projectName",
                        packageName = it.createProject?.packageName ?: "com.example",
                        prompts = it.createProject?.screenPrompts ?: emptyList()
                    )
                },
                failure = {
                    it.message?.let { message ->
                        Logger.e(message)
                    }
                    throw it
                }
            )
        }
    }

    suspend fun createScreen(
        promptString: String,
        retryCount: Int = 0,
        requestId: String? = null,
        screenName: String? = null,
        projectContext: ProjectContext? = null,
    ): CreateScreenResponse {
        return withTimeout(5.minutes) {
            if (retryCount >= maxRetryCount) {
                Logger.e("Failed to generate screen. Tried maximum number of attempts.")
                throw IllegalStateException("Failed to generate screen. Tried maximum number of attempts.")
            }
            Logger.i("$promptString,  Retry count: $retryCount")
            val response = client.invokeGenerateScreen(
                promptString = promptString,
                projectContextString = projectContext?.toContextString(),
            )
            response.mapBoth(
                success = {
                    when (val responseDetail = it.responseDetail) {
                        is CreateNewScreen -> {
                            var screen: Screen? = null
                            var result: CreateScreenResponse
                            try {
                                screen =
                                    yamlSerializer.decodeFromString<Screen>(
                                        responseDetail.yamlContent
                                    )
                                if (screenName != null) {
                                    screen = screen.createCopyOfNewName(screenName)
                                }
                                result = CreateScreenResponse.Success(
                                    screen = screen,
                                    message = it.message,
                                    requestId = requestId,
                                )
                            } catch (e: Exception) {
                                Logger.w("Failed to parse the yaml. $e")
                                result = CreateScreenResponse.Error(
                                    originalPrompt = promptString,
                                    retryCount = retryCount,
                                    requestId = requestId,
                                    errorMessage = e.message ?: "Unknown error",
                                    errorContent = responseDetail.yamlContent,
                                    throwable = e,
                                )
                            }
                            result
                        }

                        null -> {
                            throw IllegalStateException("Failed to generate the screen.")
                        }
                    }
                },
                failure = {
                    it.message?.let { message ->
                        Logger.e(message)
                    }
                    throw it
                }
            )
        }
    }
}

data class CreateProjectResult(
    val message: String,
    val projectName: String,
    val packageName: String,
    val prompts: List<ScreenPrompt>,
)

data class ProjectContext(
    val screenContexts: List<ScreenContext>
) {
    fun toContextString(): String {
        return "Project context: { screens: [${screenContexts.joinToString(separator = ", ") { it.toContextString() }}] }"
    }
}

data class ScreenContext(
    val id: String,
    val screenName: String,
) {
    fun toContextString() = "Screen(ID: $id, screenName:$screenName)"
}