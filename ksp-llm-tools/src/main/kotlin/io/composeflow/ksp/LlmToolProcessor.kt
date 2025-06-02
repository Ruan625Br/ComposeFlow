package io.composeflow.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LlmToolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("LlmToolProcessor is processing...")
        val symbols = resolver.getSymbolsWithAnnotation(LlmTool::class.qualifiedName!!)
        logger.info("Found ${symbols.count()} symbols with @LlmTool annotation")
        val unprocessed = symbols.filter { !it.validate() }.toList()

        symbols
            .filter { it is KSFunctionDeclaration && it.validate() }
            .forEach {
                logger.info("Processing function: ${(it as KSFunctionDeclaration).simpleName.asString()}")
                processFunction(it)
            }

        return unprocessed
    }

    private fun processFunction(function: KSFunctionDeclaration) {
        val annotation = function.annotations.find {
            it.shortName.asString() == "LlmTool"
        } ?: return

        val packageName = function.containingFile?.packageName?.asString() ?: ""
        val className = function.parentDeclaration?.simpleName?.asString() ?: ""
        val functionName = function.simpleName.asString()

        // Extract annotation values
        val name = annotation.arguments.find { it.name?.asString() == "name" }?.value as? String
            ?: functionName
        val description =
            annotation.arguments.find { it.name?.asString() == "description" }?.value as? String
                ?: ""
        val category =
            annotation.arguments.find { it.name?.asString() == "category" }?.value as? String
                ?: ""

        // Process parameters
        val parameters = function.parameters.map { param ->
            val paramAnnotation = param.annotations.find {
                it.shortName.asString() == "LlmParam"
            }

            val paramDescription = paramAnnotation?.arguments?.find {
                it.name?.asString() == "description"
            }?.value as? String ?: ""

            val required = paramAnnotation?.arguments?.find {
                it.name?.asString() == "required"
            }?.value as? Boolean ?: true

            val defaultValue = paramAnnotation?.arguments?.find {
                it.name?.asString() == "defaultValue"
            }?.value as? String ?: ""

            val paramType = param.type.resolve()
            val typeDeclaration = paramType.declaration

            // Check if the type has a @SerialName annotation
            val serialNameAnnotation = typeDeclaration.annotations.find {
                it.shortName.asString() == "SerialName" ||
                        it.shortName.asString() == "kotlinx.serialization.SerialName"
            }

            // Get the serial name value if available, otherwise use the qualified name
            val typeName = if (serialNameAnnotation != null) {
                // Extract the value from the annotation
                serialNameAnnotation.arguments.firstOrNull()?.value as? String
                    ?: typeDeclaration.simpleName.asString()
            } else {
                // Fall back to the qualified name or simple name
                typeDeclaration.qualifiedName?.asString()
                    ?: typeDeclaration.simpleName.asString()
            }

            ParameterInfo(
                name = param.name?.asString() ?: "",
                type = typeName,
                description = paramDescription,
                required = required,
                defaultValue = defaultValue
            )
        }

        // Get return type
        val returnType = function.returnType?.resolve()
        val returnTypeName = returnType?.declaration?.qualifiedName?.asString() ?: "void"

        // Create tool info
        val toolInfo = LlmToolInfo(
            name = name,
            description = description,
            category = category,
            className = className,
            functionName = functionName,
            packageName = packageName,
            parameters = parameters,
            returnType = returnTypeName
        )

        // Generate JSON file
        generateJsonFile(toolInfo)
    }

    private fun generateJsonFile(toolInfo: LlmToolInfo) {
        val outputDir = options["llmToolsOutputDir"] ?: "build/generated/llm-tools"
        logger.info("Output directory: $outputDir")
        val outputDirFile = File(outputDir)
        outputDirFile.mkdirs()
        logger.info("Output directory exists: ${outputDirFile.exists()}, isDirectory: ${outputDirFile.isDirectory}")
        
        // Generate the original format JSON
        val fileName = "${toolInfo.name.replace(" ", "_")}_tool"
        val fileContent = json.encodeToString(toolInfo)
        val file = outputDirFile.resolve("$fileName.json")
        logger.info("Writing to file: ${file.absolutePath}")

        try {
            file.writeText(fileContent)
            logger.info("Successfully generated LLM tool JSON for ${toolInfo.name} at ${file.absolutePath}")
            
            // Generate the MCP tool format JSON
            val mcpFileName = "${toolInfo.name.replace(" ", "_")}_mcp_tool"
            val mcpFileContent = LlmToolJsonTransformer.transform(toolInfo)
            val mcpFile = outputDirFile.resolve("$mcpFileName.json")
            logger.info("Writing MCP tool JSON to file: ${mcpFile.absolutePath}")
            
            mcpFile.writeText(mcpFileContent)
            logger.info("Successfully generated MCP tool JSON for ${toolInfo.name} at ${mcpFile.absolutePath}")
        } catch (e: Exception) {
            logger.error("Error writing JSON file: ${e.message}")
            e.printStackTrace()
        }
    }
}

class LlmToolProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return LlmToolProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}

@Serializable
data class LlmToolInfo(
    val name: String,
    val description: String,
    val category: String,
    val className: String,
    val functionName: String,
    val packageName: String,
    val parameters: List<ParameterInfo>,
    val returnType: String
)

@Serializable
data class ParameterInfo(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean,
    val defaultValue: String
)