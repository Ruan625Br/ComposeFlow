package io.composeflow.ui.about

import io.composeflow.ui.popup.Library

object LibraryParser {
    fun parseLibrariesFromToml(tomlContent: String): List<Library> {
        val libraries = mutableListOf<Library>()
        val versionMap = mutableMapOf<String, String>()

        // First, parse the versions section
        var inVersionsSection = false
        var inLibrariesSection = false

        tomlContent.lines().forEach { line ->
            val trimmedLine = line.trim()

            when {
                trimmedLine == "[versions]" -> {
                    inVersionsSection = true
                    inLibrariesSection = false
                }

                trimmedLine == "[libraries]" -> {
                    inVersionsSection = false
                    inLibrariesSection = true
                }

                trimmedLine.startsWith("[") -> {
                    inVersionsSection = false
                    inLibrariesSection = false
                }

                inVersionsSection && trimmedLine.contains("=") -> {
                    val parts = trimmedLine.split("=").map { it.trim() }
                    if (parts.size == 2) {
                        val key = parts[0]
                        val value = parts[1].trim('"')
                        versionMap[key] = value
                    }
                }

                inLibrariesSection && trimmedLine.contains("=") && !trimmedLine.startsWith("#") -> {
                    val libraryName = trimmedLine.substringBefore("=").trim()
                    val libraryDef = trimmedLine.substringAfter("=").trim()

                    // Parse library definition
                    when {
                        libraryDef.startsWith("{") && libraryDef.endsWith("}") -> {
                            // Object notation
                            val content = libraryDef.substring(1, libraryDef.length - 1)
                            val parts = content.split(",").map { it.trim() }

                            var group = ""
                            var module = ""
                            var name = ""
                            var version = ""
                            var versionRef = ""

                            parts.forEach { part ->
                                when {
                                    part.startsWith("group") -> {
                                        group = part.substringAfter("=").trim().trim('"')
                                    }

                                    part.startsWith("module") -> {
                                        module = part.substringAfter("=").trim().trim('"')
                                    }

                                    part.startsWith("name") -> {
                                        name = part.substringAfter("=").trim().trim('"')
                                    }

                                    part.startsWith("version.ref") -> {
                                        versionRef = part.substringAfter("=").trim().trim('"')
                                    }

                                    part.startsWith("version") && !part.startsWith("version.ref") -> {
                                        version = part.substringAfter("=").trim().trim('"')
                                    }
                                }
                            }

                            // Construct the full artifact ID
                            val fullGroup =
                                if (module.isNotEmpty()) {
                                    module.substringBeforeLast(":")
                                } else if (group.isNotEmpty() && name.isNotEmpty()) {
                                    "$group:$name"
                                } else {
                                    ""
                                }

                            val artifactName =
                                if (module.isNotEmpty()) {
                                    module.substringAfterLast(":")
                                } else {
                                    name
                                }

                            val finalVersion =
                                if (versionRef.isNotEmpty()) {
                                    versionMap[versionRef] ?: "unknown"
                                } else {
                                    version
                                }

                            if (fullGroup.isNotEmpty() && finalVersion.isNotEmpty()) {
                                libraries.add(
                                    Library(
                                        name = artifactName.ifEmpty { libraryName },
                                        group = fullGroup.substringBeforeLast(":"),
                                        version = finalVersion,
                                    ),
                                )
                            }
                        }

                        libraryDef.contains(":") -> {
                            // String notation like "com.example:library:1.0.0"
                            val cleanDef = libraryDef.trim('"')
                            val parts = cleanDef.split(":")
                            if (parts.size >= 3) {
                                libraries.add(
                                    Library(
                                        name = parts[1],
                                        group = parts[0],
                                        version = parts[2],
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }

        return libraries.sortedBy { "${it.group}:${it.name}" }
    }
}
