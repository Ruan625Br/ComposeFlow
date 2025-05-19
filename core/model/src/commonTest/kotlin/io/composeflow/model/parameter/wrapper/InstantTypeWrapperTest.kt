package io.composeflow.model.parameter.wrapper

import io.composeflow.cloud.storage.asDateString
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test

class InstantTypeWrapperTest {

    @Test
    fun testParseString() {
        val parsedInstant = LocalDate.parse("2024-07-15").atStartOfDayIn(TimeZone.UTC)
        assertEquals("2024-07-15", parsedInstant.asDateString())
    }
}