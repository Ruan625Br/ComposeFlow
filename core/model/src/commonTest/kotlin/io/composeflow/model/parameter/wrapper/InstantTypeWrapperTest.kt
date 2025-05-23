package io.composeflow.model.parameter.wrapper

import io.composeflow.cloud.storage.asDateString
import kotlin.test.assertEquals
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test

class InstantTypeWrapperTest {

    @Test
    fun testParseString() {
        val instant = Instant.parse("2024-07-15T00:00:00Z")
        val dateString = instant.asDateString(TimeZone.UTC)
        assertEquals("2024-07-15", dateString)
    }
}