package io.composeflow.model.datatype

import io.composeflow.model.parameter.wrapper.InstantWrapper
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.FloatProperty
import io.composeflow.model.property.InstantProperty
import io.composeflow.model.property.IntProperty
import io.composeflow.model.property.StringProperty
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

sealed interface JsonParseResult {
    fun isSuccess(): Boolean
}

sealed interface DataTypeParseResult : JsonParseResult {
    data class Success(val dataType: DataType) : DataTypeParseResult {
        override fun isSuccess(): Boolean = true
    }

    data class SuccessWithWarning(val warningMessage: String, val dataType: DataType) :
        DataTypeParseResult {
        override fun isSuccess(): Boolean = true
    }

    data class Failure(val message: String) : DataTypeParseResult {
        override fun isSuccess(): Boolean = false
    }

    data object EmptyInput : DataTypeParseResult {
        override fun isSuccess(): Boolean = false
    }
}

sealed interface DefaultValuesParseResult : JsonParseResult {
    data class Success(val defaultValues: List<DataTypeDefaultValue>) : DefaultValuesParseResult {
        override fun isSuccess(): Boolean = true
    }

    data class SuccessWithWarning(
        val warningMessage: String,
        val defaultValues: List<DataTypeDefaultValue>,
    ) : DefaultValuesParseResult {
        override fun isSuccess(): Boolean = true
    }

    data class Failure(val message: String) : DefaultValuesParseResult {
        override fun isSuccess(): Boolean = false
    }

    data object EmptyInput : DefaultValuesParseResult {
        override fun isSuccess(): Boolean = false
    }
}

@OptIn(ExperimentalSerializationApi::class)
class JsonParser(
    private val json: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        allowTrailingComma = true
    },
) {

    fun parseJsonToDataType(
        jsonText: String,
        includeDefaultValue: Boolean = false,
    ): DataTypeParseResult {
        if (jsonText.trim().isEmpty()) return DataTypeParseResult.EmptyInput

        // Create a DataType from the first detected JsonObject.
        // A variable to keep track of if the JsonObject is detected.
        var firstObjectDetected = false
        var warning: String? = null

        fun processJsonElement(
            element: JsonElement,
            dataFields: MutableList<DataField>,
            key: String? = null,
            includeDefaultValue: Boolean,
        ) {
            when (element) {
                is JsonArray -> {
                    if (element.isNotEmpty()) {
                        processJsonElement(
                            key = key,
                            element = element[0],
                            dataFields = dataFields,
                            includeDefaultValue = includeDefaultValue,
                        )
                    }
                }

                is JsonObject -> {
                    if (!firstObjectDetected) {
                        firstObjectDetected = true
                        element.forEach {
                            processJsonElement(
                                key = it.key,
                                element = it.value,
                                dataFields = dataFields,
                                includeDefaultValue = includeDefaultValue,
                            )
                        }
                    } else {
                        // Adding the object as String since nested DataType isn't allowed at the moment.
                        // TODO: Allow parsing json once nested DataType is allowed
                        warning = "Nested object(s) are translated as String"
                        dataFields.add(DataField(name = key ?: "", fieldType = FieldType.String()))
                    }
                }

                is JsonPrimitive -> {
                    if (element.content.toIntOrNull() != null) {
                        dataFields.add(
                            DataField(
                                name = key ?: "",
                                fieldType = FieldType.Int(
                                    defaultValue = if (includeDefaultValue) element.content.toInt() else 0
                                )
                            )
                        )
                    } else if (element.content.toFloatOrNull() != null) {
                        dataFields.add(
                            DataField(
                                name = key ?: "",
                                fieldType = FieldType.Float(
                                    defaultValue = if (includeDefaultValue) element.content.toFloat() else 0f
                                )
                            )
                        )
                    } else if (element.content.toBooleanStrictOrNull() != null) {
                        dataFields.add(
                            DataField(
                                name = key ?: "",
                                fieldType = FieldType.Boolean(
                                    defaultValue = if (includeDefaultValue) element.content.toBooleanStrict() else false
                                )
                            )
                        )
                    } else if (element.content.tryToParseToInstant() != null) {
                        dataFields.add(
                            DataField(
                                name = key ?: "",
                                fieldType = FieldType.Instant(
                                    defaultValue = if (includeDefaultValue) element.content.tryToParseToInstant()!! else InstantWrapper()
                                )
                            )
                        )
                    } else {
                        if (element.isString) {
                            dataFields.add(
                                DataField(
                                    name = key ?: "",
                                    fieldType = FieldType.String(
                                        defaultValue = if (includeDefaultValue) element.content else ""
                                    )
                                )
                            )
                        }
                    }
                }

                JsonNull -> {}
            }
        }

        return try {
            val fields: MutableList<DataField> = mutableListOf()
            processJsonElement(
                element = json.parseToJsonElement(jsonText),
                dataFields = fields,
                includeDefaultValue = includeDefaultValue,
            )
            warning?.let {
                DataTypeParseResult.SuccessWithWarning(
                    warningMessage = it,
                    DataType(name = "", fields = fields)
                )
            } ?: DataTypeParseResult.Success(DataType(name = "", fields = fields))
        } catch (e: Exception) {
            DataTypeParseResult.Failure("Failed to parse the json")
        }
    }

    fun parseJsonToDefaultValues(dataType: DataType, jsonText: String): DefaultValuesParseResult {
        if (jsonText.trim().isEmpty()) return DefaultValuesParseResult.EmptyInput

        var warning: String? = null

        fun processEachJsonElement(
            element: JsonElement,
            dataTypeDefaultValue: DataTypeDefaultValue,
            key: String? = null,
            // Represents if the parse process is inside an object. Since nested objects are not
            // supported at the moment, we need to keep track of if it's already inside an object.
            insideObject: Boolean = false,
        ) {
            when (element) {
                is JsonArray -> {
                    // Adding the array as String since nested array isn't allowed at the moment.
                    // TODO: Allow parsing json once nested arrays are allowed
                    warning = "Nested array(s) are translated as String"
                    dataType.findDataFieldOrNullByVariableName(fieldName = key ?: "")
                        ?.let { dataField ->
                            dataTypeDefaultValue.defaultFields.add(
                                FieldDefaultValue(
                                    fieldId = dataField.id,
                                    defaultValue = StringProperty.StringIntrinsicValue(element.toString())
                                )
                            )
                        }
                }

                is JsonObject -> {
                    if (!insideObject) {
                        element.forEach {
                            processEachJsonElement(
                                key = it.key,
                                element = it.value,
                                dataTypeDefaultValue = dataTypeDefaultValue,
                                insideObject = true,
                            )
                        }
                    } else {
                        // Adding the object as String since nested DataType isn't allowed at the moment.
                        // TODO: Allow parsing json once nested DataType is allowed
                        warning = "Nested object(s) are translated as String"
                        dataType.findDataFieldOrNullByVariableName(fieldName = key ?: "")
                            ?.let { dataField ->
                                dataTypeDefaultValue.defaultFields.add(
                                    FieldDefaultValue(
                                        fieldId = dataField.id,
                                        defaultValue = StringProperty.StringIntrinsicValue(element.toString())
                                    )
                                )
                            }
                    }
                }

                is JsonPrimitive -> {
                    dataType.findDataFieldOrNullByVariableName(fieldName = key ?: "")
                        ?.let { dataField ->
                            if (element.content.toIntOrNull() != null) {
                                dataTypeDefaultValue.defaultFields.add(
                                    FieldDefaultValue(
                                        fieldId = dataField.id,
                                        defaultValue = IntProperty.IntIntrinsicValue(
                                            element.content.toIntOrNull() ?: 0
                                        )
                                    )
                                )
                            } else if (element.content.toFloatOrNull() != null) {
                                dataTypeDefaultValue.defaultFields.add(
                                    FieldDefaultValue(
                                        fieldId = dataField.id,
                                        defaultValue = FloatProperty.FloatIntrinsicValue(
                                            element.content.toFloatOrNull() ?: 0f
                                        )
                                    )
                                )
                            } else if (element.content.toBooleanStrictOrNull() != null) {
                                dataTypeDefaultValue.defaultFields.add(
                                    FieldDefaultValue(
                                        fieldId = dataField.id,
                                        defaultValue = BooleanProperty.BooleanIntrinsicValue(
                                            element.content.toBooleanStrictOrNull() ?: false
                                        )
                                    )
                                )
                            } else if (element.content.tryToParseToInstant() != null) {
                                dataTypeDefaultValue.defaultFields.add(
                                    FieldDefaultValue(
                                        fieldId = dataField.id,
                                        defaultValue = InstantProperty.InstantIntrinsicValue(
                                            element.content.tryToParseToInstant()
                                                ?: InstantWrapper()
                                        )
                                    )
                                )
                            } else {
                                if (element.isString) {
                                    dataTypeDefaultValue.defaultFields.add(
                                        FieldDefaultValue(
                                            fieldId = dataField.id,
                                            defaultValue = StringProperty.StringIntrinsicValue(
                                                element.content
                                            )
                                        )
                                    )
                                } else {
                                }
                            }
                        }
                }

                JsonNull -> {}
            }
        }

        return try {
            val defaultValues: MutableList<DataTypeDefaultValue> = mutableListOf()
            when (val element = json.parseToJsonElement(jsonText)) {
                is JsonArray -> {
                    element.forEach { child ->
                        val dataTypeDefaultValue = DataTypeDefaultValue(dataTypeId = dataType.id)
                        processEachJsonElement(
                            element = child,
                            dataTypeDefaultValue
                        )
                        defaultValues.add(dataTypeDefaultValue)
                    }
                }

                is JsonObject -> {}
                is JsonPrimitive -> {}
                JsonNull -> {}
            }
            warning?.let {
                DefaultValuesParseResult.SuccessWithWarning(
                    warningMessage = it,
                    defaultValues = defaultValues,
                )
            } ?: DefaultValuesParseResult.Success(defaultValues = defaultValues)
        } catch (e: Exception) {
            DefaultValuesParseResult.Failure("Failed to parse the json")
        }
    }
}

private fun String.tryToParseToInstant(): InstantWrapper? {
    return try {
        val instant = Instant.parse(this)
        InstantWrapper(instant)
    } catch (e: Exception) {
        null
    }
}
