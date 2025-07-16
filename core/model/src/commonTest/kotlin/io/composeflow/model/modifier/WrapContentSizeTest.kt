package io.composeflow.model.modifier

import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.parameter.wrapper.AlignmentWrapper
import io.composeflow.model.project.Project
import io.composeflow.serializer.yamlDefaultSerializer
import io.composeflow.trimForCompare
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals

class WrapContentSizeTest {
    @Test
    fun toComposeCode_default() {
        val modifierList = listOf(ModifierWrapper.WrapContentSize())
        val code =
            modifierList.generateCode(Project(), context = GenerationContext(), dryRun = false)

        assertEquals(
            """modifier = androidx.compose.ui.Modifier.androidx.compose.foundation.layout.wrapContentSize(),""".trimForCompare(),
            code.build().toString().trimForCompare(),
        )
    }

    @Test
    fun toComposeCode_non_default_args() {
        val modifierList =
            listOf(
                ModifierWrapper.WrapContentSize(
                    align = AlignmentWrapper.BottomCenter,
                    unbounded = true,
                ),
            )
        val code =
            modifierList.generateCode(Project(), context = GenerationContext(), dryRun = false)

        assertEquals(
            """modifier = androidx.compose.ui.Modifier.androidx.compose.foundation.layout.wrapContentSize(
                 align = androidx.compose.ui.Alignment.BottomCenter,
                 unbounded = true
               ),""".trimForCompare(),
            code.build().toString().trimForCompare(),
        )
    }

    @Test
    fun serialize_verify_restored_instance() {
        val size = ModifierWrapper.WrapContentSize()

        val encodedString = yamlDefaultSerializer.encodeToString(size)
        val decoded =
            yamlDefaultSerializer.decodeFromString<ModifierWrapper.WrapContentSize>(encodedString)
        assertEquals(size, decoded)
    }
}
