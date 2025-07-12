package io.composeflow.model.project.string

import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.FallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Escapes special XML characters in text content.
 * Handles: &, <, >, ", '
 */
private fun String.escapeXml(): String =
    this
        .replace("&", "&amp;") // Must be first to avoid double-escaping
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

@Serializable
@SerialName("StringResourceHolder")
data class StringResourceHolder(
    @Serializable(with = FallbackMutableStateListSerializer::class)
    val stringResources: MutableList<StringResource> = mutableStateListEqualsOverrideOf(),
    var defaultLocale: StringResource.Locale = StringResource.Locale("en"),
    @Serializable(with = FallbackMutableStateListSerializer::class)
    val supportedLocales: MutableList<StringResource.Locale> = mutableStateListEqualsOverrideOf(defaultLocale),
) {
    /**
     * Generates strings.xml content for each supported locale.
     * Returns a map of locale to XML content.
     * For the default locale, the key will be empty string.
     */
    private fun generateStringsXmlContent(): Map<String, String> {
        if (stringResources.isEmpty()) return emptyMap()

        val result = mutableMapOf<String, String>()

        // Generate for each supported locale
        supportedLocales.forEach { locale ->
            if (stringResources.none { it.localizedValues.contains(locale) }) {
                return@forEach
            }

            val xmlContent =
                buildString {
                    appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                    appendLine("<resources>")
                    stringResources.forEach { resource ->
                        resource.localizedValues[locale]?.let { value ->
                            val escapedValue = value.escapeXml()
                            appendLine("    <string name=\"${resource.key}\">$escapedValue</string>")
                        }
                    }
                    appendLine("</resources>")
                }

            // Default locale goes to values/strings.xml (empty key)
            // Other locales go to values-{locale}/strings.xml
            val localeKey = if (locale == defaultLocale) "" else locale.toString()
            result[localeKey] = xmlContent
        }

        return result
    }

    /**
     * Generates a map of file paths to XML content for string resources.
     * Returns a map where:
     * - Key: destination path relative to project root
     * - Value: XML content to write
     */
    fun generateStringResourceFiles(): Map<String, String> {
        val xmlContents = generateStringsXmlContent()
        if (xmlContents.isEmpty()) return emptyMap()

        return xmlContents
            .map { (locale, content) ->
                val folder = if (locale.isEmpty()) "values" else "values-$locale"
                val path = "composeApp/src/commonMain/composeResources/$folder/strings.xml"
                path to content
            }.toMap()
    }
}

fun StringResourceHolder.copyContents(other: StringResourceHolder) {
    stringResources.clear()
    stringResources.addAll(other.stringResources)
    defaultLocale = other.defaultLocale
    supportedLocales.clear()
    supportedLocales.addAll(other.supportedLocales)
}
