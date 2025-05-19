package io.composeflow.serializer

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object FallbackInstantSerializer : KSerializer<Instant> {

    private val delegate = Instant.serializer()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: Instant) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return try {
            delegate.deserialize(decoder)
        } catch (e: Exception) {
            Clock.System.now()
        }
    }
}