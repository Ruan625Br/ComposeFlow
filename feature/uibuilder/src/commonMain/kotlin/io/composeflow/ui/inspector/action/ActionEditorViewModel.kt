package io.composeflow.ui.inspector.action

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import io.composeflow.model.action.Action
import io.composeflow.model.action.ActionNode
import io.composeflow.model.action.ActionNodeId
import io.composeflow.model.action.ActionType
import io.composeflow.model.action.FocusableActionNode
import moe.tlaster.precompose.viewmodel.ViewModel

class ActionEditorViewModel : ViewModel() {
    val actionsMap: MutableMap<ActionType, MutableList<ActionNode>> = mutableStateMapOf()

    var currentActionType: MutableState<ActionType> = mutableStateOf(ActionType.OnClick)
    var focusedActionNodeId: MutableState<ActionNodeId?> = mutableStateOf(null)

    fun initializeMap(
        actionTypes: List<ActionType>,
        initialActionsMap: Map<ActionType, List<ActionNode>>,
    ) {
        actionsMap.clear()
        actionsMap.putAll(
            if (initialActionsMap.isNotEmpty()) {
                initialActionsMap.entries
                    .sortedBy { it.key.priority }
                    .associate { it.key to it.value.toMutableStateList() }
            } else {
                actionTypes
                    .sortedBy { it.priority }
                    .associateWith { mutableStateListOf() }
            },
        )
        currentActionType.value =
            if (actionsMap.entries.isNotEmpty()) {
                actionsMap.entries.minByOrNull { it.key.priority }!!.key
            } else {
                ActionType.OnClick
            }
    }

    fun findFocusableActionNodeOrNull(id: ActionNodeId?): FocusableActionNode? =
        id?.let { actionNodeId ->
            actionsMap[currentActionType.value]
                ?.firstOrNull { it.hasActionNode(id) }
                ?.findFocusableActionOrNull(actionNodeId)
        }

    fun onActionTypeSelected(actionType: ActionType) {
        currentActionType.value = actionType
    }

    fun onActionNodesUpdated(
        actionType: ActionType,
        actionNodes: List<ActionNode>,
    ) {
        actionsMap[actionType] = actionNodes.toMutableStateList()
    }

    fun onFocusableActionNodeClicked(focusableActionNode: FocusableActionNode) {
        focusedActionNodeId.value = focusableActionNode.id
    }

    fun onUpdateActionWithActionNodeId(
        actionNodeId: ActionNodeId,
        action: Action,
    ) {
        val index =
            actionsMap[currentActionType.value]?.indexOfFirst { it.hasActionNode(actionNodeId) }
        if (index != null && index != -1) {
            actionsMap[currentActionType.value]?.set(
                index,
                actionsMap[currentActionType.value]!![index].replaceAction(
                    id = actionNodeId,
                    action = action,
                ),
            )
        }
    }

    fun onRemoveActionWithActionNodeId(actionNodeId: ActionNodeId) {
        val index = actionsMap[currentActionType.value]?.indexOfFirst { it.id == actionNodeId }
        if (index != null && index != -1) {
            actionsMap[currentActionType.value]?.removeAt(index)
        }
        actionsMap[currentActionType.value]?.forEach {
            it.removeAction(actionNodeId)
        }
    }
}
