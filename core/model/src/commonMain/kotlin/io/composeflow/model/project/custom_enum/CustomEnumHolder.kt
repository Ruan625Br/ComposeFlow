@file:Suppress("ktlint:standard:package-name")

package io.composeflow.model.project.custom_enum

import com.squareup.kotlinpoet.FileSpec
import io.composeflow.formatter.suppressRedundantVisibilityModifier
import io.composeflow.model.project.Project
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.FallbackMutableStateListSerializer
import io.composeflow.util.generateUniqueName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CustomEnumHolder")
data class CustomEnumHolder(
    @Serializable(with = FallbackMutableStateListSerializer::class)
    val enumList: MutableList<CustomEnum> = mutableStateListEqualsOverrideOf(),
) {
    fun generateEnumFiles(project: Project): List<FileSpec?> =
        enumList.map { enum ->
            enum.generateCustomEnumSpec()?.let {
                val fileBuilder =
                    FileSpec
                        .builder("${project.packageName}.$EnumPackage", enum.enumName)
                        .addType(it)
                fileBuilder.suppressRedundantVisibilityModifier()
                fileBuilder.build()
            }
        }

    fun newCustomEnum(name: String): CustomEnum {
        val newName =
            generateUniqueName(initial = name, existing = enumList.map { it.enumName }.toSet())
        return CustomEnum(name = newName)
    }
}
