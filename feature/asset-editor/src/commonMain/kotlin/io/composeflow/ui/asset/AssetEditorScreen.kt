package io.composeflow.ui.asset

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import io.composeflow.Res
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.cloud.storage.BlobInfoWrapper
import io.composeflow.cloud.storage.GoogleCloudStorageWrapper
import io.composeflow.cloud.storage.asDateString
import io.composeflow.model.project.Project
import io.composeflow.remove
import io.composeflow.remove_asset
import io.composeflow.remove_succeeded
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.backgroundContainerNeutral
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.switchByHovered
import io.composeflow.ui.utils.asImageComposable
import io.composeflow.upload_succeeded
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

enum class AssetEditorNavigationDestination(
    val icon: ImageVector,
    val destinationName: String,
) {
    ImageEditor(
        icon = Icons.Outlined.Image,
        destinationName = "Images",
    ),
    IconEditor(
        icon = Icons.Outlined.AddCircle,
        destinationName = "Icons",
    ),
}

@Composable
fun AssetEditorScreen(
    project: Project,
    modifier: Modifier = Modifier,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel =
        viewModel(modelClass = AssetEditorViewModel::class) {
            AssetEditorViewModel(
                firebaseIdToken = firebaseIdToken,
                project = project,
                storageWrapper = GoogleCloudStorageWrapper(),
            )
        }
    val coroutineScope = rememberCoroutineScope()
    val uploadResult by viewModel.uploadResult.collectAsState()
    val removeResult by viewModel.removeResult.collectAsState()

    val uploadSucceeded = stringResource(Res.string.upload_succeeded)

    var selectedDestination by remember { mutableStateOf(AssetEditorNavigationDestination.ImageEditor) }

    val assetEditorCallbacks =
        AssetEditorCallbacks(
            onUploadImageFile = viewModel::onUploadImageFile,
            onUploadIconFile = viewModel::onUploadIconFile,
            onDeleteImageAsset = viewModel::onDeleteImageAsset,
            onDeleteIconAsset = viewModel::onDeleteIconAsset,
        )
    val onShowSnackbar = LocalOnShowSnackbar.current
    when (val result = uploadResult) {
        UploadResult.Failure -> {}
        UploadResult.NotStarted -> {}
        is UploadResult.Success -> {
            coroutineScope.launch {
                onShowSnackbar(
                    uploadSucceeded + ": ${result.blobInfoWrapper.fileName}",
                    null,
                )
                viewModel.onResetUploadResult()
            }
        }

        UploadResult.Uploading -> {}
    }
    val removeSucceeded = stringResource(Res.string.remove_succeeded)
    when (val result = removeResult) {
        RemoveResult.Failure -> {}
        RemoveResult.NotStarted -> {}
        is RemoveResult.Removing -> {}
        is RemoveResult.Success -> {
            coroutineScope.launch {
                onShowSnackbar(
                    removeSucceeded + ": ${result.blobInfoWrapper.fileName}",
                    null,
                )
                viewModel.onResetRemoveResult()
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Row {
            AssetEditorContentNavigation(
                currentDestination = selectedDestination,
                onDestinationChanged = {
                    selectedDestination = it
                },
                modifier = Modifier.width(196.dp),
            )
            when (selectedDestination) {
                AssetEditorNavigationDestination.ImageEditor -> {
                    ImageAssetDetails(
                        project = project,
                        assetEditorCallbacks = assetEditorCallbacks,
                        removeResult = removeResult,
                        uploadResult = uploadResult,
                    )
                }

                AssetEditorNavigationDestination.IconEditor -> {
                    IconAssetDetails(
                        project = project,
                        assetEditorCallbacks = assetEditorCallbacks,
                        removeResult = removeResult,
                        uploadResult = uploadResult,
                    )
                }
            }
        }
    }
}

@Composable
fun AssetEditorContentNavigation(
    currentDestination: AssetEditorNavigationDestination,
    onDestinationChanged: (AssetEditorNavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(
                modifier = Modifier.width(180.dp),
            ) {
                Spacer(Modifier.height(16.dp))
                AssetEditorNavigationDestination.entries.forEachIndexed { i, destination ->
                    NavigationDrawerItem(
                        icon = {
                            ComposeFlowIcon(
                                imageVector = destination.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                destination.destinationName,
                                style = MaterialTheme.typography.titleSmall,
                            )
                        },
                        selected = currentDestination.ordinal == i,
                        onClick = {
                            onDestinationChanged(destination)
                        },
                        modifier =
                            Modifier
                                .heightIn(max = 40.dp)
                                .padding(horizontal = 12.dp),
                    )
                }
            }
        },
        modifier = modifier,
        content = {},
    )
}

@Composable
private fun ImageAssetDetails(
    project: Project,
    assetEditorCallbacks: AssetEditorCallbacks,
    removeResult: RemoveResult,
    uploadResult: UploadResult,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .backgroundContainerNeutral()
                .padding(16.dp),
    ) {
        Spacer(Modifier.width(128.dp))
        ImageAssetDetailsContent(
            project = project,
            assetEditorCallbacks = assetEditorCallbacks,
            removeResult = removeResult,
            uploadResult = uploadResult,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(128.dp))
    }
}

@Composable
private fun ImageAssetDetailsContent(
    project: Project,
    assetEditorCallbacks: AssetEditorCallbacks,
    removeResult: RemoveResult,
    uploadResult: UploadResult,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .wrapContentHeight()
                        .padding(16.dp),
            ) {
                TextButton(onClick = {
                    coroutineScope.launch {
                        val file =
                            FileKit.pickFile(
                                type = PickerType.Image,
                                mode = PickerMode.Single,
                                title = "Pick an image",
                            )
                        file?.let {
                            assetEditorCallbacks.onUploadImageFile(it)
                        }
                    }
                }) {
                    if (uploadResult == UploadResult.Uploading) {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp))
                    } else {
                        Text("+ Upload image")
                    }
                }
            }

            HorizontalDivider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                val assets = project.assetHolder.images
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(assets) {
                        AssetItem(
                            project = project,
                            blobInfoWrapper = it,
                            onDeleteAsset = assetEditorCallbacks.onDeleteImageAsset,
                            removeResult = removeResult,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AssetItem(
    project: Project,
    blobInfoWrapper: BlobInfoWrapper,
    onDeleteAsset: (BlobInfoWrapper) -> Unit,
    removeResult: RemoveResult,
    modifier: Modifier = Modifier,
) {
    var deleteDialogOpen by remember { mutableStateOf(false) }
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                ).switchByHovered(
                    hovered =
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp),
                        ),
                    notHovered =
                        Modifier.alpha(0.5f).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(8.dp),
                        ),
                ).size(width = 180.dp, height = 296.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Spacer(Modifier.weight(1f))
                if (removeResult is RemoveResult.Success &&
                    removeResult.blobInfoWrapper.blobId == blobInfoWrapper.blobId
                ) {
                    Box(
                        modifier =
                            Modifier
                                .shimmer()
                                .background(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(8.dp),
                                ),
                    ) {}
                } else {
                    val userId = LocalFirebaseIdToken.current.user_id
                    blobInfoWrapper.asImageComposable(
                        userId = userId,
                        projectId = project.id.toString(),
                        Modifier
                            .widthIn(max = 160.dp)
                            .heightIn(max = 180.dp),
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
            Text(
                blobInfoWrapper.fileName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            blobInfoWrapper.updateTime?.let { updateTime ->
                Text(
                    "Updated: ${updateTime.asDateString()}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }

        val removeAsset = stringResource(Res.string.remove_asset)
        Column(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp),
        ) {
            Tooltip(removeAsset) {
                ComposeFlowIconButton(
                    onClick = {
                        deleteDialogOpen = true
                    },
                ) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = removeAsset,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (deleteDialogOpen) {
        onAnyDialogIsShown()
        val removeAsset = stringResource(Res.string.remove_asset)
        val remove = stringResource(Res.string.remove)
        val closeDialog = {
            deleteDialogOpen = false
            onAllDialogsClosed()
        }
        SimpleConfirmationDialog(
            text = "$removeAsset?",
            positiveText = remove,
            onCloseClick = {
                closeDialog()
            },
            onConfirmClick = {
                onDeleteAsset(blobInfoWrapper)
                closeDialog()
            },
        )
    }
}
