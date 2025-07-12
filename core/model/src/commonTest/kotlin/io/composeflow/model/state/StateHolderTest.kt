package io.composeflow.model.state

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StateHolderTest {
    @Test
    fun testCopyContents() {
        val source = StateHolderImpl()
        val target = StateHolderImpl()

        // Add states to source
        val state1 =
            ScreenState.StringScreenState(
                name = "globalState1",
                defaultValue = "globalValue1",
            )
        val state2 =
            ScreenState.BooleanScreenState(
                name = "globalState2",
                defaultValue = false,
            )
        source.addState(state1)
        source.addState(state2)

        // Add different state to target that should be cleared
        val targetState =
            ScreenState.StringScreenState(
                name = "targetGlobalState",
                defaultValue = "targetGlobalValue",
            )
        target.addState(targetState)

        // Copy contents
        target.copyContents(source)

        // Verify states were copied and target state was cleared
        assertEquals(2, target.states.size)
        assertEquals("globalState1", target.states[0].name)
        assertEquals("globalState2", target.states[1].name)
    }

    @Test
    fun testCopyContentsWithEmptySource() {
        val source = StateHolderImpl()
        val target = StateHolderImpl()

        // Add state to target
        val targetState =
            ScreenState.StringScreenState(
                name = "targetState",
                defaultValue = "targetValue",
            )
        target.addState(targetState)

        // Copy empty source
        target.copyContents(source)

        // Verify target states were cleared
        assertTrue(target.states.isEmpty())
    }

    @Test
    fun testCopyContentsWithNonStateHolderImpl() {
        val target = StateHolderImpl()

        // Add state to target
        val targetState =
            ScreenState.StringScreenState(
                name = "targetState",
                defaultValue = "targetValue",
            )
        target.addState(targetState)

        // Create a mock StateHolder that is not StateHolderImpl
        val mockStateHolder =
            object : StateHolder {
                override fun getStateResults(project: io.composeflow.model.project.Project): List<StateResult> = emptyList()

                override fun getStates(project: io.composeflow.model.project.Project): List<ReadableState> = emptyList()

                override fun addState(readableState: ReadableState) {}

                override fun updateState(readableState: ReadableState) {}

                override fun createUniqueLabel(
                    project: io.composeflow.model.project.Project,
                    composeNode: io.composeflow.model.project.appscreen.screen.composenode.ComposeNode,
                    initial: String,
                ): String = ""

                override fun findStateOrNull(
                    project: io.composeflow.model.project.Project,
                    stateId: StateId,
                ): ReadableState? = null

                override fun removeState(stateId: StateId): Boolean = false

                override fun copyContents(other: StateHolder) {}
            }

        // Copy from non-StateHolderImpl source
        target.copyContents(mockStateHolder)

        // Verify target states were cleared (empty list is added)
        assertTrue(target.states.isEmpty())
    }
}
