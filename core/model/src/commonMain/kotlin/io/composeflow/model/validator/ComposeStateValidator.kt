package io.composeflow.model.validator

import com.squareup.kotlinpoet.CodeBlock
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.project.Project

interface ComposeStateValidator {
    fun asCodeBlock(
        project: Project,
        context: GenerationContext,
    ): CodeBlock
}
