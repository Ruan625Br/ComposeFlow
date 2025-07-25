package io.composeflow.serializer

import com.charleskorn.kaml.YamlPath
import kotlinx.serialization.SerializationException

/**
 * Enhanced serialization exception that includes YAML location information.
 * This helps pinpoint the exact location where serialization errors occur.
 */
class YamlLocationAwareException(
    message: String,
    val line: Int? = null,
    val column: Int? = null,
    val yamlPath: YamlPath? = null,
    val yamlContent: String? = null,
    cause: Throwable? = null,
) : SerializationException(message, cause) {
    override val message: String
        get() {
            val baseMessage = super.message ?: "Serialization error"
            val locationInfo =
                buildString {
                    if (line != null && column != null) {
                        append(" at line $line, column $column")
                    }
                    if (yamlPath != null) {
                        append(" (path: ${yamlPath.toHumanReadableString()})")
                    }
                }

            val contextInfo =
                yamlContent?.let { content ->
                    if (line != null && line > 0) {
                        val lines = content.lines()
                        if (line <= lines.size) {
                            val problemLine = lines[line - 1]
                            val context =
                                buildString {
                                    appendLine()
                                    appendLine("Context:")
                                    if (line > 1) {
                                        appendLine("${line - 1}: ${lines[line - 2]}")
                                    }
                                    append("$line: $problemLine")
                                    if (column != null && column > 0) {
                                        appendLine()
                                        append(" ".repeat("$line: ".length + column - 1))
                                        append("^")
                                    }
                                    if (line < lines.size) {
                                        appendLine()
                                        appendLine("${line + 1}: ${lines[line]}")
                                    }
                                }
                            context
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

            return buildString {
                append(baseMessage)
                append(locationInfo)
                contextInfo?.let { append(it) }
            }
        }
}
