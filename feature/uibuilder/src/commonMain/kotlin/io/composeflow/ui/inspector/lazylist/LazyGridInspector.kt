package io.composeflow.ui.inspector.lazylist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.DpValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.model.parameter.LazyHorizontalGridTrait
import io.composeflow.model.parameter.LazyVerticalGridTrait
import io.composeflow.model.parameter.lazylist.LazyGridCells
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty

@Composable
fun GridCellsInspector(
    node: ComposeNode,
    dropdownLabel: String,
    onGridCellsUpdated: (LazyGridCells) -> Unit,
) {
    val params = when (node.trait.value) {
        is LazyVerticalGridTrait -> {
            node.trait.value as LazyVerticalGridTrait
        }

        is LazyHorizontalGridTrait -> {
            node.trait.value as LazyHorizontalGridTrait
        }

        else -> {
            null
        }
    }
    if (params == null) return
    Row {
        BasicDropdownPropertyEditor(
            items = LazyGridCells.entries(),
            onValueChanged = { _, item ->
                onGridCellsUpdated(item)
            },
            label = dropdownLabel,
            selectedItem = params.lazyGridCells,
            modifier = Modifier.weight(1f).hoverOverlay()
        )
        Column(modifier = Modifier.weight(1f).hoverOverlay()) {
            when (val gridCells = params.lazyGridCells) {
                is LazyGridCells.Adaptive -> {
                    BasicEditableTextProperty(
                        initialValue = gridCells.minSize.value.toInt().toString(),
                        onValidValueChanged = {
                            onGridCellsUpdated(LazyGridCells.Adaptive(it.toInt().dp))
                        },
                        label = "min size",
                        validateInput = DpValidator()::validate,
                    )
                }

                is LazyGridCells.Fixed -> {
                    BasicEditableTextProperty(
                        initialValue = gridCells.count.toString(),
                        onValidValueChanged = {
                            onGridCellsUpdated(LazyGridCells.Fixed(it.toInt()))
                        },
                        label = "count",
                        validateInput = IntValidator(
                            allowLessThanZero = false,
                        )::validate,
                    )
                }

                is LazyGridCells.FixedSize -> {
                    BasicEditableTextProperty(
                        initialValue = gridCells.size.value.toInt().toString(),
                        onValidValueChanged = {
                            onGridCellsUpdated(LazyGridCells.FixedSize(it.toInt().dp))
                        },
                        label = "size",
                        validateInput = DpValidator()::validate,
                    )
                }
            }
        }
    }
}