package io.composeflow.model.project.datatype

import com.squareup.kotlinpoet.FileSpec
import io.composeflow.kotlinpoet.FileSpecWithDirectory
import io.composeflow.model.datatype.DATA_TYPE_PACKAGE
import io.composeflow.model.datatype.DataType
import io.composeflow.model.project.Project
import io.composeflow.override.mutableStateListEqualsOverrideOf
import io.composeflow.serializer.LocationAwareFallbackMutableStateListSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DataTypeHolder")
data class DataTypeHolder(
    @Serializable(with = LocationAwareFallbackMutableStateListSerializer::class)
    val dataTypes: MutableList<DataType> = mutableStateListEqualsOverrideOf(),
) {
    fun generateDataTypeFiles(project: Project): List<FileSpecWithDirectory> =
        dataTypes
            .map { dataType ->
                dataType.generateDataClassSpec(project)?.let {
                    FileSpec
                        .builder("${project.packageName}.$DATA_TYPE_PACKAGE", dataType.className)
                        .addType(it)
                        .build()
                }
            }.filterNotNull()
            .map {
                FileSpecWithDirectory(it)
            }
}
