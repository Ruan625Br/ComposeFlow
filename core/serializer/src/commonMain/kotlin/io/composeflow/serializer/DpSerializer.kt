package io.composeflow.serializer

import androidx.compose.ui.unit.Dp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.float
import kotlinx.serialization.json.jsonPrimitive

private const val UNSPECIFIED_KEYWORD = "unspecified"

object DpNonNegativeSerializer :
    KSerializer<Dp> by InternalDpSerializer(allowNegative = false)

object DpSerializer : KSerializer<Dp> by InternalDpSerializer(allowNegative = true)

private data class InternalDpSerializer(
    val allowNegative: Boolean = true,
) : KSerializer<Dp> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Dp", PrimitiveKind.FLOAT)

    override fun serialize(
        encoder: Encoder,
        value: Dp,
    ) {
        encoder.encodeFloat(value.value)
    }

    override fun deserialize(decoder: Decoder): Dp =
        when (decoder) {
            is JsonDecoder -> {
                val element = decoder.decodeJsonElement()
                when {
                    element is JsonNull || (element is JsonPrimitive && element.content == UNSPECIFIED_KEYWORD) -> Dp.Unspecified
                    element is JsonPrimitive && element.isString -> {
                        try {
                            val dpValue = element.content.toFloat()
                            if (dpValue < 0.0f && !allowNegative) {
                                Dp(0f)
                            } else {
                                Dp(dpValue)
                            }
                        } catch (e: NumberFormatException) {
                            Dp.Unspecified // Or throw an exception if an invalid string is not acceptable
                        }
                    }

                    else -> Dp(element.jsonPrimitive.float)
                }
            }

            else -> {
                try {
                    val dpValue = decoder.decodeFloat()
                    if (dpValue < 0.0f && !allowNegative) {
                        Dp(0f)
                    } else {
                        Dp(dpValue)
                    }
                } catch (e: Exception) {
                    Dp.Unspecified // Handle other decoder types or throw an exception
                }
            }
        }
}

/**
 * Location-aware DpSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when Dp parsing fails.
 */
class LocationAwareDpSerializer : KSerializer<Dp> {
    private val delegate = DpSerializer.withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Dp,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Dp = delegate.deserialize(decoder)
}

/**
 * Location-aware DpNonNegativeSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when Dp parsing fails.
 */
class LocationAwareDpNonNegativeSerializer : KSerializer<Dp> {
    private val delegate = DpNonNegativeSerializer.withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Dp,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Dp = delegate.deserialize(decoder)
}
