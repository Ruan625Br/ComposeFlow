package io.composeflow.model.property

import androidx.compose.runtime.mutableStateOf
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

    // Tests for ConditionalProperty.getAssignableProperties() method
    @Test
    fun testConditionalProperty_getAssignableProperties_simple() {
        val conditional = ConditionalProperty(
            defaultValue = EnumProperty(TextDecorationWrapper.None),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(true),
                thenValue = EnumProperty(value = TextDecorationWrapper.LineThrough),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = EnumProperty(value = TextDecorationWrapper.Underline),
            ),
        )

        val assignableProperties = conditional.getAssignableProperties()
        // Should contain: defaultValue, ifExpression, thenValue, elseBlock.value
        assertEquals(4, assignableProperties.size)
        
        // Check that all properties are present
        val enumProperties = assignableProperties.filterIsInstance<EnumProperty>()
        val booleanProperties = assignableProperties.filterIsInstance<BooleanProperty.BooleanIntrinsicValue>()
        
        assertEquals(3, enumProperties.size) // defaultValue, thenValue, elseBlock.value
        assertEquals(1, booleanProperties.size) // ifExpression
        assertEquals(true, booleanProperties[0].value)
    }

    @Test
    fun testConditionalProperty_getAssignableProperties_withElseIf() {
        val conditional = ConditionalProperty(
            defaultValue = EnumProperty(TextDecorationWrapper.None),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(false),
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

        val assignableProperties = conditional.getAssignableProperties()
        // Should contain: defaultValue, ifExpression, thenValue, 2 elseIf expressions, 2 elseIf values, elseBlock.value
        assertEquals(8, assignableProperties.size)
        
        val enumProperties = assignableProperties.filterIsInstance<EnumProperty>()
        val booleanProperties = assignableProperties.filterIsInstance<BooleanProperty.BooleanIntrinsicValue>()
        
        assertEquals(5, enumProperties.size) // defaultValue, thenValue, 2 elseIf values, elseBlock.value
        assertEquals(3, booleanProperties.size) // ifExpression + 2 elseIf expressions
    }

    @Test
    fun testConditionalProperty_getAssignableProperties_withTransformers() {
        // Create properties with transformers
        val ifExpression = BooleanProperty.BooleanIntrinsicValue(true)
        ifExpression.propertyTransformers.add(FromBoolean.ToBoolean.ToggleValue)
        
        val thenValue = StringProperty.StringIntrinsicValue("then_value")
        thenValue.propertyTransformers.add(
            FromString.ToString.AddBefore(mutableStateOf(StringProperty.StringIntrinsicValue("prefix_")))
        )
        
        val elseValue = StringProperty.StringIntrinsicValue("else_value")
        elseValue.propertyTransformers.add(
            FromString.ToString.AddAfter(mutableStateOf(StringProperty.StringIntrinsicValue("_suffix")))
        )

        val conditional = ConditionalProperty(
            defaultValue = StringProperty.StringIntrinsicValue("default"),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = ifExpression,
                thenValue = thenValue,
            ),
            elseBlock = ConditionalProperty.ElseBlock(value = elseValue),
        )

        val assignableProperties = conditional.getAssignableProperties()
        // Should contain: 
        // - defaultValue
        // - ifExpression, thenValue, elseValue (from conditional properties)
        // - prefix_ (from thenValue transformer)
        // - _suffix (from elseValue transformer)
        assertEquals(6, assignableProperties.size)
        
        val stringProperties = assignableProperties.filterIsInstance<StringProperty.StringIntrinsicValue>()
        val booleanProperties = assignableProperties.filterIsInstance<BooleanProperty.BooleanIntrinsicValue>()
        
        assertEquals(5, stringProperties.size) // default, then_value, else_value, prefix_, _suffix
        assertEquals(1, booleanProperties.size) // ifExpression
        
        // Verify specific values
        val stringValues = stringProperties.map { it.value }.toSet()
        assertEquals(setOf("default", "then_value", "else_value", "prefix_", "_suffix"), stringValues)
    }

    @Test
    fun testConditionalProperty_getAssignableProperties_nested() {
        // Create a nested conditional property
        val nestedConditional = ConditionalProperty(
            defaultValue = StringProperty.StringIntrinsicValue("nested_default"),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(false),
                thenValue = StringProperty.StringIntrinsicValue("nested_then"),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = StringProperty.StringIntrinsicValue("nested_else"),
            ),
        )

        val conditional = ConditionalProperty(
            defaultValue = StringProperty.StringIntrinsicValue("outer_default"),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(true),
                thenValue = nestedConditional,
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = StringProperty.StringIntrinsicValue("outer_else"),
            ),
        )

        val assignableProperties = conditional.getAssignableProperties()
        // Should contain all properties from outer conditional + all properties from nested conditional
        // Outer: outer_default, ifExpression(true), nestedConditional, outer_else
        // Nested (from nestedConditional): nested_default, ifExpression(false), nested_then, nested_else
        assertEquals(8, assignableProperties.size)
        
        val stringProperties = assignableProperties.filterIsInstance<StringProperty.StringIntrinsicValue>()
        val booleanProperties = assignableProperties.filterIsInstance<BooleanProperty.BooleanIntrinsicValue>()
        val conditionalProperties = assignableProperties.filterIsInstance<ConditionalProperty>()
        
        assertEquals(5, stringProperties.size) // All string values (not 6 because nested_default is included in the nested conditional)
        assertEquals(2, booleanProperties.size) // Both boolean expressions
        assertEquals(1, conditionalProperties.size) // The nested conditional itself
        
        // Verify string values
        val stringValues = stringProperties.map { it.value }.toSet()
        assertEquals(setOf("outer_default", "outer_else", "nested_default", "nested_then", "nested_else"), stringValues)
    }

    @Test
    fun testConditionalProperty_getAssignableProperties_withPropertyTransformers() {
        // Create a conditional property that itself has property transformers
        val conditional = ConditionalProperty(
            defaultValue = StringProperty.StringIntrinsicValue("default"),
            ifThen = ConditionalProperty.IfThenBlock(
                ifExpression = BooleanProperty.BooleanIntrinsicValue(true),
                thenValue = StringProperty.StringIntrinsicValue("then"),
            ),
            elseBlock = ConditionalProperty.ElseBlock(
                value = StringProperty.StringIntrinsicValue("else"),
            ),
        )
        
        // Add transformer to the conditional property itself
        conditional.propertyTransformers.add(
            FromString.ToString.AddAfter(mutableStateOf(StringProperty.StringIntrinsicValue("_added")))
        )

        val assignableProperties = conditional.getAssignableProperties()
        // Should contain:
        // - From transformer: _added
        // - From conditional: default, ifExpression(true), then, else
        assertEquals(5, assignableProperties.size)
        
        val stringProperties = assignableProperties.filterIsInstance<StringProperty.StringIntrinsicValue>()
        val booleanProperties = assignableProperties.filterIsInstance<BooleanProperty.BooleanIntrinsicValue>()
        
        assertEquals(4, stringProperties.size) // default, then, else, _added
        assertEquals(1, booleanProperties.size) // ifExpression
        
        // Verify that transformer property is included
        val stringValues = stringProperties.map { it.value }.toSet()
        assertEquals(setOf("default", "then", "else", "_added"), stringValues)
    }
}
