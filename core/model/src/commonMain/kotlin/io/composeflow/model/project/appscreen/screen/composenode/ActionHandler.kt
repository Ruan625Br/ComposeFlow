package io.composeflow.model.project.appscreen.screen.composenode

import androidx.compose.runtime.mutableStateMapOf
import io.composeflow.model.action.Action
import io.composeflow.model.action.ActionNode
import io.composeflow.model.action.ActionType
import io.composeflow.model.parameter.TextFieldTrait
import io.composeflow.model.project.Project
import io.composeflow.serializer.FallbackMutableStateListSerializer
import io.composeflow.serializer.FallbackMutableStateMapSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ActionHandler {
    var actionsMap: MutableMap<ActionType, MutableList<ActionNode>>
    fun allActions(): List<Action>
    fun allActionNodes(): List<ActionNode>

    /**
     * Get the list of ComposeNode for [actionType] that enables [ComposeStateValidator]
     */
    fun getDependentValidatorNodesForActionType(
        project: Project, actionType: ActionType,
    ): List<ComposeNode>
}

@Serializable
@SerialName("ActionHandlerImpl")
class ActionHandlerImpl : ActionHandler {
    @Serializable(with = FallbackMutableStateMapSerializer::class)
    override var actionsMap: MutableMap<
            ActionType,
            @Serializable(with = FallbackMutableStateListSerializer::class) MutableList<ActionNode>,
            > = mutableStateMapOf()

    override fun getDependentValidatorNodesForActionType(
        project: Project,
        actionType: ActionType,
    ): List<ComposeNode> {
        val actionNodes = actionsMap[actionType]
        val dependentComposeNodes = actionNodes?.flatMap { it.allActions() }
            ?.flatMap { it.getDependentComposeNodes(project) }
        return dependentComposeNodes?.filter {
            // At the moment, only TextField is able to have a validator
            it.trait.value is TextFieldTrait &&
                    (it.trait.value as TextFieldTrait).enableValidator == true
        } ?: emptyList()
    }

    override fun allActions(): List<Action> {
        return actionsMap.entries.flatMap { entry ->
            entry.value.flatMap { it.allActions() }
        }
    }

    override fun allActionNodes(): List<ActionNode> = actionsMap.entries.flatMap { it.value }
}
