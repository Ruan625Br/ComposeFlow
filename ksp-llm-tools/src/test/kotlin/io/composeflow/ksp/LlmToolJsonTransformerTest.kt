package io.composeflow.ksp

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

class LlmToolJsonTransformerTest {
    @Test
    fun `test transform with example annotation`() {
        // Create a sample LlmToolInfo based on the example annotation
        val toolInfo =
            LlmToolInfo(
                name = "add_compose_node_to_container",
                description = "Adds a Compose UI component to a container node in the UI builder. This allows placing UI elements inside containers like Column, Row, or Box.",
                category = "",
                className = "UiBuilderOperator",
                functionName = "onAddComposeNodeToContainerNode",
                packageName = "io.composeflow.ui.uibuilder",
                parameters =
                    listOf(
                        ParameterInfo(
                            name = "project",
                            type = "Project",
                            description = "",
                            required = true,
                            defaultValue = "",
                        ),
                        ParameterInfo(
                            name = "containerNodeId",
                            type = "kotlin.String",
                            description = "The ID of the container node where the component will be added. Must be a node that can contain other components.",
                            required = true,
                            defaultValue = "",
                        ),
                        ParameterInfo(
                            name = "composeNodeYaml",
                            type = "kotlin.String",
                            description = "The YAML representation of the ComposeNode node to be added to the container.",
                            required = true,
                            defaultValue = "",
                        ),
                        ParameterInfo(
                            name = "indexToDrop",
                            type = "kotlin.Int",
                            description = "The position index where the component should be inserted in the container.",
                            required = true,
                            defaultValue = "",
                        ),
                    ),
                returnType = "kotlin.Unit",
            )

        // Transform to MCP tool JSON format
        val result = LlmToolJsonTransformer.transform(toolInfo)

        // Parse the result to validate it
        val jsonParser = Json { prettyPrint = true }
        val resultJson = jsonParser.parseToJsonElement(result) as JsonObject

        // Expected JSON structure
        val expectedJson =
            """
            {
              "name": "add_compose_node_to_container",
              "description": "Adds a Compose UI component to a container node in the UI builder. This allows placing UI elements inside containers like Column, Row, or Box.",
              "input_schema": {
                "type": "object",
                "properties": {
                  "containerNodeId": {
                    "type": "string",
                    "description": "The ID of the container node where the component will be added. Must be a node that can contain other components."
                  },
                  "composeNodeYaml": {
                    "type": "string",
                    "description": "The YAML representation of the ComposeNode node to be added to the container."
                  },
                  "indexToDrop": {
                    "type": "number",
                    "description": "The position index where the component should be inserted in the container."
                  }
                },
                "required": ["containerNodeId", "composeNodeYaml", "indexToDrop"]
              }
            }
            """.trimIndent()

        val expectedJsonObj = jsonParser.parseToJsonElement(expectedJson) as JsonObject

        // Print both for comparison
        println("Result JSON:")
        println(result)
        println("\nExpected JSON:")
        println(expectedJson)

        // Verify the structure matches
        assertEquals(expectedJsonObj["name"], resultJson["name"])
        assertEquals(expectedJsonObj["description"], resultJson["description"])

        val expectedInputSchema = expectedJsonObj["input_schema"] as JsonObject
        val resultInputSchema = resultJson["input_schema"] as JsonObject

        assertEquals(expectedInputSchema["type"], resultInputSchema["type"])

        val expectedProperties = expectedInputSchema["properties"] as JsonObject
        val resultProperties = resultInputSchema["properties"] as JsonObject

        // Check each property
        for (key in expectedProperties.keys) {
            assertEquals(
                (expectedProperties[key] as JsonObject)["type"],
                (resultProperties[key] as JsonObject)["type"],
            )
            assertEquals(
                (expectedProperties[key] as JsonObject)["description"],
                (resultProperties[key] as JsonObject)["description"],
            )
        }
    }
}
