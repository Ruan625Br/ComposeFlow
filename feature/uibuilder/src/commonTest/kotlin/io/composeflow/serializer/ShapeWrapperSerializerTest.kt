package io.composeflow.serializer

import androidx.compose.ui.unit.dp
import io.composeflow.model.parameter.wrapper.ShapeWrapper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert
import org.junit.Test

class ShapeWrapperSerializerTest {

    @Test
    fun rectangle() {
        verifySerializeDeserialize(ShapeWrapper.Rectangle)
    }

    @Test
    fun circle() {
        verifySerializeDeserialize(ShapeWrapper.Circle)
    }

    @Test
    fun roundedCorner() {
        verifySerializeDeserialize(ShapeWrapper.RoundedCorner(8.dp))
    }

    @Test
    fun cutCorner() {
        verifySerializeDeserialize(ShapeWrapper.CutCorner(16.dp))
    }

    private fun verifySerializeDeserialize(shapeWrapper: ShapeWrapper) {
        val encoded = yamlSerializer.encodeToString(shapeWrapper)
        val decoded = yamlSerializer.decodeFromString<ShapeWrapper>(encoded)

        Assert.assertEquals(shapeWrapper, decoded)
    }
}
