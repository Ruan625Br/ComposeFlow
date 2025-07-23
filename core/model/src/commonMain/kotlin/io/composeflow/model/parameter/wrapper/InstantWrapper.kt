@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.composeflow.model.parameter.wrapper

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import io.composeflow.serializer.FallbackInstantSerializer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Clock
import kotlin.time.Instant

@Serializable
data class InstantWrapper(
    @Serializable(FallbackInstantSerializer::class)
    val instant: Instant? = null,
) {
    @Transient
    private val dateFormat =
        DateTimeComponents.Format {
            date(LocalDate.Formats.ISO)
        }

    fun generateCode(): CodeBlock =
        if (instant == null) {
            CodeBlock.of("%T.System.now()", Clock::class.asClassName())
        } else {
            CodeBlock.of(
                "%T.parse(\"${asString()}\").%M(%T.UTC)",
                LocalDate::class.asClassName(),
                MemberName("kotlinx.datetime", "atStartOfDayIn", isExtension = true),
                TimeZone::class.asClassName(),
            )
        }

    fun asString(): String {
        // TODO: Other candidates
        return instant?.let {
            val kotlinxInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(it.toEpochMilliseconds())
            kotlinxInstant.format(dateFormat)
        } ?: ""
    }
}
