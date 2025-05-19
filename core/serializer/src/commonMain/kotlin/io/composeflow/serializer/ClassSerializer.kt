package io.composeflow.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ClassSerializer : KSerializer<Class<*>> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("WithClassSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Class<*>) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Class<*> {
        val className = decoder.decodeString()
        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw SerializationException("Cannot find class $className")
        }
    }
}
