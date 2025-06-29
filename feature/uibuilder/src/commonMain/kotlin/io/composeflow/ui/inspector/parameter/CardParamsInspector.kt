package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.composeflow.model.parameter.CardTrait
import io.composeflow.model.parameter.CardType
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor

@Composable
fun CardParamsInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val cardTrait = node.trait.value as CardTrait
    Column {
        BasicDropdownPropertyEditor(
            project = project,
            items = CardType.entries,
            label = "Card type",
            selectedIndex = cardTrait.cardType.ordinal,
            onValueChanged = { _, item ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    cardTrait.copy(
                        cardType = item,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
    }
}
