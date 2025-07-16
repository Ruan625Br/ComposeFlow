package io.composeflow.model.project.appscreen.screen

import com.charleskorn.kaml.YamlNode
import io.composeflow.serializer.yamlDefaultSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import prefixIdInYamlNode

fun Screen.postProcessAfterAiGeneration(newId: String): Screen {
    if (label.value.contains("screen", ignoreCase = true)) {
        label.value = label.value.replace("screen", "", ignoreCase = true)
    }
    val replaced = replaceIdsToIncreaseUniqueness()
    // Prefix the id properties with the screen id to reduce the chance of duplicated IDs across
    // the screens, except for the Screen.id as it's used for navigation destination from
    // other screens
    return replaced.copy(id = newId)
}

fun Screen.replaceIdsToIncreaseUniqueness(): Screen {
    val yaml = yamlDefaultSerializer.encodeToString(this)
    val prefixedYaml =
        prefixYamlIdsWithKaml(
            yamlString = yaml,
            screenId = id,
        )
    return prefixedYaml?.let {
        yamlDefaultSerializer.decodeFromString<Screen>(prefixedYaml)
    } ?: yamlDefaultSerializer.decodeFromString<Screen>(yaml)
}

/**
 * Parses a YAML string using kaml into a YamlNode, prefixes specified ID fields
 * ('id' or keys ending in 'Id') with the given screenId if not already prefixed,
 * and returns the modified YAML string.
 *
 * @param yamlString The input YAML content as a string.
 * @param screenId The identifier for the screen (e.g., "messagesRoot") used to create the prefix.
 * @return The modified YAML string, or null if an error occurs.
 */
private fun prefixYamlIdsWithKaml(
    yamlString: String,
    screenId: String,
): String? {
    val idPrefix = "$screenId:" // Define the prefix format

    return try {
        // Step 1: Parse the YAML string into kaml's intermediate YamlNode structure
        val rootNode: YamlNode = yamlDefaultSerializer.parseToYamlNode(yamlString)

        // Step 2: Recursively process the YamlNode structure to modify IDs
        val modifiedNode: YamlNode = prefixIdInYamlNode(rootNode, screenId, idPrefix)

        // Step 3: Encode the modified YamlNode structure back to a YAML string
        // We need the YamlNode.serializer() provided by kaml
        val modifiedScreen =
            yamlDefaultSerializer.decodeFromYamlNode(Screen.serializer(), modifiedNode)
        yamlDefaultSerializer.encodeToString(modifiedScreen)
    } catch (e: Exception) {
        // Catch potential parsing or processing errors
        e.printStackTrace()
        null // Return null on error
    }
}
