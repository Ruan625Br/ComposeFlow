package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.NavigationDrawerItemTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.StringProperty
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawerItemParamsInspector(
    node: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as NavigationDrawerItemTrait
    val coroutineScope = rememberCoroutineScope()
    val onShowSnackbar = LocalOnShowSnackbar.current
    Column {
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            label = "Label",
            initialProperty = trait.labelProperty,
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        labelProperty = StringProperty.StringIntrinsicValue(
                            ""
                        )
                    ),
                )
            },
            onValidPropertyChanged = { property, lazyListSource ->
                val result = composeNodeCallbacks.onParamsUpdatedWithLazyListSource(
                    node,
                    trait.copy(labelProperty = property),
                    lazyListSource,
                )
                result.messages.forEach {
                    coroutineScope.launch {
                        onShowSnackbar(it, null)
                    }
                }
            },
            modifier = Modifier.hoverOverlay(),
            singleLine = true,
        )
        IconPropertyEditor(
            label = "Icon",
            onIconSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(imageVectorHolder = it)
                )
            },
            currentIcon = trait.imageVectorHolder?.imageVector,
            modifier = Modifier
                .hoverOverlay(),
        )
    }
}
