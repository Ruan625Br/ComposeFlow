package io.composeflow.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private class FallbackSealedSerializer<T : Any>(
    private val defaultInstance: T,
    private val serializer: KSerializer<T>,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor
        get() = serializer.descriptor

    override fun deserialize(decoder: Decoder): T =
        try {
            serializer.deserialize(decoder)
        } catch (e: SerializationException) {
            defaultInstance
        }

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        serializer.serialize(encoder, value)
    }
}

/**
 * Location-aware FallbackSealedSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when sealed class parsing fails.
 */
class LocationAwareFallbackSealedSerializer<T : Any>(
    defaultInstance: T,
    serializer: KSerializer<T>,
) : KSerializer<T> {
    private val delegate =
        FallbackSealedSerializer(defaultInstance, serializer).withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T = delegate.deserialize(decoder)
}
