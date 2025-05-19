package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.DpValidator
import io.composeflow.editor.validator.NotEmptyNotLessThanZeroIntValidator
import io.composeflow.model.parameter.LazyVerticalGridTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.ui.inspector.lazylist.GridCellsInspector
import io.composeflow.ui.inspector.propertyeditor.ArrangementVerticalPropertyEditor
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor

@Composable
fun LazyVerticalGridParamsInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val trait = node.trait.value as LazyVerticalGridTrait
    Column {
        GridCellsInspector(
            node = node,
            dropdownLabel = "Columns",
            onGridCellsUpdated = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(lazyGridCells = it),
                )
            }
        )
        Row {
            BasicEditableTextProperty(
                initialValue = trait.defaultChildNumOfItems.toString(),
                label = "Default # of child items",
                validateInput = NotEmptyNotLessThanZeroIntValidator()::validate,
                onValidValueChanged = {
                    trait.defaultChildNumOfItems = it.toInt()
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        trait,
                    )
                },
                modifier = Modifier.hoverOverlay().weight(1f),
            )
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
                modifier = Modifier.hoverOverlay().weight(1f),
            )
        }
        ArrangementVerticalPropertyEditor(
            initialValue = trait.verticalArrangement,
            onArrangementSelected = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    trait.copy(
                        verticalArrangement = it,
                    ),
                )
            },
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
    }
}
