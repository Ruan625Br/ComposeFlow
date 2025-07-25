@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.composeflow.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.time.Instant

private object FallbackInstantSerializer : KSerializer<Instant> {
    private val delegate = serializer<Instant>()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Instant =
        try {
            delegate.deserialize(decoder)
        } catch (e: Exception) {
            Instant.DISTANT_PAST
        }
}

/**
 * Location-aware FallbackInstantSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when instant parsing fails.
 */
class LocationAwareFallbackInstantSerializer : KSerializer<Instant> {
    private val delegate = FallbackInstantSerializer.withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Instant,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): Instant = delegate.deserialize(decoder)
}
