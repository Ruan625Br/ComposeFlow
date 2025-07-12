package io.composeflow.cloud.storage

import co.touchlab.kermit.Logger
import com.github.michaelbull.result.mapBoth
import io.composeflow.datastore.LocalAssetSaver

class AssetSynchronizer(
    private val googleCloudStorageWrapper: GoogleCloudStorageWrapper,
    private val localAssetSaver: LocalAssetSaver,
) {
    suspend fun syncFilesLocally(
        userId: String,
        projectId: String,
        assetFiles: List<BlobInfoWrapper>,
    ) {
        assetFiles.forEach { asset ->
            if (!localAssetSaver.isAssetExistLocally(
                    userId = userId,
                    projectId = projectId,
                    blobInfoWrapper = asset,
                )
            ) {
                Logger.i("${asset.fileName} doesn't exist locally. Downloading...")
                googleCloudStorageWrapper.downloadAsset(asset).mapBoth(
                    success = {
                        it?.let {
                            Logger.i("Downloaded ${it.fileName}. Saving it locally")
                            localAssetSaver.saveAsset(
                                userId = userId,
                                projectId = projectId,
                                blobInfoWrapper = it,
                            )
                        }
                    },
                    failure = {
                        Logger.w("Failed to download assert: ${asset.fileName}")
                    },
                )
            }
        }
    }
}
