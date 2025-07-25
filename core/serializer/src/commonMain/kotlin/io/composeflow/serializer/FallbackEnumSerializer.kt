package io.composeflow.serializer

import co.touchlab.kermit.Logger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

/**
 * Serializer for a Enum class where, it attempts to deserialize from a string value.
 * If a matching entry isn't found, fall back to the first entry.
 *
 * Features:
 * - Case-insensitive deserialization
 * - Graceful fallback to first enum entry for unknown values
 * - Proper logging of fallback scenarios
 * - Null-safe implementation
 */
private open class FallbackEnumSerializerInternal<T : Enum<T>>(
    enumClass: KClass<T>,
) : KSerializer<T> {
    private val values = enumClass.java.enumConstants ?: arrayOf()
    private val fallback =
        values.firstOrNull()
            ?: throw IllegalArgumentException("Enum class ${enumClass.simpleName} has no values")
    private val enumClassName = enumClass.simpleName ?: "Unknown"

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(enumClassName, STRING)

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        encoder.encodeString(value.name) // Store as string
    }

    override fun deserialize(decoder: Decoder): T {
        val stringValue = decoder.decodeString().trim()

        // Fast path: exact match
        val exactMatch = values.find { it.name == stringValue }
        if (exactMatch != null) return exactMatch

        // Fallback: case-insensitive match
        val caseInsensitiveMatch = values.find { it.name.equals(stringValue, ignoreCase = true) }
        if (caseInsensitiveMatch != null) return caseInsensitiveMatch

        // Last resort: use fallback and log
        if (stringValue.isNotEmpty()) {
            // Only log in debug/development, not in production
            // This prevents log spam but helps with debugging
            Logger.w("Warning: Unknown $enumClassName value '$stringValue', falling back to '${fallback.name}'")
        }

        return fallback
    }
}

/**
 * Location-aware FallbackEnumSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when enum parsing fails.
 */
open class FallbackEnumSerializer<T : Enum<T>>(
    enumClass: KClass<T>,
) : KSerializer<T> {
    private val delegate = FallbackEnumSerializerInternal(enumClass).withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T = delegate.deserialize(decoder)
}
