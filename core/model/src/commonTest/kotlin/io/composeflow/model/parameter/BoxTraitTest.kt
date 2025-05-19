package io.composeflow.model.parameter

import io.composeflow.model.parameter.wrapper.AlignmentWrapper
import io.composeflow.serializer.yamlSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert.assertEquals
import org.junit.Test

class BoxTraitTest {

    @Test
    fun serialize_deserialize() {
        val boxParams = BoxTrait(contentAlignment = AlignmentWrapper.Center)

        val encoded = yamlSerializer.encodeToString(boxParams)
        val decoded = yamlSerializer.decodeFromString<BoxTrait>(encoded)

        assertEquals(boxParams, decoded)
    }
}
