package io.composeflow.ui.settings.appassets

import androidx.compose.ui.graphics.Color
import io.composeflow.cloud.storage.BlobInfoWrapper
import io.github.vinceglb.filekit.core.PlatformFile

data class AppAssetCallbacks(
    val onUploadAndroidSplashScreenImageFile: (file: PlatformFile) -> Unit,
    val onChangeAndroidSplashBackgroundColor: (color: Color?) -> Unit,
    val onChangeAndroidSplashScreenImageIcon: (blobInfoWrapper: BlobInfoWrapper?) -> Unit,
    val onUploadIosSplashScreenImageFile: (file: PlatformFile) -> Unit,
    val onChangeIosSplashBackgroundColor: (color: Color?) -> Unit,
    val onChangeIosSplashScreenImageIcon: (blobInfoWrapper: BlobInfoWrapper?) -> Unit,
)
