package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.composeflow.model.parameter.RowTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.propertyeditor.AlignmentVerticalPropertyEditor
import io.composeflow.ui.inspector.propertyeditor.ArrangementHorizontalPropertyEditor

@Composable
fun RowParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as RowTrait
    Column {
        ArrangementHorizontalPropertyEditor(
            initialValue = trait.horizontalArrangement,
            onArrangementSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(horizontalArrangement = it),
                )
            },
        )
        AlignmentVerticalPropertyEditor(
            initialValue = trait.verticalAlignment,
            onAlignmentSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(verticalAlignment = it),
                )
            },
            label = "Vertical alignment",
        )
    }
}
