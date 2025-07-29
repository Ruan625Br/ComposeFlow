package io.composeflow.model.project.string
import io.composeflow.serializer.withLocationAwareExceptions
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private object ResourceLocaleSerializerInternal : KSerializer<ResourceLocale> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourceLocale", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: ResourceLocale,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ResourceLocale {
        val localeString = decoder.decodeString()
        return ResourceLocale.fromStringOrThrow(localeString)
    }
}

/**
 * Location-aware ResourceLocaleSerializer that provides enhanced error reporting with precise location information
 * when locale parsing fails. This helps with debugging YAML files containing invalid locale definitions.
 */
class ResourceLocaleSerializer : KSerializer<ResourceLocale> {
    private val delegate = ResourceLocaleSerializerInternal.withLocationAwareExceptions()

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: ResourceLocale,
    ) {
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): ResourceLocale = delegate.deserialize(decoder)
}
