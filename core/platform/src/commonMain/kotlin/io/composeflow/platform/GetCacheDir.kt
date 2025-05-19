package io.composeflow.platform

import io.composeflow.cloud.storage.BlobInfoWrapper
import java.io.File

expect fun getCacheDir(): File

fun getAssetCacheFileFor(
    userId: String,
    projectId: String,
    blobInfoWrapper: BlobInfoWrapper,
): File {
    val asset = getCacheDir().resolve("projects")
        .resolve(userId)
        .resolve(projectId)
        .resolve("assets")
        .resolve(blobInfoWrapper.fileName)
    return asset
}