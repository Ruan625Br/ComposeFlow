package io.composeflow.ui.asset

import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapBoth
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.cloud.storage.BlobInfoWrapper
import io.composeflow.cloud.storage.GoogleCloudStorageWrapper
import io.composeflow.datastore.LocalAssetSaver
import io.composeflow.di.ServiceLocator
import io.composeflow.model.project.Project
import io.composeflow.repository.ProjectRepository
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AssetEditorViewModel(
    private val firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val ioDispatcher: CoroutineDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KeyIoDispatcher) {
            Dispatchers.IO
        },
    private val storageWrapper: GoogleCloudStorageWrapper,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
    private val localAssetSaver: LocalAssetSaver = LocalAssetSaver(),
) : ViewModel() {
    private val _uploadResult: MutableStateFlow<UploadResult> =
        MutableStateFlow(UploadResult.NotStarted)
    val uploadResult: StateFlow<UploadResult> = _uploadResult

    private val _removeResult: MutableStateFlow<RemoveResult> =
        MutableStateFlow(RemoveResult.NotStarted)
    val removeResult: StateFlow<RemoveResult> = _removeResult

    fun onDeleteImageAsset(blobInfoWrapper: BlobInfoWrapper) {
        viewModelScope.launch {
            _removeResult.value = RemoveResult.Removing(blobInfoWrapper)
            val result = storageWrapper.deleteFile(fullPath = blobInfoWrapper.blobId.name)
            result.mapBoth(
                success = {
                    _removeResult.value = RemoveResult.Success(blobInfoWrapper)
                    project.assetHolder.images.remove(blobInfoWrapper)
                    localAssetSaver.deleteAsset(
                        userId = firebaseIdToken.user_id,
                        projectId = project.id,
                        blobInfoWrapper = blobInfoWrapper,
                    )
                    saveProject()
                },
                failure = {
                },
            )
        }
    }

    fun onUploadImageFile(file: PlatformFile) {
        viewModelScope.launch(ioDispatcher) {
            _uploadResult.value = UploadResult.Uploading
            val result =
                storageWrapper.uploadAsset(
                    userId = firebaseIdToken.user_id,
                    projectId = project.id,
                    file,
                )
            result.mapBoth(
                success = {
                    result.get()?.let { blobInfo ->
                        _uploadResult.value = UploadResult.Success(blobInfo)
                        project.assetHolder.images.add(blobInfo)
                        localAssetSaver.saveAsset(
                            firebaseIdToken.user_id,
                            project.id,
                            blobInfo,
                        )
                        saveProject()
                    }
                },
                failure = {
                    _uploadResult.value = UploadResult.Failure
                },
            )
        }
    }

    fun onUploadIconFile(file: PlatformFile) {
        viewModelScope.launch(ioDispatcher) {
            _uploadResult.value = UploadResult.Uploading
            val result =
                storageWrapper.uploadAsset(
                    userId = firebaseIdToken.user_id,
                    projectId = project.id,
                    file,
                )
            result.mapBoth(
                success = {
                    result.get()?.let { blobInfo ->
                        _uploadResult.value = UploadResult.Success(blobInfo)
                        project.assetHolder.icons.add(blobInfo)

                        localAssetSaver.saveAsset(
                            firebaseIdToken.user_id,
                            project.id,
                            blobInfo,
                        )
                        saveProject()
                    }
                },
                failure = {
                    _uploadResult.value = UploadResult.Failure
                },
            )
        }
    }

    fun onDeleteIconAsset(blobInfoWrapper: BlobInfoWrapper) {
        viewModelScope.launch {
            _removeResult.value = RemoveResult.Removing(blobInfoWrapper)
            val result = storageWrapper.deleteFile(fullPath = blobInfoWrapper.blobId.name)
            result.mapBoth(
                success = {
                    _removeResult.value = RemoveResult.Success(blobInfoWrapper)
                    project.assetHolder.icons.remove(blobInfoWrapper)
                    localAssetSaver.deleteAsset(
                        userId = firebaseIdToken.user_id,
                        projectId = project.id,
                        blobInfoWrapper = blobInfoWrapper,
                    )
                    saveProject()
                },
                failure = {
                },
            )
        }
    }

    fun onResetUploadResult() {
        _uploadResult.value = UploadResult.NotStarted
    }

    fun onResetRemoveResult() {
        _removeResult.value = RemoveResult.NotStarted
    }

    private fun saveProject() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
