package io.composeflow.ui.asset

import io.composeflow.cloud.storage.BlobInfoWrapper

sealed interface UploadResult {

    data object NotStarted : UploadResult
    data object Uploading : UploadResult
    data class Success(val blobInfoWrapper: BlobInfoWrapper) : UploadResult
    data object Failure : UploadResult
}

sealed interface RemoveResult {

    data object NotStarted : RemoveResult
    data class Removing(val blobInfoWrapper: BlobInfoWrapper) : RemoveResult
    data class Success(val blobInfoWrapper: BlobInfoWrapper) : RemoveResult
    data object Failure : RemoveResult
}