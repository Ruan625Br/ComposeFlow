package io.composeflow.model.project.string

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Supported locales for string resources based on Google Play supported languages.
 * https://support.google.com/googleplay/android-developer/table/4419860?hl=en
 */
@Serializable(with = ResourceLocaleSerializer::class)
@SerialName("Locale")
enum class ResourceLocale(
    val language: String,
    val region: String? = null,
) {
    AFRIKAANS("af"),
    AMHARIC("am"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE_HONG_KONG("zh", "HK"),
    CHINESE_PRC("zh", "CN"),
    CHINESE_TAIWAN("zh", "TW"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH_UK("en", "GB"),
    ENGLISH_US("en", "US"),
    ESTONIAN("et"),
    FILIPINO("fil"),
    FINNISH("fi"),
    FRENCH_CANADA("fr", "CA"),
    FRENCH_FRANCE("fr", "FR"),
    GERMAN("de"),
    GREEK("el"),
    HEBREW("he"),
    HINDI("hi"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    INDONESIAN("id"), // Note: Also accepts "in" as alternative
    ITALIAN("it"),
    JAPANESE("ja"),
    KOREAN("ko"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MALAY("ms"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE_BRAZIL("pt", "BR"),
    PORTUGUESE_PORTUGAL("pt", "PT"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN("sr"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH_LATIN_AMERICA("es", "419"),
    SPANISH_SPAIN("es", "ES"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    VIETNAMESE("vi"),
    ZULU("zu"),
    ;

    /**
     * Returns the string representation of the locale in Compose Multiplatform format.
     * For locales with regions, returns "language-rREGION" (e.g., "en-rUS").
     * For locales without regions, returns just the language code (e.g., "de").
     */
    override fun toString(): String =
        if (region != null) {
            "$language-r$region"
        } else {
            language
        }

    /**
     * Returns the language code with region in standard format (e.g., "en-US", "de").
     */
    val languageCode: String
        get() = if (region != null) "$language-$region" else language

    companion object Companion {
        /**
         * Creates a Locale from a string like "en", "en-US", or "en-rUS".
         * Returns null if the locale string doesn't match any supported locale.
         */
        fun fromString(localeString: String): ResourceLocale? {
            // Handle both "en-US" and "en-rUS" formats
            val normalizedString =
                if (localeString.contains("-r")) {
                    localeString.replace("-r", "-")
                } else {
                    localeString
                }

            // Also handle "in" as alternative for Indonesian
            val searchString = if (normalizedString == "in") "id" else normalizedString

            return entries.find {
                val code = if (it.region != null) "${it.language}-${it.region}" else it.language
                code == searchString
            }
        }

        /**
         * Creates a Locale from a string, throwing an exception if not found.
         */
        fun fromStringOrThrow(localeString: String): ResourceLocale =
            fromString(localeString) ?: throw IllegalArgumentException("Unsupported locale: $localeString")
    }
}
