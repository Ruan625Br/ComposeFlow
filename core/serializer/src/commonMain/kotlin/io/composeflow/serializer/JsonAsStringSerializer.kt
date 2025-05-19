package io.composeflow.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Serialize [JsonElement] to String. Although [JsonElement] is marked as [Serializable], its
 * Serializer expects JsonSerializer. This serializer just serialize [JsonElement] to String so that
 * other serializers (e.g. YamlSerializer) are able to serialize [JsonElement].
 */
object JsonAsStringSerializer : KSerializer<JsonElement> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("JsonAsString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): JsonElement =
        Json.parseToJsonElement(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: JsonElement) =
        encoder.encodeString(value.toString())
}
