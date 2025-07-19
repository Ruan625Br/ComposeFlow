package io.composeflow.serializer

import io.composeflow.override.toMutableStateListEqualsOverride
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for MutableStateList (SnapshotStateList).
 * MutableStateList can be treated as a normal MutableList, thus it can be serialized by using the built-in
 * serializer, but using the built-in serializer causes the deserialized value to be just a MutableList.
 * This serializer is to keep using MutableStateList for the deserialized value.
 */
@OptIn(ExperimentalSerializationApi::class)
class MutableStateListSerializer<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<MutableList<T>> {
    override val descriptor = listSerialDescriptor(dataSerializer.descriptor)

    override fun serialize(
        encoder: Encoder,
        value: MutableList<T>,
    ) = ListSerializer(dataSerializer).serialize(encoder, value.toList())

    override fun deserialize(decoder: Decoder): MutableList<T> =
        ListSerializer(dataSerializer).deserialize(decoder).toMutableStateListEqualsOverride()
}
