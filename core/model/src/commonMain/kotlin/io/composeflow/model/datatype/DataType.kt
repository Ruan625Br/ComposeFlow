package io.composeflow.model.datatype

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import io.composeflow.asClassName
import io.composeflow.model.project.Project
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.project.firebase.FirestoreCollection
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.LocationAwareMutableStateListSerializer
import io.composeflow.ui.propertyeditor.DropdownItem
import io.composeflow.ui.propertyeditor.DropdownTextDisplayable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

const val DATA_TYPE_PACKAGE = "datatype"
const val FIRESTORE_DOCUMENT_ID = "documentId"

val EmptyDataType = DataType(name = "")

typealias DataTypeId = String

@Serializable
@SerialName("DataType")
data class DataType(
    val id: DataTypeId = Uuid.random().toString(),
    private val name: String,
    @Serializable(LocationAwareMutableStateListSerializer::class)
    val fields: MutableList<DataField> = mutableStateListEqualsOverrideOf(),
) : DropdownTextDisplayable {
    @Transient
    val className = name.asClassName()

    private fun isValid(): Boolean = fields.isNotEmpty()

    @Composable
    override fun asDropdownText(): AnnotatedString = AnnotatedString(className)

    fun getDataFields(project: Project): List<DataField> {
        val matchingFirestoreCollection = findMatchingFirestoreCollection(project)
        return if (matchingFirestoreCollection != null) {
            listOf(
                DataField(
                    name = FIRESTORE_DOCUMENT_ID,
                    fieldType = FieldType.DocumentId(firestoreCollectionId = matchingFirestoreCollection.id),
                ),
            ) + fields
        } else {
            fields
        }
    }

    fun asKotlinPoetClassName(project: Project): ClassName = ClassName("${project.packageName}.$DATA_TYPE_PACKAGE", name.asClassName())

//    fun findDataFieldOrNull(fieldName: String): DataField? {
//        return fields.firstOrNull { it.variableName == fieldName }
//    }

    fun findDataFieldOrNullByVariableName(fieldName: String): DataField? = fields.find { it.variableName == fieldName }

    fun findDataFieldOrNull(dataFieldId: String): DataField? = fields.find { it.id == dataFieldId }

    /**
     * Generates a TypeSpec for the data class that represents this data type.
     */
    fun generateDataClassSpec(project: Project): TypeSpec? {
        if (!isValid()) return null

        val constructorSpecBuilder = FunSpec.constructorBuilder()
        val typeSpecBuilder =
            TypeSpec
                .classBuilder(name.asClassName())
                .addModifiers(KModifier.DATA)
                .addAnnotation(Serializable::class)

        if (this.findMatchingFirestoreCollection(project) != null) {
            constructorSpecBuilder.addParameter(
                ParameterSpec
                    .builder(FIRESTORE_DOCUMENT_ID, String::class.asTypeName())
                    .defaultValue("\"\"")
                    .build(),
            )
            typeSpecBuilder.addProperty(
                PropertySpec
                    .builder(
                        name = FIRESTORE_DOCUMENT_ID,
                        type = String::class.asTypeName(),
                    ).initializer(FIRESTORE_DOCUMENT_ID)
                    .build(),
            )
        }
        fields.forEach {
            val constructorParameterBuilder =
                ParameterSpec.builder(
                    name = it.variableName,
                    type = it.fieldType.type().asKotlinPoetTypeName(project),
                )
            it.fieldType.defaultValueAsCodeBlock(project).let { defaultCodeBlock ->
                constructorParameterBuilder.defaultValue(defaultCodeBlock)
            }
            constructorSpecBuilder.addParameter(
                constructorParameterBuilder.build(),
            )
            typeSpecBuilder.addProperty(
                PropertySpec
                    .builder(
                        name = it.variableName,
                        type = it.fieldType.type().asKotlinPoetTypeName(project),
                    ).initializer(it.variableName)
                    .build(),
            )
        }
        typeSpecBuilder
            .primaryConstructor(
                constructorSpecBuilder.build(),
            )
        return typeSpecBuilder.build()
    }

    fun dataFieldsDropdownItems(project: Project): List<DropdownItem> {
        val matcingFirestoreCollection = findMatchingFirestoreCollection(project)
        return if (matcingFirestoreCollection != null) {
            buildList {
                add(DocumentIdDropdownItem(matcingFirestoreCollection.id))
                addAll(fields)
            }
        } else {
            fields
        }
    }

    private fun findMatchingFirestoreCollection(project: Project): FirestoreCollection? =
        project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections.firstOrNull {
            it.dataTypeId == id
        }
}

@Serializable
data class DataTypeDefaultValue(
    val dataTypeId: DataTypeId,
    val defaultFields: MutableList<FieldDefaultValue> = mutableListOf(),
)

fun List<DataTypeDefaultValue>.generateCodeBlock(project: Project): CodeBlock {
    val builder = CodeBlock.builder()
    if (isEmpty()) {
        builder.add("\"[]\"")
        return builder.build()
    }
    val dataType = project.findDataTypeOrNull(get(0).dataTypeId) ?: return builder.build()
    builder.add("listOf(")
    forEach { entry ->
        builder.add("%T(", dataType.asKotlinPoetClassName(project))
        entry.defaultFields.forEach { field ->
            val dataField = dataType.findDataFieldOrNull(field.fieldId)
            dataField?.let {
                builder.add("${dataField.variableName} = %L,", field.defaultValue.asCodeBlock())
            }
        }
        builder.add("),")
    }
    builder.add(")")
    return builder.build()
}

@Serializable
data class FieldDefaultValue(
    val fieldId: DataFieldId,
    val defaultValue: IntrinsicProperty<*>,
)
