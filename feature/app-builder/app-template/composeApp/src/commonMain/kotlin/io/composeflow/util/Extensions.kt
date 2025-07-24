@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.composeflow.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.JsonElement
import kotlin.time.Instant

fun selectString(
    jsonElement: JsonElement,
    jsonPath: String,
    replaceQuotation: Boolean = false,
): String {
    val extracted = JsonPath.path(jsonPath).getOrNull(jsonElement).toString()
    return if (replaceQuotation) {
        extracted.replace("\"", "")
    } else {
        extracted
    }
}

inline fun <reified T> toRoute(backStackEntry: NavBackStackEntry): T = backStackEntry.toRoute()

@Composable
fun String.toRichHtmlString(): AnnotatedString {
    val state = rememberRichTextState()

    LaunchedEffect(this) {
        state.setHtml(this@toRichHtmlString)
    }

    return state.annotatedString
}

fun Instant.setHour(
    newHour: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Instant {
    val localDateTime = this.toLocalDateTime(timeZone)
    val updatedDateTime =
        localDateTime
            .setHour(newHour)
    return updatedDateTime.toInstant(timeZone)
}

fun Instant.setMinute(
    newMinute: Int,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Instant {
    val localDateTime = this.toLocalDateTime(timeZone)
    val updatedDateTime =
        localDateTime
            .setMinute(newMinute)
    return updatedDateTime.toInstant(timeZone)
}

fun LocalDateTime.setHour(newHour: Int): LocalDateTime = LocalDateTime(year, monthNumber, dayOfMonth, newHour, minute, second, nanosecond)

fun LocalDateTime.setMinute(newMinute: Int): LocalDateTime =
    LocalDateTime(year, monthNumber, dayOfMonth, hour, newMinute, second, nanosecond)
