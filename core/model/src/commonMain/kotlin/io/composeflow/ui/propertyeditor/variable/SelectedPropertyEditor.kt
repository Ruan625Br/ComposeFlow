package io.composeflow.ui.propertyeditor.variable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.delete_transformation
import io.composeflow.invalid
import io.composeflow.invalid_input
import io.composeflow.invalid_output
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.EmptyProperty
import io.composeflow.model.property.PropertyTransformer
import io.composeflow.model.property.leadingIcon
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.no_valid_transformers
import io.composeflow.not_assignable_to
import io.composeflow.selected_type
import io.composeflow.transform_state
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.DropdownProperty
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectedPropertyEditor(
    project: Project,
    node: ComposeNode,
    acceptableType: ComposeFlowType,
    initialProperty: AssignableProperty?,
    isPropertyValid: Boolean,
    onValidPropertyEdited: (AssignableProperty) -> Unit,
) {
    val editedProperty by remember(initialProperty) {
        mutableStateOf(
            initialProperty ?: EmptyProperty
        )
    }
    val resolvedType by remember(initialProperty) {
        derivedStateOf {
            editedProperty.transformedValueType(project)
        }
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            Text(
                stringResource(Res.string.selected_type),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.padding(8.dp))

            if (resolvedType != ComposeFlowType.UnknownType()) {
                Text(
                    resolvedType.displayName(project),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            } else {
                Text(
                    stringResource(Res.string.invalid),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (isPropertyValid && resolvedType != ComposeFlowType.UnknownType()) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 4.dp).size(16.dp),
                )
            }
        }

        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            acceptableType = acceptableType,
            initialProperty = editedProperty,
            label = "",
            onValidPropertyChanged = { _, _ -> },
            leadingIcon = {
                ComposeFlowIcon(
                    imageVector = resolvedType.leadingIcon(),
                    contentDescription = null,
                )
            },
            modifier = Modifier,
            singleLine = false,
            destinationStateId = null,
            onInitializeProperty = null,
            editable = false,
        )
        if (!isPropertyValid) {
            Text(
                stringResource(Res.string.not_assignable_to) + acceptableType.displayName(project),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        LazyColumn {
            item {
                val transformersCandidates = PropertyTransformer.transformers(
                    fromType = editedProperty.valueType(project),
                    toType = if (editedProperty.propertyTransformers.isNotEmpty()) {
                        editedProperty.propertyTransformers[0].fromType()
                    } else {
                        null
                    },
                )
                AddTransformButton(
                    onAddTransform = {
                        editedProperty.propertyTransformers.add(
                            0,
                            transformersCandidates[0],
                        )
                        onValidPropertyEdited(editedProperty)
                    },
                    enabled = transformersCandidates.isNotEmpty(),
                )
            }
            itemsIndexed(editedProperty.propertyTransformers) { transformerIndex, transformer ->
                val hasValidInput = if (transformerIndex == 0) {
                    true
                } else {
                    editedProperty.propertyTransformers[transformerIndex - 1].toType() == transformer.fromType()
                }

                val hasValidOutput =
                    if (transformerIndex == editedProperty.propertyTransformers.lastIndex) {
                        true
                    } else {
                        editedProperty.propertyTransformers[transformerIndex + 1].fromType() == transformer.toType()
                    }

                Column {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (hasValidInput && hasValidOutput) {
                                    MaterialTheme.colorScheme.outlineVariant
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                shape = RoundedCornerShape(8.dp),
                            )
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val currentCandidates = PropertyTransformer.transformers(
                                    fromType = transformer.fromType(),
                                    toType = if (transformerIndex == editedProperty.propertyTransformers.lastIndex) {
                                        null
                                    } else {
                                        transformer.toType()
                                    },
                                )
                                DropdownProperty(
                                    items = currentCandidates,
                                    displayText = {
                                        Text(
                                            it.displayName(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 12.dp),
                                        )
                                    },
                                    dropDownMenuText = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(it.displayName())
                                            Text(
                                                " -> ",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.outline,
                                            )
                                            Text(
                                                text = it.toType().displayName(project),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.tertiary,
                                            )
                                        }
                                    },
                                    onValueChanged = { index, _ ->
                                        editedProperty.propertyTransformers[transformerIndex] =
                                            currentCandidates[index]
                                    },
                                    selectedIndex = currentCandidates.indexOfFirst {
                                        it::class == transformer::class
                                    },
                                )
                                Spacer(Modifier.weight(1f))

                                val deleteTransformation =
                                    stringResource(Res.string.delete_transformation)
                                Tooltip(deleteTransformation) {
                                    ComposeFlowIconButton(onClick = {
                                        editedProperty.propertyTransformers.removeAt(
                                            transformerIndex,
                                        )
                                        onValidPropertyEdited(editedProperty)
                                    }) {
                                        ComposeFlowIcon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = deleteTransformation,
                                            tint = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                }
                            }

                            transformer.Editor(
                                project = project,
                                node = node,
                                onTransformerEdited = {
                                    editedProperty.propertyTransformers[transformerIndex] = it
                                    onValidPropertyEdited(editedProperty)
                                },
                                modifier = Modifier,
                            )
                        }
                    }
                    if (!hasValidInput) {
                        Text(
                            text = stringResource(Res.string.invalid_input),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                    if (!hasValidOutput) {
                        Text(
                            text = stringResource(Res.string.invalid_output),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }

                    val transformersCandidates = PropertyTransformer.transformers(
                        fromType = editedProperty.propertyTransformers[transformerIndex].toType(),
                        toType = if (editedProperty.propertyTransformers.size > transformerIndex + 1) {
                            editedProperty.propertyTransformers[transformerIndex + 1].fromType()
                        } else {
                            null
                        },
                    )
                    AddTransformButton(
                        onAddTransform = {
                            editedProperty.propertyTransformers.add(
                                transformerIndex + 1,
                                transformersCandidates[0],
                            )
                            onValidPropertyEdited(editedProperty)
                        },
                        enabled = transformersCandidates.isNotEmpty(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTransformButton(
    enabled: Boolean,
    onAddTransform: () -> Unit,
) {
    val contentsDesc = if (enabled) {
        stringResource(Res.string.transform_state)
    } else {
        stringResource(Res.string.no_valid_transformers)
    }

    Tooltip(contentsDesc) {
        ComposeFlowIconButton(
            onClick = {
                onAddTransform()
            },
            modifier = Modifier.padding(start = 8.dp),
            enabled = enabled,
        ) {
            ComposeFlowIcon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = contentsDesc,
                tint = if (enabled) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
        }
    }
}
