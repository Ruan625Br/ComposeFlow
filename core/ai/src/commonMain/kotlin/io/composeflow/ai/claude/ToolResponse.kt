package io.composeflow.ai.claude

import io.composeflow.ai.Usage
import kotlinx.serialization.Serializable

@Serializable
data class ToolResponseWrapper(
    val response: Response,
    val tool_results: List<ToolResult> = emptyList(),
    val message: String? = null
)

@Serializable
data class Response(
    val id: String,
    val type: String,
    val role: String,
    val model: String,
    val content: List<@Serializable(with = ContentItemSerializer::class) ToolContentItem>,
    val stop_reason: String? = null,
    val stop_sequence: String? = null,
    val usage: Usage
)

@Serializable
sealed class ToolContentItem {
    abstract val type: String
    abstract val id: String
    abstract val name: String
}

@Serializable
data class AddComposeNodeContentItem(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: AddComposeNodeInput
) : ToolContentItem()

@Serializable
data class AddComposeNodeInput(
    val containerNodeId: String,
    val composeNodeYaml: String,
    val indexToDrop: Int = 0
)

@Serializable
data class RemoveComposeNode(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: RemoveComposeNodeInput
) : ToolContentItem()

@Serializable
data class RemoveComposeNodeInput(
    val composeNodeId: String,
)

@Serializable
data class AddModifier(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: AddModifierInput
) : ToolContentItem()

@Serializable
data class AddModifierInput(
    val composeNodeId: String,
    val modifierYaml: String,
)

@Serializable
data class UpdateModifier(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: UpdateModifierInput
) : ToolContentItem()

@Serializable
data class UpdateModifierInput(
    val composeNodeId: String,
    val index: Int,
    val modifierYaml: String,
)

@Serializable
data class RemoveModifier(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: RemoveModifierInput,
) : ToolContentItem()

@Serializable
data class RemoveModifierInput(
    val composeNodeId: String,
    val index: Int,
)

@Serializable
data class SwapModifiers(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: SwapModifierInput,
) : ToolContentItem()

@Serializable
data class SwapModifierInput(
    val composeNodeId: String,
    val fromIndex: Int,
    val toIndex: Int,
)

@Serializable
data class MoveComposeNodeToContainer(
    override val type: String,
    override val id: String,
    override val name: String,
    val input: MoveComposeNodeToContainerInput,
) : ToolContentItem()

@Serializable
data class MoveComposeNodeToContainerInput(
    val composeNodeId: String,
    val containerNodeId: String,
    val index: Int,
)

@Serializable
data class ToolResult(
    val tool_name: String,
    val tool_use_id: String,
    val error: String? = null,
    val type: String? = null,
)