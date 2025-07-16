package io.composeflow.serializer

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.composeflow.model.parameter.wrapper.shapeWrapperModule
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.modules.SerializersModule

val composeflowModule =
    SerializersModule {
        include(shapeWrapperModule)
    }

private val yamlConfiguration =
    YamlConfiguration(
        strictMode = false,
        breakScalarsAt = Int.MAX_VALUE,
        polymorphismStyle = PolymorphismStyle.Tag,
        encodeDefaults = false,
    )

/**
 * Default yaml serializer that deserializes the type tag as
 * (eg. !<typeOfThing> { property: value })
 */
val yamlDefaultSerializer =
    Yaml(
        configuration = yamlConfiguration,
        serializersModule = composeflowModule,
    )

/**
 * Fallback yaml serializer where it deserializes the type tag as
 * { type: typeOfThing, property: value }
 */
val yamlPropertyBasedSerializer =
    Yaml(
        configuration = yamlConfiguration.copy(polymorphismStyle = PolymorphismStyle.Property),
        serializersModule = composeflowModule,
    )

/**
 * Wrapper method that attempts to decode using yamlDefaultSerializer first,
 * and falls back to yamlPropertyBasedSerializer if a SerializationException occurs.
 */
inline fun <reified T> decodeFromStringWithFallback(yamlContent: String): T =
    try {
        yamlDefaultSerializer.decodeFromString<T>(yamlContent)
    } catch (e: SerializationException) {
        yamlPropertyBasedSerializer.decodeFromString<T>(yamlContent)
    }

/**
 * Wrapper method that attempts to decode using yamlDefaultSerializer first,
 * and falls back to yamlPropertyBasedSerializer if a SerializationException occurs.
 * This version takes a DeserializationStrategy for non-reified types.
 */
fun <T> decodeFromStringWithFallback(
    deserializer: DeserializationStrategy<T>,
    yamlContent: String,
): T =
    try {
        yamlDefaultSerializer.decodeFromString(deserializer, yamlContent)
    } catch (e: SerializationException) {
        yamlPropertyBasedSerializer.decodeFromString(deserializer, yamlContent)
    }
