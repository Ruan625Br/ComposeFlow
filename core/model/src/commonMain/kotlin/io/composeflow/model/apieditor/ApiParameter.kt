package io.composeflow.model.apieditor

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import io.composeflow.asVariableName
import io.composeflow.model.project.ParameterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

@Serializable
sealed interface ApiProperty {
    fun asCodeBlock(): CodeBlock

    fun asStringValue(): String

    @Serializable
    @SerialName("ApiPropertyIntrinsicValue")
    data class IntrinsicValue(
        val value: String = "",
    ) : ApiProperty {
        override fun asCodeBlock(): CodeBlock = CodeBlock.of("\"\"\"$value\"\"\"")

        override fun asStringValue(): String = value
    }

    @Serializable
    @SerialName("ApiPropertyStringParameter")
    data class StringParameter(
        val parameterId: ParameterId = Uuid.random().toString(),
        val name: String,
        val defaultValue: String = "",
    ) : ApiProperty {
        @Transient
        val variableName = name.asVariableName()

        override fun asCodeBlock(): CodeBlock = CodeBlock.of(name)

        override fun asStringValue(): String = defaultValue

        fun generateArgumentParameterSpec(): ParameterSpec =
            ParameterSpec
                .builder(
                    name = name,
                    String::class,
                ).defaultValue("\"$defaultValue\"")
                .build()
    }
}
