package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.parameter.IconAssetType
import io.composeflow.model.parameter.IconButtonTrait
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.ColorProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableColorPropertyEditor
import io.composeflow.ui.propertyeditor.DropdownProperty
import io.composeflow.ui.utils.asIconComposable
import kotlinx.coroutines.launch

// Mostly identical with IconParamsInspector.
@Composable
fun IconButtonParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val iconButtonTrait = node.trait.value as IconButtonTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        LabeledBorderBox(
            label = "Icon type",
            modifier = Modifier.hoverOverlay()
        ) {
            DropdownProperty(
                project = project,
                items = IconAssetType.entries,
                onValueChanged = { index, _ ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        iconButtonTrait.copy(assetType = IconAssetType.entries[index]),
                    )
                },
                selectedIndex = iconButtonTrait.assetType.ordinal,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        when (iconButtonTrait.assetType) {
            IconAssetType.Material -> {
                IconPropertyEditor(
                    label = "Icon",
                    onIconSelected = {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            iconButtonTrait.copy(imageVectorHolder = it)
                        )
                    },
                    currentIcon = iconButtonTrait.imageVectorHolder?.imageVector,
                    modifier = Modifier
                        .hoverOverlay(),
                )
            }

            IconAssetType.CustomAsset -> {
                LabeledBorderBox(
                    label = "Asset",
                    modifier = Modifier.hoverOverlay()
                        .fillMaxWidth()
                ) {
                    val iconAssets = project.assetHolder.icons
                    val userId = LocalFirebaseIdToken.current.user_id
                    DropdownProperty(
                        project = project,
                        items = iconAssets,
                        dropDownMenuText = {
                            Row {
                                it.asIconComposable(
                                    userId = userId,
                                    projectId = project.id,
                                )

                                Text(
                                    it.fileName,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        },
                        displayText = {
                            iconButtonTrait.blobInfoWrapper?.let {
                                Text(
                                    it.fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        },
                        onValueChanged = { index, _ ->
                            composeNodeCallbacks.onTraitUpdated(
                                node,
                                iconButtonTrait.copy(blobInfoWrapper = iconAssets[index]),
                            )
                        },
                        selectedItem = iconButtonTrait.blobInfoWrapper,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }

        AssignableColorPropertyEditor(
            project = project,
            node = node,
            label = "Color",
            acceptableType = ComposeFlowType.Color(),
            initialProperty = iconButtonTrait.tint
                ?: ColorProperty.ColorIntrinsicValue(
                    ColorWrapper(
                        themeColor = null,
                        color = LocalContentColor.current
                    )
                ),
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    iconButtonTrait.copy(tint = property),
                    lazyListSource,
                )
                result.errorMessages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(node, iconButtonTrait.copy(tint = null))
            },
            modifier = Modifier
                .hoverOverlay()
                .fillMaxWidth(),
        )
    }
}
