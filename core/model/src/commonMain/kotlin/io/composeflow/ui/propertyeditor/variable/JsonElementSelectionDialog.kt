package io.composeflow.ui.propertyeditor.variable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.StringProperty
import io.composeflow.ui.jewel.SingleSelectionLazyTree
import io.composeflow.ui.jsonpath.createJsonTreeWithJsonPath
import io.composeflow.ui.popup.PositionCustomizablePopup
import kotlinx.serialization.json.JsonArray
import org.jetbrains.jewel.foundation.lazy.tree.rememberTreeState

@Composable
fun JsonElementSelectionDialog(
    apiDefinition: ApiDefinition,
    onCloseDialog: () -> Unit,
    onElementClicked: (AssignableProperty) -> Unit,
    modifier: Modifier = Modifier,
) {
    PositionCustomizablePopup(
        onDismissRequest = onCloseDialog
    ) {
        Surface(
            modifier = modifier.size(600.dp, 700.dp)
        ) {
            (apiDefinition.exampleJsonResponse?.jsonElement as? JsonArray)?.get(0)
                ?.let { firstItem ->
                    val tree = createJsonTreeWithJsonPath(firstItem.toString())
                    val treeState = rememberTreeState()
                    var initiallyExpanded by remember { mutableStateOf(false) }
                    SingleSelectionLazyTree(
                        tree = tree,
                        treeState = treeState,
                        onSelectionChange = {
                            if (it.isNotEmpty()) {
                                val element = it.first()
                                onElementClicked(
                                    StringProperty.ValueByJsonPath(
                                        jsonPath = element.data.jsonPath,
                                        jsonElement = firstItem,
                                    ),
                                )
                            }
                        },
                        modifier = Modifier
                            .onGloballyPositioned {
                                if (!initiallyExpanded) {
                                    treeState.openNodes(tree.roots.map { it.id })
                                    initiallyExpanded = true
                                }
                            }
                    ) {
                        val element = it.data
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = element.displayName ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
        }
    }
}