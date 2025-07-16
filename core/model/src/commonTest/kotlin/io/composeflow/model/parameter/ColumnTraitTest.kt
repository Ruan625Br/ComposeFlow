package io.composeflow.model.parameter

import io.composeflow.model.parameter.wrapper.AlignmentHorizontalWrapper
import io.composeflow.model.parameter.wrapper.ArrangementVerticalWrapper
import io.composeflow.serializer.yamlDefaultSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class ColumnTraitTest {
    @Test
    fun serialize_deserialize() {
        val columnParams =
            ColumnTrait(
                verticalArrangementWrapper = ArrangementVerticalWrapper.Bottom,
                horizontalAlignmentWrapper = AlignmentHorizontalWrapper.CenterHorizontally,
            )

        val encoded = yamlDefaultSerializer.encodeToString(columnParams)
        val decoded = yamlDefaultSerializer.decodeFromString<ColumnTrait>(encoded)

        assertEquals(columnParams, decoded)
    }
}
