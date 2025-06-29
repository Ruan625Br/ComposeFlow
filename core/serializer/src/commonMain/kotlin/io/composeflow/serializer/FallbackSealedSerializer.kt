package io.composeflow.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class FallbackSealedSerializer<T : Any>(
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
