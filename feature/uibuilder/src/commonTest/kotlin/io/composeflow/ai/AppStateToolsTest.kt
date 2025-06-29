package io.composeflow.ai

import io.composeflow.ai.openrouter.tools.ToolArgs
import io.composeflow.ai.openrouter.tools.ToolExecutionStatus
import io.composeflow.model.project.Project
import io.composeflow.model.state.AppState
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for app state tool operations using ToolDispatcher.
 * Verifies that ListAppStatesArgs and GetAppStateArgs properly return data to the LLM.
 */
class AppStateToolsTest {

    private lateinit var project: Project
    private lateinit var toolDispatcher: ToolDispatcher

    @Before
    fun setUp() {
        project = Project()
        toolDispatcher = ToolDispatcher()
    }

    private fun executeToolAndSetStatus(toolArgs: ToolArgs) {
        val eventResult = toolDispatcher.dispatchToolResponse(project, toolArgs)
        toolArgs.status = if (eventResult.isSuccessful()) ToolExecutionStatus.Success else ToolExecutionStatus.Error
    }

    @Test
    fun testListAppStatesArgs_EmptyProject() {
        // Test listing app states in an empty project
        val toolArgs = ToolArgs.ListAppStatesArgs()
        
        executeToolAndSetStatus(toolArgs)
        
        assertEquals(ToolExecutionStatus.Success, toolArgs.status)
        assertTrue(toolArgs.result.contains("[]") || toolArgs.result.isBlank()) // Empty list in YAML format
    }

    @Test
    fun testListAppStatesArgs_WithAppStates() {
        // Add some app states to the project
        val stringState1 = AppState.StringAppState(name = "userInput", defaultValue = "Hello")
        val stringState2 = AppState.StringAppState(name = "counter", defaultValue = "0")
        
        project.globalStateHolder.addState(stringState1)
        project.globalStateHolder.addState(stringState2)
        
        val toolArgs = ToolArgs.ListAppStatesArgs()
        
        executeToolAndSetStatus(toolArgs)
        
        assertEquals(ToolExecutionStatus.Success, toolArgs.status)
        
        // Result should contain YAML representation of the states
        assertTrue(toolArgs.result.contains("userInput") || toolArgs.result.contains("\"userInput\""))
        assertTrue(toolArgs.result.contains("counter") || toolArgs.result.contains("\"counter\""))
        assertTrue(toolArgs.result.contains("Hello") || toolArgs.result.contains("\"Hello\""))
    }

    @Test
    fun testGetAppStateArgs_ExistingState() {
        // Add an app state to the project
        val stringState = AppState.StringAppState(name = "testState", defaultValue = "test value")
        project.globalStateHolder.addState(stringState)
        
        val toolArgs = ToolArgs.GetAppStateArgs(appStateId = stringState.id)
        
        executeToolAndSetStatus(toolArgs)
        
        assertEquals(ToolExecutionStatus.Success, toolArgs.status)
        
        // Result should contain YAML representation of the specific state
        assertTrue(toolArgs.result.contains("testState") || toolArgs.result.contains("\"testState\""))
        assertTrue(toolArgs.result.contains("test value") || toolArgs.result.contains("\"test value\""))
    }

    @Test
    fun testGetAppStateArgs_NonExistentState() {
        // Try to get a state that doesn't exist
        val toolArgs = ToolArgs.GetAppStateArgs(appStateId = "non-existent-id")
        
        executeToolAndSetStatus(toolArgs)
        
        assertEquals(ToolExecutionStatus.Success, toolArgs.status)
        
        // Result should indicate the state was not found
        assertTrue(toolArgs.result.contains("not found") || toolArgs.result.contains("null"))
    }

    @Test
    fun testToolArgsResultProperty_IsModifiable() {
        // Test that the result property can be modified (was previously val)
        val toolArgs = ToolArgs.ListAppStatesArgs()
        
        // Initial value
        assertEquals("Successfully executed.", toolArgs.result)
        
        // Should be able to modify
        toolArgs.result = "Custom result"
        assertEquals("Custom result", toolArgs.result)
        
        // Should be able to modify again
        toolArgs.result = "Another result"
        assertEquals("Another result", toolArgs.result)
    }

    @Test
    fun testIntegrationFlow_ListThenGet() {
        // Test a realistic scenario: list states, then get a specific one
        
        // Add app states
        val state1 = AppState.StringAppState(name = "userName", defaultValue = "Anonymous")
        val state2 = AppState.StringAppState(name = "theme", defaultValue = "light")
        project.globalStateHolder.addState(state1)
        project.globalStateHolder.addState(state2)
        
        // First, list all states
        val listToolArgs = ToolArgs.ListAppStatesArgs()
        executeToolAndSetStatus(listToolArgs)
        
        assertEquals(ToolExecutionStatus.Success, listToolArgs.status)
        val listResult = listToolArgs.result
        
        // Result should contain both states
        assertTrue(listResult.contains("userName") || listResult.contains("\"userName\""))
        assertTrue(listResult.contains("theme") || listResult.contains("\"theme\""))
        
        // Then, get a specific state
        val getToolArgs = ToolArgs.GetAppStateArgs(appStateId = state1.id)
        executeToolAndSetStatus(getToolArgs)
        
        assertEquals(ToolExecutionStatus.Success, getToolArgs.status)
        val getResult = getToolArgs.result
        
        // Result should contain the specific state
        assertTrue(getResult.contains("userName") || getResult.contains("\"userName\""))
        assertTrue(getResult.contains("Anonymous") || getResult.contains("\"Anonymous\""))
        // Should not contain the other state
        assertTrue(!getResult.contains("theme") || !getResult.contains("\"theme\"") || getResult.contains("not found"))
    }
}