package io.composeflow.model.parameter

import io.composeflow.model.parameter.wrapper.AlignmentWrapper
import io.composeflow.serializer.yamlDefaultSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class BoxTraitTest {
    @Test
    fun serialize_deserialize() {
        val boxParams = BoxTrait(contentAlignment = AlignmentWrapper.Center)

        val encoded = yamlDefaultSerializer.encodeToString(boxParams)
        val decoded = yamlDefaultSerializer.decodeFromString<BoxTrait>(encoded)

        assertEquals(boxParams, decoded)
    }
}
