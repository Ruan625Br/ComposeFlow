package io.composeflow.ai.claude

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ContentItemSerializer :
    JsonContentPolymorphicSerializer<ToolContentItem>(ToolContentItem::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ToolContentItem> {
        return when (val name = element.jsonObject["name"]?.jsonPrimitive?.content) {
            "add_compose_node_to_container" -> AddComposeNodeContentItem.serializer()
            "remove_compose_node" -> RemoveComposeNode.serializer()
            "add_modifier" -> AddModifier.serializer()
            "update_modifier" -> UpdateModifier.serializer()
            "remove_modifier" -> RemoveModifier.serializer()
            "swap_modifiers" -> SwapModifiers.serializer()
            "move_compose_node_to_container" -> MoveComposeNodeToContainer.serializer()
            else -> throw SerializationException("Unknown content item: $name")
        }
    }
}