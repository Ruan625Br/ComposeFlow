package io.composeflow.model.parameter

import androidx.compose.ui.unit.Dp
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.project.Project
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.PropertyContainer
import io.composeflow.model.type.ComposeFlowType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class DividerTrait(
    @Transient
    open val thickness: Dp? = null,
    @Transient
    open val color: AssignableProperty? = null,
) : ComposeTrait {

    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Basic)
    override fun getPropertyContainers(): List<PropertyContainer> {
        return listOf(
            PropertyContainer("Color", color, ComposeFlowType.Color()),
        )
    }

    fun generateParamsCode(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        thickness?.let {
            codeBlockBuilder.addStatement(
                "thickness = ${it.value.toInt()}.%M,",
                MemberName("androidx.compose.ui.unit", "dp"),
            )
        }
        color?.let {
            codeBlockBuilder.add("color = ")
            codeBlockBuilder.add(
                it.transformedCodeBlock(
                    project,
                    context,
                    ComposeFlowType.Color(),
                    dryRun = dryRun
                )
            )
            codeBlockBuilder.addStatement(",")
        }
        return codeBlockBuilder.build()
    }
}

