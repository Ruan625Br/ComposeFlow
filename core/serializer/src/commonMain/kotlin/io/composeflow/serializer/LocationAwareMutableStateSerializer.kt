package io.composeflow.serializer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private class MutableStateSerializer<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<MutableState<T>> {
    override val descriptor: SerialDescriptor = dataSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: MutableState<T>,
    ) = dataSerializer.serialize(encoder, value.value)

    override fun deserialize(decoder: Decoder): MutableState<T> = mutableStateOf(dataSerializer.deserialize(decoder))
}

/**
 * Location-aware MutableStateSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when state parsing fails.
 */
class LocationAwareMutableStateSerializer<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<MutableState<T>> {
    private val delegate = MutableStateSerializer(dataSerializer).withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: MutableState<T>,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): MutableState<T> = delegate.deserialize(decoder)
}
