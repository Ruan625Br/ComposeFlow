package io.composeflow.ui.string

import co.touchlab.kermit.Logger
import io.composeflow.ksp.LlmParam
import io.composeflow.ksp.LlmTool
import io.composeflow.model.project.Project
import io.composeflow.model.project.string.ResourceLocale
import io.composeflow.model.project.string.StringResource
import io.composeflow.serializer.decodeFromStringWithFallback
import io.composeflow.serializer.encodeToString
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

    fun updateSupportedLocales(
        project: Project,
        newLocales: List<ResourceLocale>,
    ): EventResult {
        val result = EventResult()
        try {
            val defaultLocale = project.stringResourceHolder.defaultLocale.value
            // Ensure default locale is always included
            val localesWithDefault =
                if (newLocales.contains(defaultLocale)) {
                    newLocales
                } else {
                    listOf(defaultLocale) + newLocales
                }

            // Find locales to remove
            val currentLocales = project.stringResourceHolder.supportedLocales.toSet()
            val newLocalesSet = localesWithDefault.toSet()
            val localesToRemove = currentLocales - newLocalesSet

            // Remove old locales from string resources
            if (localesToRemove.isNotEmpty()) {
                project.stringResourceHolder.stringResources.forEach { resource ->
                    localesToRemove.forEach { locale ->
                        resource.localizedValues.remove(locale)
                    }
                }
            }

            // Replace the supported locales list
            project.stringResourceHolder.supportedLocales.clear()
            project.stringResourceHolder.supportedLocales.addAll(localesWithDefault)
        } catch (e: Exception) {
            Logger.e(e) { "Error replacing supported locales" }
            result.errorMessages.add("Failed to update supported locales: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_supported_locales",
        description = "Updates the entire list of supported locales with a new list. The default locale is always preserved.",
    )
    fun onUpdateSupportedLocales(
        project: Project,
        @LlmParam(description = "YAML representation of the new list of supported locales.")
        localesYaml: String,
    ): EventResult =
        try {
            val locales = decodeFromStringWithFallback<List<ResourceLocale>>(localesYaml)
            updateSupportedLocales(project, locales)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing locales YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse locales YAML: ${e.message}")
            }
        }

    fun setDefaultLocale(
        project: Project,
        locale: ResourceLocale,
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
        @LlmParam(description = "The locale code to set as default (e.g., 'en-US', 'fr-FR', 'es-ES').")
        localeCode: String,
    ): EventResult {
        val locale = ResourceLocale.fromString(localeCode)
        return if (locale == null) {
            EventResult().apply {
                errorMessages.add(
                    "Invalid locale code: $localeCode. Use one of the supported locales: ${
                        ResourceLocale.entries.joinToString(", ") { it.toString() }
                    }.",
                )
            }
        } else {
            setDefaultLocale(project, locale)
        }
    }

    @LlmTool(
        name = "list_string_resources",
        description = "Lists all string resources in the project. Returns a YAML representation of all resources.",
    )
    fun onListStringResources(project: Project): String =
        try {
            val resources = project.stringResourceHolder.stringResources
            encodeToString(resources)
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
                encodeToString(resource)
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
            encodeToString(locales)
        } catch (e: Exception) {
            Logger.e(e) { "Error getting supported locales" }
            "Error getting supported locales: ${e.message}"
        }
}
