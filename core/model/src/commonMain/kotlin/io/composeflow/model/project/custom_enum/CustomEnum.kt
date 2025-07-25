@file:Suppress("ktlint:standard:package-name")

package io.composeflow.model.project.custom_enum

import androidx.compose.runtime.mutableStateListOf
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import io.composeflow.asClassName
import io.composeflow.model.project.Project
import io.composeflow.serializer.FallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

const val ENUM_PACKAGE = "custom_enum"

typealias CustomEnumId = String

@Serializable
@SerialName("CustomEnum")
data class CustomEnum(
    val customEnumId: CustomEnumId = Uuid.random().toString(),
    private val name: String,
    @Serializable(FallbackMutableStateListSerializer::class)
    val values: MutableList<String> = mutableStateListOf(),
) {
    @Transient
    val enumName = name.asClassName()

    fun asKotlinPoetClassName(project: Project): ClassName = ClassName("${project.packageName}.$ENUM_PACKAGE", enumName)

    fun generateCustomEnumSpec(): TypeSpec? {
        if (values.isEmpty()) return null
        val typeSpecBuilder = TypeSpec.enumBuilder(enumName)
        values.forEach { value ->
            typeSpecBuilder.addEnumConstant(name = value)
        }
        return typeSpecBuilder.build()
    }
}
