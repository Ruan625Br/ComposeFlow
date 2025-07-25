package io.composeflow.serializer

import co.touchlab.kermit.Logger
import io.composeflow.override.toMutableStateListEqualsOverride
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for MutableStateList (SnapshotStateList).
 * MutableStateList can be treated as a normal MutableList, thus it can be serialized by using the built-in
 * serializer, but using the built-in serializer causes the deserialized value to be just a MutableList.
 * This serializer is to keep using MutableStateList for the deserialized value.
 *
 * Falls back to the built-in MutableList serializer when the MutableStateList cannot be deserialized.
 */
@OptIn(ExperimentalSerializationApi::class)
private class FallbackMutableStateListSerializerInternal<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<MutableList<T>> {
    override val descriptor = listSerialDescriptor(dataSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: MutableList<T>,
    ) = ListSerializer(dataSerializer).serialize(encoder, value.toList())

    override fun deserialize(decoder: Decoder): MutableList<T> =
        try {
            ListSerializer(dataSerializer).deserialize(decoder).toMutableStateListEqualsOverride()
        } catch (e: SerializationException) {
            Logger.e { "Failed to deserialize list: ${e.message}, returning empty list" }
            listOf<T>().toMutableStateListEqualsOverride()
        }
}

/**
 * Location-aware FallbackMutableStateListSerializer class that can be used in @Serializable annotations.
 * Provides enhanced error reporting with precise location information when state list parsing fails.
 */
class FallbackMutableStateListSerializer<T>(
    dataSerializer: KSerializer<T>,
) : KSerializer<MutableList<T>> {
    private val delegate =
        FallbackMutableStateListSerializerInternal(dataSerializer).withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: MutableList<T>,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): MutableList<T> = delegate.deserialize(decoder)
}
