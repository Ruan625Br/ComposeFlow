package io.composeflow.ui.asset

import io.composeflow.cloud.storage.BlobInfoWrapper
import io.github.vinceglb.filekit.core.PlatformFile

data class AssetEditorCallbacks(
    val onUploadImageFile: (PlatformFile) -> Unit,
    val onUploadIconFile: (PlatformFile) -> Unit,
    val onDeleteImageAsset: (BlobInfoWrapper) -> Unit,
    val onDeleteIconAsset: (BlobInfoWrapper) -> Unit,
)