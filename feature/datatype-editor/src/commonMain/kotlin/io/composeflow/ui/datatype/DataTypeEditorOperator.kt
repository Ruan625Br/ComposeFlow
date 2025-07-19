package io.composeflow.ui.datatype

import co.touchlab.kermit.Logger
import io.composeflow.ksp.LlmParam
import io.composeflow.ksp.LlmTool
import io.composeflow.model.datatype.DataField
import io.composeflow.model.datatype.DataType
import io.composeflow.model.project.Project
import io.composeflow.model.project.custom_enum.CustomEnum
import io.composeflow.serializer.decodeFromStringWithFallback
import io.composeflow.serializer.encodeToString
import io.composeflow.ui.EventResult
import io.composeflow.util.generateUniqueName

/**
 * Handles operations related to data type editor, such as adding, updating, or removing data types and custom enums.
 * Operations in this class are exposed to the LLM to allow them to call it as tools as well as used
 * from the GUI in ComposeFlow.
 */
class DataTypeEditorOperator {
    fun addDataType(
        project: Project,
        dataType: DataType,
    ): EventResult {
        val result = EventResult()
        try {
            val newName =
                generateUniqueName(
                    dataType.className,
                    project.dataTypeHolder.dataTypes
                        .map { it.className }
                        .toSet(),
                )
            val newDataType = DataType(name = newName, fields = dataType.fields)
            project.dataTypeHolder.dataTypes.add(newDataType)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding data type" }
            result.errorMessages.add("Failed to add data type: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_data_type",
        description = "Adds a new data type to the project. Data types are used to define custom data structures with fields.",
    )
    fun onAddDataType(
        project: Project,
        @LlmParam(description = "The YAML representation of the DataType to be added. The name will be made unique if necessary.")
        dataTypeYaml: String,
    ): EventResult =
        try {
            val dataType = decodeFromStringWithFallback<DataType>(dataTypeYaml)
            addDataType(project, dataType)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing data type YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse data type YAML: ${e.message}")
            }
        }

    fun deleteDataType(
        project: Project,
        dataTypeId: String,
    ): EventResult {
        val result = EventResult()
        try {
            val dataTypeIndex =
                project.dataTypeHolder.dataTypes.indexOfFirst { it.id == dataTypeId }
            if (dataTypeIndex == -1) {
                result.errorMessages.add("Data type with ID $dataTypeId not found.")
                return result
            }
            project.dataTypeHolder.dataTypes.removeAt(dataTypeIndex)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting data type" }
            result.errorMessages.add("Failed to delete data type: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "delete_data_type",
        description = "Removes a data type from the project by its ID.",
    )
    fun onDeleteDataType(
        project: Project,
        @LlmParam(description = "The ID of the data type to be deleted.")
        dataTypeId: String,
    ): EventResult = deleteDataType(project, dataTypeId)

    fun updateDataType(
        project: Project,
        dataType: DataType,
    ): EventResult {
        val result = EventResult()
        try {
            val dataTypeIndex =
                project.dataTypeHolder.dataTypes.indexOfFirst { it.id == dataType.id }
            if (dataTypeIndex == -1) {
                result.errorMessages.add("Data type with ID ${dataType.id} not found.")
                return result
            }

            val newName =
                generateUniqueName(
                    dataType.className,
                    project.dataTypeHolder.dataTypes
                        .filter { it.id != dataType.id }
                        .map { it.className }
                        .toSet(),
                )
            val newDataType = DataType(name = newName, fields = dataType.fields, id = dataType.id)
            project.dataTypeHolder.dataTypes[dataTypeIndex] = newDataType
        } catch (e: Exception) {
            Logger.e(e) { "Error updating data type" }
            result.errorMessages.add("Failed to update data type: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_data_type",
        description = "Updates an existing data type in the project. The name will be made unique if necessary.",
    )
    fun onUpdateDataType(
        project: Project,
        @LlmParam(description = "The YAML representation of the updated DataType. Must include the ID of the data type to update.")
        dataTypeYaml: String,
    ): EventResult =
        try {
            val dataType = decodeFromStringWithFallback<DataType>(dataTypeYaml)
            updateDataType(project, dataType)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing data type YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse data type YAML: ${e.message}")
            }
        }

    fun addDataField(
        project: Project,
        dataTypeId: String,
        dataField: DataField,
    ): EventResult {
        val result = EventResult()
        try {
            val dataType = project.dataTypeHolder.dataTypes.find { it.id == dataTypeId }
            if (dataType == null) {
                result.errorMessages.add("Data type with ID $dataTypeId not found.")
                return result
            }

            val newName =
                generateUniqueName(
                    dataField.variableName,
                    dataType.fields.map { it.variableName }.toSet(),
                )
            val newDataField = DataField(name = newName, fieldType = dataField.fieldType)
            dataType.fields.add(newDataField)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding data field" }
            result.errorMessages.add("Failed to add data field: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_data_field",
        description = "Adds a new field to an existing data type.",
    )
    fun onAddDataField(
        project: Project,
        @LlmParam(description = "The ID of the data type to add the field to.")
        dataTypeId: String,
        @LlmParam(description = "The YAML representation of the DataField to be added. The name will be made unique if necessary.")
        dataFieldYaml: String,
    ): EventResult =
        try {
            val dataField = decodeFromStringWithFallback<DataField>(dataFieldYaml)
            addDataField(project, dataTypeId, dataField)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing data field YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse data field YAML: ${e.message}")
            }
        }

    fun deleteDataField(
        project: Project,
        dataTypeId: String,
        dataFieldId: String,
    ): EventResult {
        val result = EventResult()
        try {
            val dataType = project.dataTypeHolder.dataTypes.find { it.id == dataTypeId }
            if (dataType == null) {
                result.errorMessages.add("Data type with ID $dataTypeId not found.")
                return result
            }

            val fieldIndex = dataType.fields.indexOfFirst { it.id == dataFieldId }
            if (fieldIndex == -1) {
                result.errorMessages.add("Data field with ID $dataFieldId not found.")
                return result
            }

            dataType.fields.removeAt(fieldIndex)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting data field" }
            result.errorMessages.add("Failed to delete data field: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "delete_data_field",
        description = "Removes a field from a data type by its ID.",
    )
    fun onDeleteDataField(
        project: Project,
        @LlmParam(description = "The ID of the data type containing the field.")
        dataTypeId: String,
        @LlmParam(description = "The ID of the data field to be deleted.")
        dataFieldId: String,
    ): EventResult = deleteDataField(project, dataTypeId, dataFieldId)

    fun addCustomEnum(
        project: Project,
        customEnum: CustomEnum,
    ): EventResult {
        val result = EventResult()
        try {
            val newName =
                generateUniqueName(
                    customEnum.enumName,
                    project.customEnumHolder.enumList
                        .map { it.enumName }
                        .toSet(),
                )
            val newEnum = CustomEnum(name = newName, values = customEnum.values)
            project.customEnumHolder.enumList.add(newEnum)
        } catch (e: Exception) {
            Logger.e(e) { "Error adding custom enum" }
            result.errorMessages.add("Failed to add custom enum: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "add_custom_enum",
        description = "Adds a new custom enum to the project. Custom enums are used to define a set of named constants.",
    )
    fun onAddCustomEnum(
        project: Project,
        @LlmParam(description = "The YAML representation of the CustomEnum to be added. The name will be made unique if necessary.")
        customEnumYaml: String,
    ): EventResult =
        try {
            val customEnum = decodeFromStringWithFallback<CustomEnum>(customEnumYaml)
            addCustomEnum(project, customEnum)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing custom enum YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse custom enum YAML: ${e.message}")
            }
        }

    fun deleteCustomEnum(
        project: Project,
        customEnumId: String,
    ): EventResult {
        val result = EventResult()
        try {
            val enumIndex =
                project.customEnumHolder.enumList.indexOfFirst { it.customEnumId == customEnumId }
            if (enumIndex == -1) {
                result.errorMessages.add("Custom enum with ID $customEnumId not found.")
                return result
            }
            project.customEnumHolder.enumList.removeAt(enumIndex)
        } catch (e: Exception) {
            Logger.e(e) { "Error deleting custom enum" }
            result.errorMessages.add("Failed to delete custom enum: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "delete_custom_enum",
        description = "Removes a custom enum from the project by its ID.",
    )
    fun onDeleteCustomEnum(
        project: Project,
        @LlmParam(description = "The ID of the custom enum to be deleted.")
        customEnumId: String,
    ): EventResult = deleteCustomEnum(project, customEnumId)

    fun updateCustomEnum(
        project: Project,
        customEnum: CustomEnum,
    ): EventResult {
        val result = EventResult()
        try {
            val enumIndex =
                project.customEnumHolder.enumList.indexOfFirst { it.customEnumId == customEnum.customEnumId }
            if (enumIndex == -1) {
                result.errorMessages.add("Custom enum with ID ${customEnum.customEnumId} not found.")
                return result
            }

            val newName =
                generateUniqueName(
                    customEnum.enumName,
                    project.customEnumHolder.enumList
                        .filter { it.customEnumId != customEnum.customEnumId }
                        .map { it.enumName }
                        .toSet(),
                )
            val newEnum =
                CustomEnum(
                    name = newName,
                    values = customEnum.values,
                    customEnumId = customEnum.customEnumId,
                )
            project.customEnumHolder.enumList[enumIndex] = newEnum
        } catch (e: Exception) {
            Logger.e(e) { "Error updating custom enum" }
            result.errorMessages.add("Failed to update custom enum: ${e.message}")
        }
        return result
    }

    @LlmTool(
        name = "update_custom_enum",
        description = "Updates an existing custom enum in the project. The name will be made unique if necessary.",
    )
    fun onUpdateCustomEnum(
        project: Project,
        @LlmParam(description = "The YAML representation of the updated CustomEnum. Must include the ID of the enum to update.")
        customEnumYaml: String,
    ): EventResult =
        try {
            val customEnum = decodeFromStringWithFallback<CustomEnum>(customEnumYaml)
            updateCustomEnum(project, customEnum)
        } catch (e: Exception) {
            Logger.e(e) { "Error parsing custom enum YAML" }
            EventResult().apply {
                errorMessages.add("Failed to parse custom enum YAML: ${e.message}")
            }
        }

    @LlmTool(
        name = "list_data_types",
        description = "Lists all data types in the project. Returns a YAML representation of all data types.",
    )
    fun onListDataTypes(project: Project): String =
        try {
            val dataTypes = project.dataTypeHolder.dataTypes
            encodeToString(dataTypes)
        } catch (e: Exception) {
            Logger.e(e) { "Error listing data types" }
            "Error listing data types: ${e.message}"
        }

    @LlmTool(
        name = "get_data_type",
        description = "Gets a specific data type by its ID. Returns a YAML representation of the data type.",
    )
    fun onGetDataType(
        project: Project,
        @LlmParam(description = "The ID of the data type to retrieve.")
        dataTypeId: String,
    ): String =
        try {
            val dataType = project.dataTypeHolder.dataTypes.find { it.id == dataTypeId }
            if (dataType != null) {
                encodeToString(dataType)
            } else {
                "Data type with ID $dataTypeId not found."
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error getting data type" }
            "Error getting data type: ${e.message}"
        }

    @LlmTool(
        name = "list_custom_enums",
        description = "Lists all custom enums in the project. Returns a YAML representation of all custom enums.",
    )
    fun onListCustomEnums(project: Project): String =
        try {
            val customEnums = project.customEnumHolder.enumList
            encodeToString(customEnums)
        } catch (e: Exception) {
            Logger.e(e) { "Error listing custom enums" }
            "Error listing custom enums: ${e.message}"
        }

    @LlmTool(
        name = "get_custom_enum",
        description = "Gets a specific custom enum by its ID. Returns a YAML representation of the custom enum.",
    )
    fun onGetCustomEnum(
        project: Project,
        @LlmParam(description = "The ID of the custom enum to retrieve.")
        customEnumId: String,
    ): String =
        try {
            val customEnum =
                project.customEnumHolder.enumList.find { it.customEnumId == customEnumId }
            if (customEnum != null) {
                encodeToString(customEnum)
            } else {
                "Custom enum with ID $customEnumId not found."
            }
        } catch (e: Exception) {
            Logger.e(e) { "Error getting custom enum" }
            "Error getting custom enum: ${e.message}"
        }
}
