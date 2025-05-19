package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.DpValidator
import io.composeflow.model.parameter.DividerTrait
import io.composeflow.model.parameter.HorizontalDividerTrait
import io.composeflow.model.parameter.VerticalDividerTrait
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.parameter.wrapper.Material3ColorWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.ColorProperty
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableColorPropertyEditor
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty
import kotlinx.coroutines.launch

@Composable
fun DividerParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val dividerTrait = node.trait.value as DividerTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        BasicEditableTextProperty(
            initialValue = dividerTrait.thickness?.value?.toInt()?.toString() ?: "",
            label = "Thickness",
            validateInput = DpValidator(allowEmpty = true)::validate,
            onValidValueChanged = {
                val newValue = if (it.isEmpty()) null else it.toInt().dp
                when (dividerTrait) {
                    is HorizontalDividerTrait -> {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            dividerTrait.copy(thickness = newValue)
                        )
                    }

                    is VerticalDividerTrait -> {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            dividerTrait.copy(thickness = newValue)
                        )
                    }

                    else -> {
                        // No action needed for other divider trait types
                    }
                }
            },
        )
        AssignableColorPropertyEditor(
            project = project,
            node = node,
            label = "Color",
            acceptableType = ComposeFlowType.Color(),
            initialProperty = dividerTrait.color
                ?: ColorProperty.ColorIntrinsicValue(
                    ColorWrapper(
                        themeColor = null,
                        color = DividerDefaults.color,
                    ),
                ),
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    when (dividerTrait) {
                        is HorizontalDividerTrait -> dividerTrait.copy(color = property)
                        is VerticalDividerTrait -> dividerTrait.copy(color = property)
                        else -> dividerTrait // Should not happen for sealed class
                    },
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            onInitializeProperty = {
                val defaultColor = ColorProperty.ColorIntrinsicValue(
                    ColorWrapper(
                        Material3ColorWrapper.OnSurfaceVariant
                    )
                )
                when (dividerTrait) {
                    is HorizontalDividerTrait -> {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            dividerTrait.copy(color = defaultColor)
                        )
                    }

                    is VerticalDividerTrait -> {
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            dividerTrait.copy(color = defaultColor)
                        )
                    }

                    else -> {
                        // No action needed for other divider trait types
                    }
                }
            },
            modifier = Modifier
                .hoverOverlay()
                .fillMaxWidth(),
        )
    }
}
