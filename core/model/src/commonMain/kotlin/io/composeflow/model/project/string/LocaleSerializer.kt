package io.composeflow.model.project.string
import io.composeflow.model.project.string.LocationAwareLocaleSerializer
import io.composeflow.serializer.withLocationAwareExceptions
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

/**
 * Location-aware LocaleSerializer that provides enhanced error reporting with precise location information
 * when locale parsing fails. This helps with debugging YAML files containing invalid locale definitions.
 */
class LocationAwareLocaleSerializer : KSerializer<StringResource.Locale> {
    private val delegate = LocaleSerializer.withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: StringResource.Locale,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): StringResource.Locale = delegate.deserialize(decoder)
}
