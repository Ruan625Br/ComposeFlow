package io.composeflow.serializer

import io.composeflow.model.project.appscreen.screen.composenode.ActionHandler
import io.composeflow.model.project.appscreen.screen.composenode.ActionHandlerImpl
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom serializer for ActionHandler with a fallback to a default ActionHandlerImpl instance.
 *
 * This serializer attempts to deserialize an ActionHandler as an ActionHandlerImpl and falls back
 * to a default ActionHandlerImpl instance if deserialization fails.
 */
object FallbackActionHandlerSerializer : KSerializer<ActionHandler> {
    // We will primarily deal with ActionHandlerImpl as the concrete type.
    private val delegateSerializer = ActionHandlerImpl.serializer()

    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: ActionHandler,
    ) {
        // When serializing, we expect the 'value' to be an instance of ActionHandlerImpl
        // because our deserialization fallback and delegate are tied to ActionHandlerImpl.
        if (value is ActionHandlerImpl) {
            encoder.encodeSerializableValue(delegateSerializer, value)
        } else {
            // If 'value' can be other @Serializable implementations of ActionHandler,
            // and you have a proper polymorphic serialization setup (e.g., in SerializersModule),
            // you might try to find the actual serializer for 'value::class'.
            // However, for this specific fallback scenario focused on ActionHandlerImpl,
            // we'll assume that what we serialize should also be an ActionHandlerImpl.
            throw SerializationException(
                "FallbackActionHandlerSerializer is designed to serialize ActionHandlerImpl. " +
                    "Encountered type: ${value::class.simpleName}. " +
                    "If other implementations are needed, consider a polymorphic serialization setup.",
            )
        }
    }

    override fun deserialize(decoder: Decoder): ActionHandler =
        try {
            // Attempt to deserialize as ActionHandlerImpl
            decoder.decodeSerializableValue(delegateSerializer)
        } catch (e: Exception) {
            // Log the exception for debugging purposes (optional but recommended)
            // In a real app, use a proper logger.
            println(
                "WARN: FallbackActionHandlerSerializer failed to deserialize ActionHandler. " +
                    "Falling back to a default ActionHandlerImpl instance. Error: ${e.message}",
            )
            // Fallback to a new default instance of ActionHandlerImpl
            ActionHandlerImpl()
        }
}
