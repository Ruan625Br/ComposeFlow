package io.composeflow.cloud.storage

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.OffsetDateTime

fun OffsetDateTime.toKotlinxInstant(): Instant =
    Instant.fromEpochSeconds(this.toEpochSecond(), this.nano.toLong())

fun Instant.asDateString(): String {
    val date = toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${date.year}-${
        date.monthNumber.toString().padStart(2, '0')
    }-${date.dayOfMonth.toString().padStart(2, '0')}"
}