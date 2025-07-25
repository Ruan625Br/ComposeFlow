package io.composeflow.model.project.string

import io.composeflow.override.mutableStateMapEqualsOverrideOf
import io.composeflow.override.toMutableStateMapEqualsOverride
import io.composeflow.serializer.FallbackMutableStateMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

typealias StringResourceId = String

@Serializable
@SerialName("StringResource")
data class StringResource(
    val id: StringResourceId = Uuid.random().toString(),
    val key: String,
    /** Optional description for the string resource. Can be used to provide context for translators. */
    val description: String? = null,
    @Serializable(with = FallbackMutableStateMapSerializer::class)
    val localizedValues: MutableMap<Locale, String> = mutableStateMapEqualsOverrideOf(),
) {
    // TODO: Replace Locale with an enum class to provide a list of languages and regions in the UI
    //       and to detect when LLM uses a non-existent locale.
    //       https://github.com/ComposeFlow/ComposeFlow/issues/63

    // Compose Multiplatform supports language and region qualifiers for resources.
    // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-setup.html#language-and-regional-qualifiers
    @Serializable(with = LocationAwareLocaleSerializer::class)
    @SerialName("Locale")
    data class Locale(
        /** Two-letter ISO 639-1 language code */
        val language: String,
        /** Two-letter ISO 3166-1 alpha-2 region code */
        val region: String? = null,
    ) {
        override fun toString(): String =
            buildString {
                append(language)
                if (region != null) {
                    append("-r$region")
                }
            }

        companion object {
            /**
             * Creates a Locale from a string like "en" or "en-rUS".
             * If region is not specified, defaults to null.
             */
            fun fromString(locale: String): Locale {
                val parts = locale.split("-")
                return if (parts.size == 2) {
                    if (parts[1].startsWith("r")) {
                        Locale(parts[0], parts[1].substring(1))
                    } else {
                        Locale(parts[0], parts[1])
                    }
                } else {
                    Locale(parts[0])
                }
            }
        }
    }
}

@JvmName("stringResourceOfWithLocale")
fun stringResourceOf(
    key: String,
    vararg localizedValues: Pair<StringResource.Locale, String>,
    description: String? = null,
    id: StringResourceId = Uuid.random().toString(),
): StringResource =
    StringResource(
        id = id,
        key = key,
        description = description,
        localizedValues = localizedValues.toMap().toMutableStateMapEqualsOverride(),
    )

@JvmName("stringResourceOfWithString")
fun stringResourceOf(
    key: String,
    vararg localizedValues: Pair<String, String>,
    description: String? = null,
    id: StringResourceId = Uuid.random().toString(),
): StringResource =
    StringResource(
        id = id,
        key = key,
        description = description,
        localizedValues =
            localizedValues
                .associate { (locale, value) ->
                    StringResource.Locale.fromString(locale) to value
                }.toMutableStateMapEqualsOverride(),
    )
