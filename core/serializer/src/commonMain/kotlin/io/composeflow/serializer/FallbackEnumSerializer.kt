package io.composeflow.serializer

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
 */
open class FallbackEnumSerializer<T : Enum<T>>(enumClass: KClass<T>) : KSerializer<T> {
    private val values = enumClass.java.enumConstants
    private val fallback = values.first() // Default to first entry

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(enumClass.simpleName ?: "Enum", STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name) // Store as string
    }

    override fun deserialize(decoder: Decoder): T {
        val stringValue = decoder.decodeString()
        return values.find { it.name.equals(stringValue, ignoreCase = true) } ?: fallback
    }
}