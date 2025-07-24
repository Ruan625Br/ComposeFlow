package io.composeflow.ui.string

import co.touchlab.kermit.Logger
import io.composeflow.ksp.LlmParam
import io.composeflow.ksp.LlmTool
import io.composeflow.model.project.Project
import io.composeflow.model.project.string.StringResource
import io.composeflow.serializer.decodeFromStringWithFallback
import io.composeflow.serializer.yamlDefaultSerializer
import io.composeflow.ui.EventResult
import io.composeflow.util.generateUniqueName
import kotlinx.serialization.encodeToString

// TODO: Wire this class with ToolDispatcher to enable AI to edit string resources.
//       https://github.com/ComposeFlow/ComposeFlow/issues/34

/**
 * Handles operations related to string resource editor, such as adding, updating, or removing string resources.
 * Operations in this class are exposed to the LLM to allow them to call it as tools as well as used
 * from the GUI in ComposeFlow.
 */
class StringResourceEditorOperator {
    fun addStringResource(
        project: Project,
        stringResource: StringResource,
    ): EventResult {
        val result = EventResult()
        try {
            val newKey =
                generateUniqueName(
                    stringResource.key,
                    project.stringResourceHolder.stringResources
                        .map { it.key }
                        .toSet(),
                )
            val newResource = stringResource.copy(key = newKey)
            project.stringResourceHolder.stringResources.add(newResource)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding string resource" }
            result.errorMessages.add("Failed to add string resource: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_string_resource",
        "Adds a new string resource to the project. String resources are used for internationalization and localization of text in the application.",
    )
    fun onAddStringResource(
        project: Project,
        @LlmParam(description = "The YAML representation of the StringResource to be added. The key will be made unique if necessary.")
        stringResourceYaml: String,
    ): EventResult =
        try {
            val stringResource = decodeFromStringWithFallback<StringResource>(stringResourceYaml)
            addStringResource(project, stringResource)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing string resource YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse string resource YAML: ${e.message}")
            }
        }

    fun deleteStringResource(
        project: Project,
        stringResourceId: String,
    ): EventResult {
        val result = EventResult()
        try {
            val resource = project.stringResourceHolder.stringResources.find { it.id == stringResourceId }
            if (resource == null) {
                result.errorMessages.add("String resource with ID $stringResourceId not found.")
                return result
            }
            project.stringResourceHolder.stringResources.remove(resource)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting string resource" }
            result.errorMessages.add("Failed to delete string resource: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "delete_string_resource",
        description = "Removes a string resource from the project by its ID.",
    )
    fun onDeleteStringResource(
        project: Project,
        @LlmParam(description = "The ID of the string resource to be deleted.")
        stringResourceId: String,
    ): EventResult = deleteStringResource(project, stringResourceId)

    fun updateStringResource(
        project: Project,
        stringResource: StringResource,
    ): EventResult {
        val result = EventResult()
        try {
            val existingResourceIndex =
                project.stringResourceHolder.stringResources.indexOfFirst { it.id == stringResource.id }
            if (existingResourceIndex == -1) {
                result.errorMessages.add("String resource with ID ${stringResource.id} not found.")
                return result
            }

            project.stringResourceHolder.stringResources[existingResourceIndex] = stringResource
        } catch (e: Exception) {
            Logger.e(e) { "Error updating string resource" }
            result.errorMessages.add("Failed to update string resource: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_string_resource",
        description = "Updates an existing string resource in the project.",
    )
    fun onUpdateStringResource(
        project: Project,
        @LlmParam(description = "The YAML representation of the updated StringResource. Must include the ID of the resource to update.")
        stringResourceYaml: String,
    ): EventResult =
        try {
            val stringResource = decodeFromStringWithFallback<StringResource>(stringResourceYaml)
            updateStringResource(project, stringResource)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing string resource YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse string resource YAML: ${e.message}")
            }
        }

    fun addLocale(
        project: Project,
        locale: StringResource.Locale,
    ): EventResult {
        val result = EventResult()
        try {
            if (project.stringResourceHolder.supportedLocales.contains(locale)) {
                result.errorMessages.add("Locale $locale already exists.")
                return result
            }
            project.stringResourceHolder.supportedLocales.add(locale)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding locale" }
            result.errorMessages.add("Failed to add locale: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_locale",
        description = "Adds a new supported locale to the project for string localization.",
    )
    fun onAddLocale(
        project: Project,
        @LlmParam(description = "The language code (e.g., 'en', 'fr', 'es').")
        language: String,
        @LlmParam(description = "The optional region code (e.g., 'US', 'GB', 'MX'). Can be null.")
        region: String?,
    ): EventResult = addLocale(project, StringResource.Locale(language, region))

    fun removeLocale(
        project: Project,
        locale: StringResource.Locale,
    ): EventResult {
        val result = EventResult()
        try {
            if (locale == project.stringResourceHolder.defaultLocale.value) {
                result.errorMessages.add("Cannot remove the default locale.")
                return result
            }
            project.stringResourceHolder.supportedLocales.remove(locale)
            // Remove this locale from all string resources
            project.stringResourceHolder.stringResources.forEach { resource ->
                resource.localizedValues.remove(locale)
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error removing locale" }
            result.errorMessages.add("Failed to remove locale: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "remove_locale",
        description = "Removes a supported locale from the project.",
    )
    fun onRemoveLocale(
        project: Project,
        @LlmParam(description = "The language code of the locale to remove.")
        language: String,
        @LlmParam(description = "The optional region code of the locale to remove. Can be null.")
        region: String?,
    ): EventResult = removeLocale(project, StringResource.Locale(language, region))

    fun setDefaultLocale(
        project: Project,
        locale: StringResource.Locale,
    ): EventResult {
        val result = EventResult()
        try {
            if (!project.stringResourceHolder.supportedLocales.contains(locale)) {
                project.stringResourceHolder.supportedLocales.add(locale)
            }
            project.stringResourceHolder.defaultLocale.value = locale
        } catch (e: Exception) {
            Logger.e(e) { "Error setting default locale" }
            result.errorMessages.add("Failed to set default locale: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "set_default_locale",
        description = "Sets the default locale for the project.",
    )
    fun onSetDefaultLocale(
        project: Project,
        @LlmParam(description = "The language code of the default locale.")
        language: String,
        @LlmParam(description = "The optional region code of the default locale. Can be null.")
        region: String?,
    ): EventResult = setDefaultLocale(project, StringResource.Locale(language, region))

    @LlmTool(
        name = "list_string_resources",
        description = "Lists all string resources in the project. Returns a YAML representation of all resources.",
    )
    fun onListStringResources(project: Project): String =
        try {
            val resources = project.stringResourceHolder.stringResources
            yamlDefaultSerializer.encodeToString(resources)
        } catch (e: Exception) {
            Logger.e(e) { "Error listing string resources" }
            "Error listing string resources: ${e.message}"
        }

    @LlmTool(
        name = "get_string_resource",
        description = "Gets a specific string resource by its ID. Returns a YAML representation of the resource.",
    )
    fun onGetStringResource(
        project: Project,
        @LlmParam(description = "The ID of the string resource to retrieve.")
        stringResourceId: String,
    ): String =
        try {
            val resource = project.stringResourceHolder.stringResources.find { it.id == stringResourceId }
            if (resource != null) {
                yamlDefaultSerializer.encodeToString(resource)
            } else {
                "String resource with ID $stringResourceId not found."
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error getting string resource" }
            "Error getting string resource: ${e.message}"
        }

    @LlmTool(
        name = "get_supported_locales",
        description = "Gets all supported locales in the project. Returns a YAML representation of the locales.",
    )
    fun onGetSupportedLocales(project: Project): String =
        try {
            val locales = project.stringResourceHolder.supportedLocales
            yamlDefaultSerializer.encodeToString(locales)
        } catch (e: Exception) {
            Logger.e(e) { "Error getting supported locales" }
            "Error getting supported locales: ${e.message}"
        }
}
