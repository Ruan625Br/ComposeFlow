package io.composeflow.ui.firestore

import org.jetbrains.compose.resources.StringResource

internal sealed interface FirestoreOperationResult {
    data class Failure(val stringResource: StringResource) : FirestoreOperationResult
    data object Success : FirestoreOperationResult
}