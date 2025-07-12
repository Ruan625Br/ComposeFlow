package io.composeflow.ui.datatype

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.composeflow.asClassName
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.datatype.DataField
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.FieldType
import io.composeflow.model.project.Project
import io.composeflow.model.project.custom_enum.CustomEnum
import io.composeflow.repository.ProjectRepository
import io.composeflow.swap
import io.composeflow.util.generateUniqueName
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class DataTypeEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
) : ViewModel() {
    private val dataTypeEditorOperator = DataTypeEditorOperator()

    var focusedDataTypeIndex by mutableStateOf<Int?>(null)
        private set
    var focusedEnumIndex by mutableStateOf<Int?>(null)
        private set

    fun onDataTypeAdded(dataTypeName: String) {
        val result = dataTypeEditorOperator.addDataType(project, DataType(name = dataTypeName))
        if (result.isSuccessful()) {
            focusedDataTypeIndex = project.dataTypeHolder.dataTypes.size - 1
        }
        saveProject()
    }

    fun onDataTypeWithFieldsAdded(
        dataTypeName: String,
        fields: List<DataField> = emptyList(),
    ) {
        val result =
            dataTypeEditorOperator.addDataType(
                project,
                DataType(name = dataTypeName, fields = fields.toMutableList()),
            )
        if (result.isSuccessful()) {
            focusedDataTypeIndex = project.dataTypeHolder.dataTypes.size - 1
        }
        saveProject()
    }

    fun onDataTypeDeleted() {
        focusedDataTypeIndex?.let { index ->
            val dataType = project.dataTypeHolder.dataTypes[index]
            dataTypeEditorOperator.deleteDataType(project, dataType.id)
            saveProject()
        }
        focusedDataTypeIndex = if (project.dataTypeHolder.dataTypes.isNotEmpty()) 0 else null
    }

    fun onDataFieldAdded(dataField: DataField) {
        focusedDataTypeIndex?.let { focusedIndex ->
            val dataType = project.dataTypeHolder.dataTypes[focusedIndex]
            dataTypeEditorOperator.addDataField(project, dataType.id, dataField)
            saveProject()
        }
    }

    fun onDataFieldNameUpdated(
        index: Int,
        inputName: String,
    ) {
        focusedDataTypeIndex?.let { focusedIndex ->
            val editedDataType = project.dataTypeHolder.dataTypes[focusedIndex]
            val newName =
                if (project.dataTypeHolder.dataTypes[focusedIndex]
                        .fields[index]
                        .variableName == inputName
                ) {
                    inputName
                } else {
                    generateUniqueName(
                        inputName,
                        project.dataTypeHolder.dataTypes[focusedIndex]
                            .fields
                            .map { it.variableName }
                            .toSet(),
                    )
                }
            editedDataType.fields[index] =
                project.dataTypeHolder.dataTypes[focusedIndex]
                    .fields[index]
                    .copy(name = newName)
            saveProject()
        }
    }

    fun onDataFieldDefaultValueUpdated(
        index: Int,
        newFieldType: FieldType<*>,
    ) {
        focusedDataTypeIndex?.let { focusedIndex ->
            val editedDataType = project.dataTypeHolder.dataTypes[focusedIndex]
            editedDataType.fields[index] =
                project.dataTypeHolder.dataTypes[focusedIndex]
                    .fields[index]
                    .copy(fieldType = newFieldType)
            saveProject()
        }
    }

    fun onFocusedDataTypeIndexUpdated(index: Int) {
        focusedDataTypeIndex = index
    }

    fun onDeleteDataField(dataFieldIndex: Int) {
        focusedDataTypeIndex?.let { dataTypeIndex ->
            val dataType = project.dataTypeHolder.dataTypes[dataTypeIndex]
            val dataField = dataType.fields[dataFieldIndex]
            dataTypeEditorOperator.deleteDataField(project, dataType.id, dataField.id)
            saveProject()
        }
    }

    fun onFocusedEnumIndexUpdated(index: Int) {
        focusedEnumIndex = index
    }

    fun onEnumAdded(enumName: String) {
        val result = dataTypeEditorOperator.addCustomEnum(project, CustomEnum(name = enumName))
        if (result.isSuccessful()) {
            focusedEnumIndex = project.customEnumHolder.enumList.size - 1
        }
        saveProject()
    }

    fun onEnumValueAdded(enumValue: String) {
        focusedEnumIndex?.let { focusedIndex ->
            val newName =
                generateUniqueName(
                    enumValue,
                    project.customEnumHolder.enumList[focusedIndex]
                        .values
                        .toSet(),
                )
            val editedEnum = project.customEnumHolder.enumList[focusedIndex]
            editedEnum.values.add(newName)
            saveProject()
        }
    }

    fun onEnumValueUpdated(
        index: Int,
        value: String,
    ) {
        focusedEnumIndex?.let { focusedIndex ->
            val editedEnum = project.customEnumHolder.enumList[focusedIndex]
            if (editedEnum.enumName == value.asClassName()) {
                return
            }
            val newName =
                generateUniqueName(
                    value,
                    project.customEnumHolder.enumList[focusedIndex]
                        .values
                        .toSet(),
                )
            editedEnum.values[index] = newName
            saveProject()
        }
    }

    fun onEnumDeleted() {
        focusedEnumIndex?.let { index ->
            val customEnum = project.customEnumHolder.enumList[index]
            dataTypeEditorOperator.deleteCustomEnum(project, customEnum.customEnumId)
            saveProject()
        }
        focusedEnumIndex = if (project.customEnumHolder.enumList.size > 0) 0 else null
    }

    fun onDeleteEnumValue(enumValueIndex: Int) {
        focusedEnumIndex?.let {
            val editedEnum = project.customEnumHolder.enumList[it]
            editedEnum.values.removeAt(enumValueIndex)
            saveProject()
        }
    }

    fun onSwapEnumValueIndexes(
        from: Int,
        to: Int,
    ) {
        focusedEnumIndex?.let {
            val editedEnum = project.customEnumHolder.enumList[it]
            editedEnum.values.swap(from, to)
            saveProject()
        }
    }

    private fun saveProject() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
