package io.composeflow.model.parameter

import io.composeflow.model.parameter.wrapper.AlignmentVerticalWrapper
import io.composeflow.model.parameter.wrapper.ArrangementHorizontalWrapper
import io.composeflow.serializer.yamlDefaultSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class RowTraitTest {
    @Test
    fun serialize_deserialize() {
        val rowParams =
            RowTrait(
                horizontalArrangement = ArrangementHorizontalWrapper.End,
                verticalAlignment = AlignmentVerticalWrapper.CenterVertically,
            )

        val encoded = yamlDefaultSerializer.encodeToString(rowParams)
        val decoded = yamlDefaultSerializer.decodeFromString<RowTrait>(encoded)

        assertEquals(rowParams, decoded)
    }
}
