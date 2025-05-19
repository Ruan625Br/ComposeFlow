package io.composeflow.model.parameter

import io.composeflow.model.property.StringProperty
import io.composeflow.serializer.yamlSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.Assert
import org.junit.Test

class TopAppBarTraitTest {

    @Test
    fun serialize_deserialize() {
        val topAppBarParams = TopAppBarTrait(
            title = StringProperty.StringIntrinsicValue("test title"),
            topAppBarType = TopAppBarTypeWrapper.CenterAligned,
            scrollBehaviorWrapper = ScrollBehaviorWrapper.EnterAlways,
        )

        val encoded = yamlSerializer.encodeToString(topAppBarParams)
        val decoded = yamlSerializer.decodeFromString<TopAppBarTrait>(encoded)

        Assert.assertTrue(topAppBarParams.contentEquals(decoded))
    }
}
