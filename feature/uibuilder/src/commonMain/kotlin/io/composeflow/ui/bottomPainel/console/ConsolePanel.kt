package io.composeflow.ui.bottomPainel.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import io.composeflow.logger.InMemoryLogStorage
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ConsolePanel() {
    val logs by InMemoryLogStorage.logs.collectAsState()
    val listState = rememberLazyListState()

    // TODO implements scroll to the end when adding log
    LaunchedEffect(logs) {
        listState.animateScrollToItem(logs.size)
    }

    Column(
        modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Console",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 10.dp),
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth().height(1.dp),
        )
        SelectionContainer {
            LazyColumn(
                state = listState,
            ) {
                items(logs) { log ->

                    MaterialTheme.typography.titleMedium
                    val severityColor =
                        when (log.severity) {
                            Severity.Verbose -> Color(0xFF616161)
                            Severity.Debug -> Color(0xFF1976D2)
                            Severity.Info -> Color(0xFF388E3C)
                            Severity.Warn -> Color(0xFFF57C00)
                            Severity.Error -> Color(0xFFD32F2F)
                            Severity.Assert -> Color(0xFF6A1B9A)
                        }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.width(80.dp),
                            text = log.formatTime(),
                            color = Color(0xFF8D6E63),
                        )

                        LogSeverityBadge(log.severity)

                        if (log.tag.isNotEmpty()) {
                            Text(
                                text = "[${log.tag}]",
                                color = Color(0xFF757575),
                            )
                        }

                        Text(
                            text = log.message,
                            color = severityColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogSeverityBadge(severity: Severity) {
    val (text, color, background) =
        when (severity) {
            Severity.Verbose -> Triple("V", Color(0xFF616161), Color(0xFFE0E0E0))
            Severity.Debug -> Triple("D", Color(0xFF1976D2), Color(0xFFBBDEFB))
            Severity.Info -> Triple("I", Color(0xFF388E3C), Color(0xFFC8E6C9))
            Severity.Warn -> Triple("W", Color(0xFFF57C00), Color(0xFFFFE0B2))
            Severity.Error -> Triple("E", Color(0xFFD32F2F), Color(0xFFFFCDD2))
            Severity.Assert -> Triple("A", Color(0xFF6A1B9A), Color(0xFFE1BEE7))
        }

    Box(
        modifier = Modifier.size(25.dp).background(color = background),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}
