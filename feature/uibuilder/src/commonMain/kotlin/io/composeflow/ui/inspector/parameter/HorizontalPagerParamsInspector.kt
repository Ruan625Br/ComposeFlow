package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.DpValidator
import io.composeflow.model.parameter.HorizontalPagerTrait
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.parameter.wrapper.Material3ColorWrapper
import io.composeflow.model.parameter.wrapper.SnapPositionWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.ColorProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.inspector.propertyeditor.AlignmentVerticalPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableColorPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import kotlinx.coroutines.launch

@Composable
fun HorizontalPagerParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as HorizontalPagerTrait
    val coroutineScope = rememberCoroutineScope()
    Column {
        Row {
            BasicEditableTextProperty(
                initialValue = trait.contentPadding?.value?.toInt()?.toString() ?: "",
                label = "Content padding",
                validateInput = DpValidator()::validate,
                onValidValueChanged = {
                    val newValue = if (it.isEmpty()) 0.dp else it.toInt().dp
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        trait.copy(contentPadding = newValue),
                    )
                },
                modifier = Modifier.weight(1f).hoverOverlay(),
            )

            BasicEditableTextProperty(
                initialValue = trait.pageSpacing?.value?.toInt()?.toString() ?: "",
                label = "Page spacing",
                validateInput = DpValidator()::validate,
                onValidValueChanged = {
                    val newValue = if (it.isEmpty()) 0.dp else it.toInt().dp
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        trait.copy(pageSpacing = newValue),
                    )
                },
                modifier = Modifier.weight(1f).hoverOverlay(),
            )
        }

        AlignmentVerticalPropertyEditor(
            initialValue = trait.verticalAlignment,
            onAlignmentSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        verticalAlignment = it,
                    ),
                )
            },
            label = "Vertical alignment",
        )
        BasicDropdownPropertyEditor(
            project = project,
            items = SnapPositionWrapper.entries,
            onValueChanged = { _, item ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        snapPositionWrapper = item,
                    ),
                )
            },
            label = "Snap position",
            selectedItem = trait.snapPositionWrapper,
            modifier = Modifier.hoverOverlay()
        )
        Row {
            BooleanPropertyEditor(
                checked = trait.reverseLayout ?: false,
                label = "Reverse layout",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(node, trait.copy(reverseLayout = it))
                },
                modifier = Modifier.hoverOverlay().weight(1f),
            )
            BooleanPropertyEditor(
                checked = trait.userScrollEnabled ?: true,
                label = "User scroll enabled",
                onCheckedChange = {
                    composeNodeCallbacks.onTraitUpdated(node, trait.copy(userScrollEnabled = it))
                },
                modifier = Modifier.hoverOverlay().weight(1f),
            )
        }

        BooleanPropertyEditor(
            checked = trait.showIndicator,
            label = "Show indicator",
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, trait.copy(showIndicator = it))
            },
            modifier = Modifier.hoverOverlay(),
        )
        val onShowSnackbar = LocalOnShowSnackbar.current
        if (trait.showIndicator) {
            AssignableColorPropertyEditor(
                project = project,
                node = node,
                label = "Indicator selected color",
                acceptableType = ComposeFlowType.Color(),
                initialProperty = trait.indicatorSelectedColor,
                onValidPropertyChanged = { property, lazyListSource ->
                    val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        trait.copy(indicatorSelectedColor = property),
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
                        trait.copy(
                            indicatorSelectedColor = ColorProperty.ColorIntrinsicValue(
                                value = ColorWrapper(themeColor = Material3ColorWrapper.OnSurface)
                            )
                        )
                    )
                },
                modifier = Modifier
                    .hoverOverlay()
                    .fillMaxWidth(),
            )
            AssignableColorPropertyEditor(
                project = project,
                node = node,
                label = "Indicator unselected color",
                acceptableType = ComposeFlowType.Color(),
                initialProperty = trait.indicatorUnselectedColor,
                onValidPropertyChanged = { property, lazyListSource ->
                    val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                        node,
                        trait.copy(indicatorUnselectedColor = property),
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
                        trait.copy(
                            indicatorSelectedColor = ColorProperty.ColorIntrinsicValue(
                                value = ColorWrapper(themeColor = Material3ColorWrapper.SurfaceVariant)
                            )
                        )
                    )
                },
                modifier = Modifier
                    .hoverOverlay()
                    .fillMaxWidth(),
            )
        }
    }
}
