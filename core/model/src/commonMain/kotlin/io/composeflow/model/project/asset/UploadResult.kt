package io.composeflow.model.project.asset

import io.composeflow.cloud.storage.BlobInfoWrapper

sealed interface UploadResult {
    data object NotStarted : UploadResult

    data object Uploading : UploadResult

    data class Success(
        val blobInfoWrapper: BlobInfoWrapper,
    ) : UploadResult

    data class Failure(
        val message: String?,
        val e: Throwable? = null,
    ) : UploadResult
}

sealed interface RemoveResult {
    data object NotStarted : RemoveResult

    data class Removing(
        val blobInfoWrapper: BlobInfoWrapper,
    ) : RemoveResult

    data class Success(
        val blobInfoWrapper: BlobInfoWrapper,
    ) : RemoveResult

    data class Failure(
        val message: String?,
        val e: Throwable? = null,
    ) : RemoveResult
}
