package io.composeflow.model.project.datatype

import com.squareup.kotlinpoet.FileSpec
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.DataTypePackage
import io.composeflow.model.project.Project
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.FallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DataTypeHolder")
data class DataTypeHolder(
    @Serializable(with = FallbackMutableStateListSerializer::class)
    val dataTypes: MutableList<DataType> = mutableStateListEqualsOverrideOf(),
) {
    fun generateDataTypeFiles(project: Project): List<FileSpec?> {
        return dataTypes.map { dataType ->
            dataType.generateDataClassSpec(project)?.let {
                FileSpec.builder("${project.packageName}.${DataTypePackage}", dataType.className)
                    .addType(it)
                    .build()
            }
        }
    }
}
