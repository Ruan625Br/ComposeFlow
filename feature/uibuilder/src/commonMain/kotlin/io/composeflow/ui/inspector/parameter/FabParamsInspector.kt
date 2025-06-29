package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.FabElevationWrapper
import io.composeflow.model.parameter.FabPositionWrapper
import io.composeflow.model.parameter.FabTrait
import io.composeflow.model.parameter.FabType
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.ColorProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableColorPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import kotlinx.coroutines.launch

@Composable
fun FabParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val fabTrait = node.trait.value as FabTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        IconPropertyEditor(
            label = "Image vector",
            onIconSelected = {
                composeNodeCallbacks.onTraitUpdated(node, fabTrait.copy(imageVectorHolder = it))
            },
            currentIcon = fabTrait.imageVectorHolder.imageVector,
            modifier =
                Modifier
                    .hoverOverlay(),
        )

        AssignableColorPropertyEditor(
            project = project,
            node = node,
            label = "Container color",
            acceptableType = ComposeFlowType.Color(),
            initialProperty =
                fabTrait.containerColorWrapper
                    ?: ColorProperty.ColorIntrinsicValue(
                        ColorWrapper(
                            themeColor = null,
                            color = FloatingActionButtonDefaults.containerColor,
                        ),
                    ),
            onValidPropertyChanged = { property, lazyListSource ->
                val result =
                    composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        fabTrait.copy(containerColorWrapper = property),
                        lazyListSource,
                    )
                result.errorMessages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    fabTrait.copy(containerColorWrapper = null),
                )
            },
            modifier =
                Modifier
                    .hoverOverlay()
                    .fillMaxWidth(),
        )

        AssignableColorPropertyEditor(
            project = project,
            node = node,
            label = "Content color",
            acceptableType = ComposeFlowType.Color(),
            initialProperty =
                fabTrait.contentColorWrapper
                    ?: ColorProperty.ColorIntrinsicValue(
                        ColorWrapper(
                            themeColor = null,
                            color = contentColorFor(FloatingActionButtonDefaults.containerColor),
                        ),
                    ),
            onValidPropertyChanged = { property, lazyListSource ->
                val result =
                    composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        fabTrait.copy(contentColorWrapper = property),
                        lazyListSource,
                    )
                result.errorMessages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(node, fabTrait.copy(contentColorWrapper = null))
            },
            modifier =
                Modifier
                    .hoverOverlay()
                    .fillMaxWidth(),
        )

        Row {
            BasicDropdownPropertyEditor(
                project = project,
                items = FabPositionWrapper.entries,
                selectedItem = fabTrait.fabPositionWrapper,
                label = "Position",
                onValueChanged = { _, selected ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        fabTrait.copy(
                            fabPositionWrapper = selected,
                        ),
                    )
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .hoverOverlay(),
            )
            BasicDropdownPropertyEditor(
                project = project,
                items = FabElevationWrapper.entries,
                selectedItem = fabTrait.fabElevationWrapper,
                label = "Elevation",
                onValueChanged = { _, selected ->
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        fabTrait.copy(
                            fabElevationWrapper = selected,
                        ),
                    )
                },
                modifier =
                    Modifier
                        .weight(1f)
                        .hoverOverlay(),
            )
        }

        BasicDropdownPropertyEditor(
            project = project,
            items = FabType.entries,
            label = "Fab type",
            selectedIndex = fabTrait.fabType.ordinal,
            onValueChanged = { _, item ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    fabTrait.copy(
                        fabType = item,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
