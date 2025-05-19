package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.model.parameter.BoxTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.ParamInspectorHeaderRow
import io.composeflow.ui.inspector.propertyeditor.AlignmentPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.switch.ComposeFlowSwitch

@Composable
fun BoxParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val params = node.trait.value as BoxTrait
    Column {
        AlignmentPropertyEditor(
            initialValue = params.contentAlignment,
            label = "Content alignment",
            onAlignmentSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    params.copy(contentAlignment = it),
                )
            },
        )
        PropagateMinConstraintsInspector(node, params, composeNodeCallbacks)
    }
}

@Composable
private fun PropagateMinConstraintsInspector(
    node: ComposeNode,
    boxTrait: BoxTrait,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    Column(modifier = Modifier.hoverOverlay().padding(vertical = 4.dp)) {
        ParamInspectorHeaderRow(
            label = "Propagate min constraints",
        )
        val checked = boxTrait.propagateMinConstraints == true

        ComposeFlowSwitch(
            checked = checked,
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    boxTrait.copy(propagateMinConstraints = !checked),
                )
            },
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
