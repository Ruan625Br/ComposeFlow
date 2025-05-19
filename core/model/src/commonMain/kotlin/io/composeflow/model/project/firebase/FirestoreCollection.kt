package io.composeflow.model.project.firebase

import androidx.compose.runtime.Composable
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import io.composeflow.ViewModelConstant
import io.composeflow.kotlinpoet.ClassHolder
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.DataTypeId
import io.composeflow.model.project.Project
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.project.findFirestoreCollectionOrNull
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.propertyeditor.DropdownItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

typealias CollectionId = String

@Serializable
@SerialName("FirestoreCollection")
data class FirestoreCollection(
    val id: CollectionId = Uuid.random().toString(),
    val name: String,
    val dataTypeId: DataTypeId? = null,
) : DropdownItem {

    fun findCompanionDataType(project: Project): DataType? {
        return project.dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId }
    }

    @Composable
    override fun asDropdownText(): String = name

    override fun isSameItem(item: Any): Boolean {
        return item is FirestoreCollection && item.id == id
    }

    fun getReadVariableName(project: Project): String {
        val dataType = findCompanionDataType(project) ?: return "firestoreCollection"
        return "${dataType.className.replaceFirstChar { it.lowercase() }}Collection"
    }

    fun generateStatePropertiesToViewModel(project: Project): List<PropertySpec> {
        val firestoreCollection = project.findFirestoreCollectionOrNull(id)
        val dataType = firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }
        if (dataType == null) return emptyList()

        val listDataType = ClassName("kotlin.collections", "List").parameterizedBy(
            dataType.asKotlinPoetClassName(project)
        )
        val queryResultType = ClassHolder.ComposeFlow.DataResult.parameterizedBy(
            listDataType
        )
        val stateFlowType = ClassHolder.Coroutines.Flow.StateFlow.parameterizedBy(queryResultType)
        val optInAnnotation = AnnotationSpec.builder(ClassName("kotlin", "OptIn"))
            .addMember("%T::class", ClassName("kotlinx.coroutines", "ExperimentalCoroutinesApi"))
            .build()

        val collectionStateFlowProperty =
            PropertySpec.builder(getReadVariableName(project), stateFlowType)
                .addAnnotation(optInAnnotation)
                .initializer(
                    CodeBlock.of(
                        """${ViewModelConstant.firestore}.collection("${firestoreCollection.name}")
        .snapshots(includeMetadataChanges = false)
        .%M { querySnapshot ->
            %T.Success(querySnapshot.documents.map { document ->
                document.data(%T.serializer()).copy(documentId = document.id)
            })
        }
        .%M(
            scope = %M,
            started = %M.WhileSubscribed(5_000),
            initialValue = %T.Loading
        ) 
            """,
                        MemberHolder.Coroutines.Flow.mapLatest,
                        ClassHolder.ComposeFlow.DataResult,
                        dataType.asKotlinPoetClassName(project),
                        MemberHolder.Coroutines.Flow.stateIn,
                        MemberHolder.PreCompose.viewModelScope,
                        MemberHolder.Coroutines.Flow.SharingStarted,
                        ClassHolder.ComposeFlow.DataResult,
                    )
                )
                .build()
        return listOf(collectionStateFlowProperty)
    }

    fun generateVariableInitializationBlock(
        project: Project,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock {
        val builder = CodeBlock.builder()
        builder.addStatement(
            "val ${getReadVariableName(project)} = ${context.currentEditable.viewModelName}.${
                getReadVariableName(
                    project
                )
            }.%M().value",
            MemberHolder.AndroidX.Runtime.collectAsState,
        )
        return builder.build()
    }

    fun valueType(project: Project): ComposeFlowType {
        val firestoreCollection = project.findFirestoreCollectionOrNull(id)
        val dataType = firestoreCollection?.dataTypeId?.let { project.findDataTypeOrNull(it) }
        if (dataType == null) return ComposeFlowType.UnknownType()
        return ComposeFlowType.CustomDataType(isList = true, dataTypeId = dataType.id)
    }
}