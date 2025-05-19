package io.composeflow.model.property

import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.enumwrapper.TextDecorationWrapper
import io.composeflow.model.project.Project
import io.composeflow.serializer.yamlSerializer
import io.composeflow.trimForCompare
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals

class AssignablePropertyTest {

    @Test
    fun testConditionalBlock_onlyIfElse() {
        val conditional = ConditionalProperty(
            defaultValue = EnumProperty(TextDecorationWrapper.None),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(),
                thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = EnumProperty(value = TextDecorationWrapper.Underline),
            ),
        )

        val generated =
            conditional.generateCodeBlock(Project(), context = GenerationContext(), dryRun = false)
        assertEquals(
            """
                if (false) {
                  androidx.compose.ui.text.style.TextDecoration.LineThrough
                } else {
                  androidx.compose.ui.text.style.TextDecoration.Underline
                }
            """.trimForCompare(),
            generated.toString().trimForCompare(),
        )
    }

    @Test
    fun testConditionalBlock_includingElseIfBlocks() {
        val conditional = ConditionalProperty(
            defaultValue = EnumProperty(TextDecorationWrapper.None),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(),
                thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
            ),
            elseIfBlocks = mutableListOf(
                ConditionalProperty.IfThenBlock(
                    ifExpression = BooleanProperty.BooleanIntrinsicValue(true),
                    thenValue = EnumProperty(value = TextDecorationWrapper.None),
                ),
                ConditionalProperty.IfThenBlock(
                    ifExpression = BooleanProperty.BooleanIntrinsicValue(false),
                    thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
                ),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = EnumProperty(value = TextDecorationWrapper.Underline),
            ),
        )

        val generated =
            conditional.generateCodeBlock(Project(), context = GenerationContext(), dryRun = false)
        assertEquals(
            """
                if (false) {
                  androidx.compose.ui.text.style.TextDecoration.LineThrough
                } else if (true) {
                  androidx.compose.ui.text.style.TextDecoration.None
                } else if (false) {
                  androidx.compose.ui.text.style.TextDecoration.LineThrough
                } else {
                  androidx.compose.ui.text.style.TextDecoration.Underline
                }
            """.trimForCompare(),
            generated.toString().trimForCompare(),
        )
    }

    @Test
    fun testSerialize_conditionalProperty() {
        val conditional = ConditionalProperty(
            defaultValue = EnumProperty(TextDecorationWrapper.None),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(),
                thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
            ),
            elseIfBlocks = mutableListOf(
                ConditionalProperty.IfThenBlock(
                    ifExpression = BooleanProperty.BooleanIntrinsicValue(true),
                    thenValue = EnumProperty(value = TextDecorationWrapper.None),
                ),
                ConditionalProperty.IfThenBlock(
                    ifExpression = BooleanProperty.BooleanIntrinsicValue(false),
                    thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
                ),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = EnumProperty(value = TextDecorationWrapper.Underline),
            ),
        )

        val encoded = yamlSerializer.encodeToString(conditional)
        val decoded = yamlSerializer.decodeFromString<ConditionalProperty>(encoded)
        assertEquals(conditional, decoded)
    }
}
