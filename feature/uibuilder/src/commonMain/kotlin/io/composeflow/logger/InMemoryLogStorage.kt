package io.composeflow.logger

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Instant

object InMemoryLogStorage {
    private val _logs = MutableStateFlow<List<LogMessage>>(emptyList())
    val logs = _logs.asStateFlow()

    fun addLog(message: LogMessage) {
        _logs.value = _logs.value + message
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}

class LogMessage(
    val severity: Severity,
    val tag: String,
    val message: String,
    val time: Instant,
) {
    fun formatTime(): String {
        val dateTime = time.toLocalDateTime(TimeZone.currentSystemDefault())
        return dateTime
            .toJavaLocalDateTime()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
    }
}

class InMemoryLogWriter : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        val message = LogMessage(severity, tag, message, Clock.System.now())
        InMemoryLogStorage.addLog(message)
    }
}
