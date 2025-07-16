package io.composeflow.model.parameter

import io.composeflow.model.property.StringProperty
import io.composeflow.serializer.yamlDefaultSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class ButtonTraitTest {
    @Test
    fun serialize_deserialize() {
        val buttonParams =
            ButtonTrait(
                textProperty = StringProperty.StringIntrinsicValue("button"),
            )

        val encoded = yamlDefaultSerializer.encodeToString(buttonParams)
        val decoded = yamlDefaultSerializer.decodeFromString<ButtonTrait>(encoded)

        assertEquals(buttonParams, decoded)
    }
}
