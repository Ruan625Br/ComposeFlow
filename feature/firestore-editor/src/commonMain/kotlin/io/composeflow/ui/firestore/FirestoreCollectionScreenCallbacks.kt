package io.composeflow.ui.firestore

import io.composeflow.model.datatype.DataField

internal data class FirestoreCollectionScreenCallbacks(
    val onFirestoreCollectionAdded: (
        String,
        DataTypeToAssociate,
    ) -> FirestoreOperationResult = { _, _ -> FirestoreOperationResult.Success },
    val onFocusedFirestoreCollectionIndexUpdated: (Int) -> Unit = {},
    val onDataFieldAdded: (DataField) -> Unit = {},
    val onDataFieldNameUpdated: (dataFieldIndex: Int, inputName: String) -> Unit = { _, _ -> },
    val onDeleteDataField: (dataFieldIndex: Int) -> Unit = {},
    val onDeleteFirestoreCollectionRelationship: () -> Unit = {},
)
