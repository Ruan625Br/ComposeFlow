package io.composeflow.model.project.theme

import com.squareup.kotlinpoet.FileSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ThemeHolder")
class ThemeHolder(
    val colorSchemeHolder: ColorSchemeHolder = ColorSchemeHolder(),
    val fontHolder: FontHolder = FontHolder(),
) {
    fun generateThemeFiles(): List<FileSpec> = listOf(colorSchemeHolder.generateColorFile(), fontHolder.generateFontFile())
}

fun ThemeHolder.copyContents(other: ThemeHolder) {
    colorSchemeHolder.copyContents(other.colorSchemeHolder)
    fontHolder.copyContents(other.fontHolder)
}
