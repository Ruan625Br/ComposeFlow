package io.composeflow.serializer

import com.charleskorn.kaml.YamlInput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A custom serializer wrapper that intercepts SerializationExceptions during deserialization
 * and enhances them with location-aware information from YamlNode context.
 *
 * This is a more robust approach than regex parsing of YAML content, as it leverages
 * the serializer's context and the kaml library's built-in location tracking.
 *
 * Usage example:
 * ```kotlin
 * val locationAwareSerializer = LocationAwareSerializerWrapper(MyData.serializer())
 * val yaml = Yaml(serializersModule = SerializersModule {
 *     contextual(MyData::class, locationAwareSerializer)
 * })
 * ```
 */
class LocationAwareSerializerWrapper<T>(
    private val delegate: KSerializer<T>,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: T,
    ) {
        // Serialization doesn't need location enhancement
        delegate.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): T =
        try {
            delegate.deserialize(decoder)
        } catch (e: SerializationException) {
            // Try to extract location information from the decoder context
            val enhancedException = createEnhancedExceptionFromDecoder(e, decoder)
            throw enhancedException
        }

    /**
     * Creates an enhanced exception with location information extracted from the decoder context.
     * This approach is more reliable than parsing YAML strings with regex.
     */
    private fun createEnhancedExceptionFromDecoder(
        exception: SerializationException,
        decoder: Decoder,
    ): YamlLocationAwareException =
        try {
            // Try to extract location information from the decoder
            // Note: This is a simplified approach. In practice, you might need to cast
            // the decoder to a kaml-specific decoder type to access YamlNode information
            val locationInfo = extractLocationFromDecoder(decoder)

            YamlLocationAwareException(
                message = exception.message ?: "Serialization error during deserialization",
                line = locationInfo?.line,
                column = locationInfo?.column,
                yamlPath = locationInfo?.yamlPath,
                yamlContent = locationInfo?.yamlContent,
                cause = exception,
            )
        } catch (e: Exception) {
            // If we can't extract location info, return a basic enhanced exception
            YamlLocationAwareException(
                message = exception.message ?: "Serialization error during deserialization",
                cause = exception,
            )
        }

    /**
     * Attempts to extract location information from a Decoder instance.
     * This leverages kaml's internal YamlInput structure to get precise location information.
     */
    private fun extractLocationFromDecoder(decoder: Decoder): DecoderLocationInfo? =
        try {
            when (decoder) {
                is YamlInput -> {
                    // YamlInput provides direct access to location and path information
                    val currentLocation = decoder.getCurrentLocation()
                    val currentPath = decoder.getCurrentPath()
                    val currentNode = decoder.node

                    // Try to extract YAML content context from the node if possible
                    val yamlContent =
                        try {
                            // We can't get the full YAML document from YamlInput,
                            // but we can get a representation of the current node
                            currentNode.toString()
                        } catch (e: Exception) {
                            null
                        }

                    DecoderLocationInfo(
                        line = currentLocation.line,
                        column = currentLocation.column,
                        yamlPath = currentPath,
                        yamlContent = yamlContent,
                    )
                }

                else -> {
                    // If decoder is not a YamlInput, we can't extract location information
                    // This might happen if using a different serialization format or wrapper
                    null
                }
            }
        } catch (e: Exception) {
            // If anything goes wrong accessing decoder internals, return null
            // to fall back to other location extraction methods
            null
        }

    /**
     * Data class to hold location information extracted from decoder context.
     */
    private data class DecoderLocationInfo(
        val line: Int?,
        val column: Int?,
        val yamlPath: com.charleskorn.kaml.YamlPath?,
        val yamlContent: String?,
    )
}

/**
 * Extension function to wrap any KSerializer with location-aware exception handling.
 */
fun <T> KSerializer<T>.withLocationAwareExceptions(): LocationAwareSerializerWrapper<T> = LocationAwareSerializerWrapper(this)
