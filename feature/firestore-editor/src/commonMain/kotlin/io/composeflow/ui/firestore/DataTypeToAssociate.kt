package io.composeflow.ui.firestore

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import io.composeflow.model.datatype.DataType
import io.composeflow.ui.propertyeditor.DropdownItem

sealed interface DataTypeToAssociate : DropdownItem {
    data class ExistingDataType(
        val dataType: DataType,
    ) : DataTypeToAssociate {
        @Composable
        override fun asDropdownText(): AnnotatedString = AnnotatedString(dataType.className)

        override fun isSameItem(item: Any): Boolean = (item is ExistingDataType) && item.dataType.id == dataType.id
    }

    data object CreateNewDataType : DataTypeToAssociate {
        @Composable
        override fun asDropdownText(): AnnotatedString = AnnotatedString("(Create new Data Type)")

        override fun isSameItem(item: Any): Boolean = item is CreateNewDataType
    }
}
