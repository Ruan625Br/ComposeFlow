package io.composeflow.ui.inspector.codeviewer

import androidx.compose.ui.text.AnnotatedString

sealed interface CodeInspectorUiState {
    data object Loading : CodeInspectorUiState

    data class Success(
        val parsedCode: AnnotatedString,
    ) : CodeInspectorUiState
}
