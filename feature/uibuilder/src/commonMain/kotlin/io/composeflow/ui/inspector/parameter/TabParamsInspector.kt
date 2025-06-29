package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.parameter.TabTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.StringProperty
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty

@Composable
fun TabParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val tabTrait = node.trait.value as TabTrait
    Column {
        BasicEditableTextProperty(
            label = "Text",
            initialValue = tabTrait.text?.displayText(project) ?: "",
            onValidValueChanged = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    tabTrait.copy(
                        text =
                            if (it.isEmpty()) null else StringProperty.StringIntrinsicValue(it),
                    ),
                )
            },
            modifier = Modifier.hoverOverlay().fillMaxWidth(),
        )
        IconPropertyEditor(
            label = "Icon",
            onIconSelected = {
                composeNodeCallbacks.onTraitUpdated(node, tabTrait.copy(icon = it))
            },
            currentIcon = tabTrait.icon?.imageVector,
            onIconDeleted = {
                composeNodeCallbacks.onTraitUpdated(node, tabTrait.copy(icon = null))
            },
            modifier =
                Modifier
                    .hoverOverlay()
                    .padding(horizontal = 8.dp),
        )
    }
}
