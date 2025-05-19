package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.composeflow.model.parameter.TabRowTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun TabRowParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val params = node.trait.value as TabRowTrait
    Column {
        BooleanPropertyEditor(
            checked = params.scrollable,
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, params.copy(scrollable = it))
            },
            label = "Scrollable"
        )
    }
}
