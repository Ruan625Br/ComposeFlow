package io.composeflow.ui.firestore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.composeflow.Res
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.firestore_collection_same_name_exists
import io.composeflow.model.datatype.DataField
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.FieldType
import io.composeflow.model.project.Project
import io.composeflow.model.project.firebase.FirestoreCollection
import io.composeflow.repository.ProjectRepository
import io.composeflow.util.generateUniqueName
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class FirestoreEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
) : ViewModel() {
    var focusedFirestoreCollectionIndex by mutableStateOf<Int?>(null)

    internal fun onFirestoreCollectionAdded(
        collectionName: String,
        dataTypeToAssociate: DataTypeToAssociate,
    ): FirestoreOperationResult {
        val existingNames =
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
                .map { it.name }
                .toSet()
        if (existingNames.contains(collectionName)) {
            return FirestoreOperationResult.Failure(Res.string.firestore_collection_same_name_exists)
        }

        val firestoreCollection =
            when (dataTypeToAssociate) {
                DataTypeToAssociate.CreateNewDataType -> {
                    val dataTypeName =
                        generateUniqueName(
                            initial = collectionName,
                            existing =
                                project.dataTypeHolder.dataTypes
                                    .map { it.className }
                                    .toSet(),
                        )
                    val dataType = DataType(name = dataTypeName)
                    project.dataTypeHolder.dataTypes.add(dataType)
                    FirestoreCollection(name = collectionName, dataTypeId = dataType.id)
                }

                is DataTypeToAssociate.ExistingDataType -> {
                    FirestoreCollection(
                        name = collectionName,
                        dataTypeId = dataTypeToAssociate.dataType.id,
                    )
                }
            }
        project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
            .add(firestoreCollection)
        focusedFirestoreCollectionIndex =
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections.size - 1
        saveProject()
        return FirestoreOperationResult.Success
    }

    fun onFocusedFirestoreCollectionIndexUpdated(index: Int) {
        focusedFirestoreCollectionIndex = index
    }

    fun onDataFieldAdded(dataField: DataField) {
        focusedFirestoreCollectionIndex?.let { focusedIndex ->
            val dataTypeId =
                project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections[focusedIndex]
                    .dataTypeId
            val dataType =
                project.dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId } ?: return
            val newName =
                generateUniqueName(
                    dataField.variableName,
                    dataType.fields.map { it.variableName }.toSet(),
                )
            dataType.fields.add(dataField.copy(name = newName))
            saveProject()
        }
    }

    fun onDataFieldNameUpdated(
        dataFieldIndex: Int,
        inputName: String,
    ) {
        focusedFirestoreCollectionIndex?.let { focusedIndex ->
            val dataTypeId =
                project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections[focusedIndex]
                    .dataTypeId
            val dataType =
                project.dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId } ?: return
            val newName =
                if (dataType.fields[dataFieldIndex].variableName == inputName) {
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
            dataType.fields[dataFieldIndex] = dataType.fields[dataFieldIndex].copy(name = newName)
            saveProject()
        }
    }

    fun onDataFieldDefaultValueUpdated(
        dataFieldIndex: Int,
        newFieldType: FieldType<*>,
    ) {
        focusedFirestoreCollectionIndex?.let { focusedIndex ->
            val dataTypeId =
                project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections[focusedIndex]
                    .dataTypeId
            val dataType =
                project.dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId } ?: return
            dataType.fields[dataFieldIndex] = dataType.fields[dataFieldIndex].copy(fieldType = newFieldType)
            saveProject()
        }
    }

    fun onDeleteDataField(dataFieldIndex: Int) {
        focusedFirestoreCollectionIndex?.let {
            val dataTypeId =
                project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections[it]
                    .dataTypeId
            val dataType =
                project.dataTypeHolder.dataTypes.firstOrNull { it.id == dataTypeId } ?: return
            dataType.fields.removeAt(dataFieldIndex)
            saveProject()
        }
    }

    fun onDeleteFirestoreCollectionRelationship() {
        focusedFirestoreCollectionIndex?.let {
            project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
                .removeAt(it)
            saveProject()
        }
        focusedFirestoreCollectionIndex =
            if (project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
                    .isNotEmpty()
            ) {
                0
            } else {
                null
            }
    }

    private fun saveProject() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
