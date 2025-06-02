# KSP LLM Tools

This module provides a Kotlin Symbol Processing (KSP) implementation for generating JSON documentation from Kotlin functions that are intended to be exposed as tools to Large Language Models (LLMs).

## Overview

The KSP LLM Tools module allows you to annotate Kotlin functions with `@LlmTool` and parameters with `@LlmParam` to generate structured JSON documentation. This documentation can then be used to expose these functions as tools to LLMs like OpenAI's GPT models.

## Features

- Annotate Kotlin functions to expose them as LLM tools
- Generate JSON documentation for annotated functions
- Convert the documentation to formats suitable for LLM APIs (Claude and OpenAI)
- Utility classes for loading and using the generated documentation

## Usage

### 1. Add the plugin to your module

```kotlin
plugins {
    // Other plugins
    id("io.compose.flow.llm.tools")
}
```

### 2. Annotate your functions

```kotlin
import io.composeflow.ksp.LlmTool
import io.composeflow.ksp.LlmParam

class MyViewModel {
    @LlmTool(
        name = "add_item",
        description = "Adds a new item to the list"
    )
    fun addItem(
        @LlmParam(description = "The name of the item")
        name: String,
        @LlmParam(description = "The quantity of the item", required = false, defaultValue = "1")
        quantity: Int = 1
    ) {
        // Implementation
    }
}
```

### 3. Build your project

When you build your project, the KSP processor will generate JSON files in the `build/generated/llm-tools` directory.

### 4. Load and use the generated documentation

```kotlin
import io.composeflow.ksp.LlmToolsLoader
import java.io.File

// Load the tools
val loader = LlmToolsLoader()
val tools = loader.loadTools(File("build/generated/llm-tools"))

// Convert to Claude format
val claudeToolsMap = loader.convertToClaudeFormat(tools)

// Use with Claude API
val claudeClient = ClaudeClient(apiKey)
val response = claudeClient.createMessage {
    model = "claude-3-opus-20240229"
    messages = listOf(
        Message(role = "user", content = "Add a new item called 'Apple' with quantity 5.")
    )
    tools = claudeToolsMap
}

// Handle the tool call
val toolCall = response.content[0].toolUse
if (toolCall.name == "add_item") {
    val args = toolCall.input
    // Parse arguments and call the actual function
    // ...
}
```

## Generated JSON Format

The generated JSON files have the following structure:

```json
{
  "name": "add_item",
  "description": "Adds a new item to the list",
  "category": "",
  "className": "MyViewModel",
  "functionName": "addItem",
  "packageName": "com.example",
  "parameters": [
    {
      "name": "name",
      "type": "kotlin.String",
      "description": "The name of the item",
      "required": true,
      "defaultValue": ""
    },
    {
      "name": "quantity",
      "type": "kotlin.Int",
      "description": "The quantity of the item",
      "required": false,
      "defaultValue": "1"
    }
  ],
  "returnType": "kotlin.Unit"
}
```

## Claude API Format

The `convertToClaudeFormat` method converts the tool definitions to the format required by Claude's API:

```json
{
  "tools": [
    {
      "name": "add_item",
      "description": "Adds a new item to the list",
      "input_schema": {
        "type": "object",
        "properties": {
          "name": {
            "type": "string",
            "description": "The name of the item"
          },
          "quantity": {
            "type": "integer",
            "description": "The quantity of the item"
          }
        },
        "required": ["name"]
      }
    }
  ]
}
```

This format matches Claude's expected tools schema, where tools are provided as an array under the "tools" key.

## OpenAI Function Format

The `convertToOpenAIFormat` method is also available if you need to use OpenAI's function calling API:

```json
{
  "type": "function",
  "function": {
    "name": "add_item",
    "description": "Adds a new item to the list",
    "parameters": {
      "type": "object",
      "properties": {
        "name": {
          "type": "string",
          "description": "The name of the item"
        },
        "quantity": {
          "type": "integer",
          "description": "The quantity of the item"
        }
      },
      "required": ["name"]
    }
  }
}
```

## Generated JSON Files

When you build a module that uses the KSP LLM tools processor, two types of JSON files will be generated in the following location:

```
<module>/build/generated/llm-tools/<tool_name>_tool.json       # Original format
<module>/build/generated/llm-tools/<tool_name>_mcp_tool.json   # MCP tool format
```

For example, when building the `feature:uibuilder` module, the JSON files will be generated at:

```
feature/uibuilder/build/generated/llm-tools/add_compose_node_to_container_tool.json
feature/uibuilder/build/generated/llm-tools/add_compose_node_to_container_mcp_tool.json
```

### MCP Tool Format

The MCP (Model Context Protocol) tool format is a simplified JSON format that follows the schema expected by MCP servers. This format is automatically generated alongside the original format:

```json
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
```

Key differences from the original format:
- Excludes internal metadata like className, functionName, packageName, and returnType
- Transforms parameters into an input_schema object with properties
- Simplifies Kotlin types to JSON schema types (e.g., kotlin.String → string, kotlin.Int → number)
- Moves required parameters to a top-level array in the input_schema
- Excludes the "project" parameter which is typically an internal dependency

### Type Names and @SerialName Support

The KSP processor supports the `@SerialName` annotation for parameter types. If a type is annotated with `@SerialName`, the processor will use that name instead of the fully qualified name in the generated JSON.

For example, if you have a class:

```kotlin
@Serializable
@SerialName("ComposeNode")
class ComposeNode { ... }
```

And a method parameter:

```kotlin
@LlmTool(...)
fun someMethod(
    @LlmParam(description = "...")
    node: ComposeNode
) { ... }
```

The generated JSON will use "ComposeNode" as the type name instead of the fully qualified name:

```json
{
  "parameters": [
    {
      "name": "node",
      "type": "ComposeNode",
      "description": "...",
      "required": true,
      "defaultValue": ""
    }
  ]
}
```

This makes the generated JSON more readable and easier to use with LLMs.

## Running the Example

To run the example that demonstrates how to load and format the generated tool definitions:

```bash
# First, build the project to generate the tool definitions
./gradlew :feature:uibuilder:build

# Then run the example
./gradlew :ksp-llm-tools:runExample
```

The example will:
1. Load the generated tool definitions from the `build/generated/llm-tools` directory
2. Print information about each tool, including its name, description, and parameters
3. Convert the tools to Claude's API format and print the result
4. Show an example of how to use the tools with Claude's API

## License

This project is licensed under the same license as the ComposeFlow project.