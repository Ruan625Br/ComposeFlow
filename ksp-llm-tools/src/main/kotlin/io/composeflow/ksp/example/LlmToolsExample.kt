package io.composeflow.ksp.example

import io.composeflow.ksp.LlmToolsLoader
import java.io.File

/**
 * Example of how to use the LlmToolsLoader to load and format tool definitions for an LLM.
 *
 * This is a simplified example. In a real application, you would integrate this with your
 * LLM client library (e.g., OpenAI API client).
 */

object LlmToolsExample {
    /**
     * Main function to demonstrate loading and formatting LLM tools.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Path to the directory containing generated JSON files
        val toolsDir = File("feature/uibuilder/build/generated/llm-tools")

        // Load the tools
        val loader = LlmToolsLoader()
        val tools = loader.loadTools(toolsDir)

        println("Loaded ${tools.size} LLM tools:")
        tools.forEach { tool ->
            println("- ${tool.name}: ${tool.description}")
            println("  Parameters:")
            tool.parameters.forEach { param ->
                println("  - ${param.name} (${param.type}): ${param.description}")
                if (!param.required) {
                    println("    Optional, default: ${param.defaultValue}")
                }
            }
            println()
        }

        // Convert to Claude format
        val claudeToolsMap = loader.convertToClaudeFormat(tools)

        println("Claude format:")
        println(claudeToolsMap)
        println()

        // Extract the tools list for easier access
        @Suppress("UNCHECKED_CAST")
        val claudeTools = claudeToolsMap["tools"] as List<Map<String, Any>>

        // Example of how you might use this with Claude API client
        println("Example Claude API integration:")
        println(
            """
            // Using with Claude API (pseudocode)
            val claudeClient = ClaudeClient(apiKey)
            
            val response = claudeClient.createMessage {
                model = "claude-3-opus-20240229"
                messages = listOf(
                    Message(role = "user", content = "Add a Button component to the Column container.")
                )
                tools = claudeToolsMap
            }
            
            // Handle the tool call
            val toolCall = response.content[0].toolUse
            if (toolCall.name == "add_compose_node_to_container") {
                val args = toolCall.input
                // Parse arguments and call the actual function
                // ...
            }
            """.trimIndent(),
        )
    }
}
