package io.composeflow.datastore

import io.composeflow.cloud.storage.BlobInfoWrapper
import io.composeflow.di.ServiceLocator
import io.composeflow.platform.getCacheDir
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalAssetSaver(
    private val ioDispatcher: CoroutineDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KEY_IO_DISPATCHER) {
            Dispatchers.IO
        },
) {
    fun isAssetExistLocally(
        userId: String,
        projectId: String,
        blobInfoWrapper: BlobInfoWrapper,
    ): Boolean {
        val assetDir = prepareAssetDir(userId, projectId)
        val file = assetDir.resolve(blobInfoWrapper.fileName)
        return file.exists()
    }

    suspend fun saveAsset(
        userId: String,
        projectId: String,
        blobInfoWrapper: BlobInfoWrapper,
    ) {
        withContext(ioDispatcher) {
            val assetDir = prepareAssetDir(userId, projectId)
            val file = assetDir.resolve(blobInfoWrapper.fileName)
            blobInfoWrapper.contentBytes?.let {
                file.writeBytes(it)
            }
        }
    }

    suspend fun deleteAsset(
        userId: String,
        projectId: String,
        blobInfoWrapper: BlobInfoWrapper,
    ) {
        withContext(ioDispatcher) {
            val assetDir = prepareAssetDir(userId, projectId)
            val file = assetDir.resolve(blobInfoWrapper.fileName)
            file.delete()
        }
    }
}

private fun prepareAssetDir(
    userId: String,
    projectId: String,
): File {
    val assetsDir =
        getCacheDir()
            .resolve("projects")
            .resolve(userId)
            .resolve(projectId)
            .resolve("assets")
    assetsDir.mkdirs()
    return assetsDir
}
