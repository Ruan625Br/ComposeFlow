package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.composeflow.model.parameter.GoogleSignInButtonTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun GoogleSignInButtonParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as GoogleSignInButtonTrait
    Column {
        BooleanPropertyEditor(
            checked = trait.iconOnly,
            onCheckedChange = {
                composeNodeCallbacks.onTraitUpdated(node, trait.copy(iconOnly = it))
            },
            label = "Icon only",
        )
    }
}
