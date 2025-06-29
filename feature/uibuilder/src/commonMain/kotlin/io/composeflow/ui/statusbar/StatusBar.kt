package io.composeflow.ui.statusbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.unit.dp
import io.composeflow.ui.common.ComposeFlowTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatusBar(
    uiState: StatusBarUiState = StatusBarUiState.Normal,
    modifier: Modifier = Modifier,
) {
    Surface {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(24.dp),
        ) {
            when (uiState) {
                is StatusBarUiState.Failure -> {
                    StatusBarFailure(uiState)
                }

                is StatusBarUiState.Loading -> {
                    StatusBarLoading(uiState)
                }

                StatusBarUiState.Normal -> {}
                is StatusBarUiState.Success -> {
                    StatusBarSuccess(uiState)
                }

                is StatusBarUiState.JsBrowserRunSuccess -> {
                    StatusBarSuccess(uiState)
                }
            }
        }
    }
}

@Composable
private fun StatusBarLoading(uiState: StatusBarUiState.Loading) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        LinearProgressIndicator(
            modifier =
                Modifier
                    .heightIn(max = 2.dp)
                    .fillMaxWidth(),
            strokeCap = Round,
        )
        uiState.message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun StatusBarSuccess(uiState: StatusBarUiState) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.tertiary),
    ) {
        val message =
            when (uiState) {
                is StatusBarUiState.JsBrowserRunSuccess -> uiState.message
                is StatusBarUiState.Success -> uiState.message
                else -> error("Invalid uiState. $uiState")
            }
        message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onTertiary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun StatusBarFailure(uiState: StatusBarUiState.Failure) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.errorContainer),
    ) {
        uiState.message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ThemedStatusBarPreview(
    useDarkTheme: Boolean,
    uiState: StatusBarUiState,
) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        StatusBar(uiState = uiState)
    }
}

@Preview
@Composable
fun StatusBarPreview_Normal_Light() {
    ThemedStatusBarPreview(useDarkTheme = false, uiState = StatusBarUiState.Normal)
}

@Preview
@Composable
fun StatusBarPreview_Normal_Dark() {
    ThemedStatusBarPreview(useDarkTheme = true, uiState = StatusBarUiState.Normal)
}

@Preview
@Composable
fun StatusBarPreview_Loading_Light() {
    ThemedStatusBarPreview(
        useDarkTheme = false,
        uiState = StatusBarUiState.Loading("Building application..."),
    )
}

@Preview
@Composable
fun StatusBarPreview_Loading_Dark() {
    ThemedStatusBarPreview(
        useDarkTheme = true,
        uiState = StatusBarUiState.Loading("Building application..."),
    )
}

@Preview
@Composable
fun StatusBarPreview_Success_Light() {
    ThemedStatusBarPreview(
        useDarkTheme = false,
        uiState = StatusBarUiState.Success("Build completed successfully"),
    )
}

@Preview
@Composable
fun StatusBarPreview_Success_Dark() {
    ThemedStatusBarPreview(
        useDarkTheme = true,
        uiState = StatusBarUiState.Success("Build completed successfully"),
    )
}

@Preview
@Composable
fun StatusBarPreview_Failure_Light() {
    ThemedStatusBarPreview(
        useDarkTheme = false,
        uiState = StatusBarUiState.Failure("Build failed: compilation error"),
    )
}

@Preview
@Composable
fun StatusBarPreview_Failure_Dark() {
    ThemedStatusBarPreview(
        useDarkTheme = true,
        uiState = StatusBarUiState.Failure("Build failed: compilation error"),
    )
}
