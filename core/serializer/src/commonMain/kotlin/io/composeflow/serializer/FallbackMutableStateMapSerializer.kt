package io.composeflow.serializer

import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for MutableStateMap (SnapshotStateMap).
 * MutableStateMap can be treated as a normal MutableMap, thus it can be serialized by using the built-in
 * serializer, but using the built-in serializer causes the deserialized value to be just a MutableMap.
 * This serializer is to keep using MutableStateMap for the deserialized value.
 *
 *  Falls back to the built-in serializer when the MutableStateMap cannot be deserialized.
 */
@OptIn(ExperimentalSerializationApi::class)
class FallbackMutableStateMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>,
) : KSerializer<MutableMap<K, V>> {

    override val descriptor: SerialDescriptor =
        mapSerialDescriptor(keySerializer.descriptor, valueSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: MutableMap<K, V>) {
        MapSerializer(keySerializer, valueSerializer).serialize(encoder, value.toMap())
    }

    override fun deserialize(decoder: Decoder): MutableMap<K, V> {
        return try {
            MapSerializer(keySerializer, valueSerializer).deserialize(decoder)
                .toMutableMap()
                .toMutableStateMap()
        } catch (e: SerializationException) {
            println("Deserialization failed, returning empty state map: ${e.message}")
            mutableStateMapOf()
        }
    }
}

private fun <K, V> MutableMap<K, V>.toMutableStateMap(): MutableMap<K, V> {
    val stateMap = mutableStateMapOf<K, V>()
    stateMap.putAll(this)
    return stateMap
}
