package io.composeflow.model.datatype

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import io.composeflow.asVariableName
import io.composeflow.model.project.Project
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.project.firebase.CollectionId
import io.composeflow.ui.propertyeditor.DropdownItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

typealias DataFieldId = String

@Serializable
sealed interface DataFieldType {
    /**
     * Field name that is used to specify this field type.
     */
    fun fieldName(project: Project): String

    @Serializable
    data object Primitive : DataFieldType {
        override fun fieldName(project: Project): String = ""
    }

    @Serializable
    data class DocumentId(
        val firestoreCollectionId: CollectionId,
    ) : DataFieldType {
        override fun fieldName(project: Project): String = ".$FIRESTORE_DOCUMENT_ID"
    }

    @Serializable
    data class DataType(
        val dataTypeId: DataTypeId,
    ) : DataFieldType {
        override fun fieldName(project: Project): String = ""
    }

    @Serializable
    data class FieldInDataType(
        val dataTypeId: DataTypeId,
        val fieldId: DataFieldId,
    ) : DataFieldType {
        override fun fieldName(project: Project): String {
            val dataType = project.findDataTypeOrNull(dataTypeId)
            val dataField = dataType?.findDataFieldOrNull(fieldId)
            return dataField?.let { ".${it.variableName}" } ?: ""
        }
    }
}

@Serializable
@SerialName("DataField")
data class DataField(
    val id: DataFieldId = Uuid.random().toString(),
    private val name: String,
    val fieldType: FieldType<*>,
) : DropdownItem {
    val variableName = name.asVariableName()

    @Composable
    override fun asDropdownText(): AnnotatedString = AnnotatedString("$name ${fieldType.asDropdownText().text}")

    override fun isSameItem(item: Any): Boolean = item is DataField && item.id == id
}

data class DocumentIdDropdownItem(
    val firestoreCollectionId: CollectionId,
) : DropdownItem {
    @Composable
    override fun asDropdownText(): AnnotatedString = AnnotatedString("Document ID")

    override fun isSameItem(item: Any): Boolean = item is DocumentIdDropdownItem
}
