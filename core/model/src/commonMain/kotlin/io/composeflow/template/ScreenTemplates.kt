package io.composeflow.template

import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.serializer.yamlSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import org.jetbrains.annotations.VisibleForTesting

object ScreenTemplates {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            // initialize loading the templates so that deserialization doesn't happen on the
            // next load
            initialTemplates()
        }
    }

    @get:VisibleForTesting
    val blankScreen by lazy {
        val resourcePath = "/blank_screen_template.yaml"
        ScreenTemplatePair(
            resourcePath = resourcePath,
            screen = loadScreenTemplate(resourcePath)
        )
    }

    @get:VisibleForTesting
    val messagesScreen by lazy {
        val resourcePath = "/messages_screen_template.yaml"
        ScreenTemplatePair(
            resourcePath = resourcePath,
            screen = loadScreenTemplate(resourcePath)
        )
    }

    @get:VisibleForTesting
    val loginScreen by lazy {
        val resourcePath = "/login_screen_template.yaml"
        ScreenTemplatePair(
            resourcePath = resourcePath,
            screen = loadScreenTemplate(resourcePath)
        )
    }

    @get:VisibleForTesting
    val settingsScreen by lazy {
        val resourcePath = "/settings_screen_template.yaml"
        ScreenTemplatePair(
            resourcePath = resourcePath,
            screen = loadScreenTemplate(resourcePath)
        )
    }

    /**
     * Initial set of templates. Use [createNewScreen] when creating a new screen.
     */
    fun initialTemplates(): List<ScreenTemplatePair> {
        return listOf(
            blankScreen, messagesScreen, loginScreen, settingsScreen
        )
    }

    // When creating new Screen from a template, replace Uuids with random ones while keeping the
    // relationships to keep the referenced statuses.
    fun createNewScreen(screenTemplatePair: ScreenTemplatePair): Screen =
        loadScreenTemplate(screenTemplatePair.resourcePath)

    private fun loadScreenTemplate(fileName: String): Screen {
        val yaml = object {}.javaClass.getResourceAsStream(fileName)
        check(yaml != null)
        val newYaml = replaceUuids(yaml.reader().readText())
        return yamlSerializer.decodeFromString<Screen>(newYaml)
    }
}
