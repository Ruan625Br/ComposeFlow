package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.composeflow.model.parameter.ColumnTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.propertyeditor.AlignmentHorizontalPropertyEditor
import io.composeflow.ui.inspector.propertyeditor.ArrangementVerticalPropertyEditor

@Composable
fun ColumnParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as ColumnTrait
    Column {
        ArrangementVerticalPropertyEditor(
            initialValue = trait.verticalArrangementWrapper,
            onArrangementSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        verticalArrangementWrapper = it,
                    ),
                )
            },
        )
        AlignmentHorizontalPropertyEditor(
            initialValue = trait.horizontalAlignmentWrapper,
            onAlignmentSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        horizontalAlignmentWrapper = it,
                    ),
                )
            },
            label = "Horizontal alignment",
        )
    }
}
