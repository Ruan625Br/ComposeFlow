package io.composeflow.model.project.theme

import io.composeflow.kotlinpoet.FileSpecWithDirectory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ThemeHolder")
class ThemeHolder(
    val colorSchemeHolder: ColorSchemeHolder = ColorSchemeHolder(),
    val fontHolder: FontHolder = FontHolder(),
) {
    fun generateThemeFiles(): List<FileSpecWithDirectory> =
        listOf(colorSchemeHolder.generateColorFile(), fontHolder.generateFontFile())
            .map {
                FileSpecWithDirectory(it)
            }
}

fun ThemeHolder.copyContents(other: ThemeHolder) {
    colorSchemeHolder.copyContents(other.colorSchemeHolder)
    fontHolder.copyContents(other.fontHolder)
}
