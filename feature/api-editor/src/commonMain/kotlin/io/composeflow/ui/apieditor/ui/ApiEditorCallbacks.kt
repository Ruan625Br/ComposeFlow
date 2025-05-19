package io.composeflow.ui.apieditor.ui

import io.composeflow.model.apieditor.ApiDefinition

data class ApiEditorCallbacks(
    val onFocusedApiDefinitionUpdated: (Int) -> Unit = {},
    val onApiDefinitionCopied: (ApiDefinition) -> Unit = {},
    val onApiDefinitionDeleted: (ApiDefinition) -> Unit = {},
)