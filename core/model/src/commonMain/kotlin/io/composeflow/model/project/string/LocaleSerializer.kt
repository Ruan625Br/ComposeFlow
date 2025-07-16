package io.composeflow.model.project.string

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocaleSerializer : KSerializer<StringResource.Locale> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Locale", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: StringResource.Locale,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): StringResource.Locale {
        val localeString = decoder.decodeString()
        return StringResource.Locale.fromString(localeString)
    }
}
