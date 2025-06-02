package io.composeflow.model.state

import io.composeflow.model.project.Project
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.FallbackMutableStateListSerializer
import io.composeflow.util.generateUniqueName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias StateResult = Pair<StateHolderType, ReadableState>

interface StateHolder {

    fun getStateResults(project: Project): List<StateResult>
    fun getStates(project: Project): List<ReadableState>
    fun addState(readableState: ReadableState)
    fun updateState(readableState: ReadableState)

    fun createUniqueName(project: Project, initial: String): String
    fun findStateOrNull(project: Project, stateId: StateId): ReadableState?

    /**
     * Remove the state from the [StateHolder]  instance.
     * @return true if the state is successfully removed.
     */
    fun removeState(stateId: StateId): Boolean
    fun copyContents(other: StateHolder)
}

@Serializable
@SerialName("StateHolderImpl")
data class StateHolderImpl(
    @Serializable(FallbackMutableStateListSerializer::class)
    val states: MutableList<ReadableState> = mutableStateListEqualsOverrideOf(),
) : StateHolder {

    override fun getStates(project: Project): List<ReadableState> = states
    override fun getStateResults(project: Project): List<StateResult> =
        states.map { StateHolderType.Global to it }

    override fun addState(readableState: ReadableState) {
        states.add(readableState)
    }

    override fun createUniqueName(project: Project, initial: String): String {
        return generateUniqueName(
            initial = initial,
            existing = getStates(project).map { it.name }.toSet(),
        )
    }

    override fun findStateOrNull(project: Project, stateId: StateId): ReadableState? =
        getStates(project).firstOrNull { it.id == stateId }

    override fun removeState(stateId: StateId): Boolean =
        states.removeIf {
            it.id == stateId
        }

    override fun updateState(readableState: ReadableState) {
        val index = states.indexOfFirst { it.id == readableState.id }
        if (index != -1) {
            states[index] = readableState
        }
    }

    override fun copyContents(other: StateHolder) {
        states.clear()
        states.addAll((other as? StateHolderImpl)?.states ?: emptyList())
    }
}

