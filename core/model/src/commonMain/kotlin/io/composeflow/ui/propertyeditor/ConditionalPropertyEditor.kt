package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_else_if_block
import io.composeflow.cancel
import io.composeflow.conditional_property_for
import io.composeflow.confirm
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.ConditionalProperty
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.switch.ComposeFlowSwitch
import org.jetbrains.compose.resources.stringResource

@Composable
fun <P : AssignableProperty> ConditionalPropertyEditor(
    project: Project,
    node: ComposeNode,
    onCloseClick: () -> Unit,
    onValidPropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    defaultValue: P,
    initialProperty: ConditionalProperty? = null,
) {
    var conditionalProperty by remember {
        mutableStateOf(
            initialProperty
                ?: ConditionalProperty(defaultValue = defaultValue),
        )
    }
    Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
        Text(
            buildAnnotatedString {
                val textStyle = MaterialTheme.typography.bodyLarge
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = textStyle.fontSize,
                        fontWeight = textStyle.fontWeight,
                        fontStyle = textStyle.fontStyle,
                    ),
                ) {
                    append(stringResource(Res.string.conditional_property_for))
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = textStyle.fontSize,
                            fontWeight = textStyle.fontWeight,
                            fontStyle = textStyle.fontStyle,
                        ),
                    ) {
                        append(defaultValue.valueType(project).displayName(project))
                    }
                }
            },
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
        ) {
            item {
                IfBlock(
                    project = project,
                    node = node,
                    initialIfThen = conditionalProperty.ifThen,
                    defaultValue = defaultValue,
                    onIfExpressionChanged = {
                        conditionalProperty = conditionalProperty.copyWith(
                            newIfThenBlock =
                            conditionalProperty.ifThen.copy(ifExpression = it),
                        )
                    },
                    onMetIfTrueChanged = {
                        conditionalProperty = conditionalProperty.copyWith(
                            conditionalProperty.ifThen.copy(metIfTrue = it),
                        )
                    },
                    onThenValueChanged = {
                        conditionalProperty = conditionalProperty.copyWith(
                            newIfThenBlock =
                            conditionalProperty.ifThen.copy(thenValue = it),
                        )
                    },
                    elseIf = false,
                )
            }

            itemsIndexed(conditionalProperty.elseIfBlocks) { i, elseIfBlock ->
                AddElseIfBlockContainer(onAddClicked = {
                    conditionalProperty.elseIfBlocks.add(
                        i,
                        ConditionalProperty.IfThenBlock(
                            thenValue = defaultValue,
                        ),
                    )
                })

                IfBlock(
                    project = project,
                    node = node,
                    initialIfThen = elseIfBlock,
                    defaultValue = defaultValue,
                    onIfExpressionChanged = {
                        conditionalProperty.elseIfBlocks[i] =
                            conditionalProperty.elseIfBlocks[i].copy(ifExpression = it)
                    },
                    onMetIfTrueChanged = {
                        conditionalProperty.elseIfBlocks[i] =
                            conditionalProperty.elseIfBlocks[i].copy(metIfTrue = it)
                    },
                    onThenValueChanged = {
                        conditionalProperty.elseIfBlocks[i] =
                            conditionalProperty.elseIfBlocks[i].copy(thenValue = it)
                    },
                    elseIf = true,
                    onDeleteBlock = {
                        conditionalProperty.elseIfBlocks.removeAt(i)
                    },
                )
            }

            item {
                AddElseIfBlockContainer(onAddClicked = {
                    conditionalProperty.elseIfBlocks.add(
                        ConditionalProperty.IfThenBlock(
                            thenValue = defaultValue,
                        ),
                    )
                })
            }

            item {
                Column(
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp),
                    )
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "ELSE",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(16.dp),
                        )

                        defaultValue.Editor(
                            project = project,
                            node = node,
                            initialProperty = conditionalProperty.elseBlock.value,
                            label = "",
                            onValidPropertyChanged = { property, _ ->
                                @Suppress("UNCHECKED_CAST")
                                conditionalProperty = conditionalProperty.copyWith(
                                    newElseBlock =
                                    conditionalProperty.elseBlock.copy(value = property as P),
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            destinationStateId = null,
                            onInitializeProperty = null,
                            validateInput = null,
                            editable = true,
                            functionScopeProperties = emptyList(),
                        )
                    }
                }
            }
        }
        HorizontalDivider()
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    onCloseClick()
                },
                modifier = Modifier.padding(end = 16.dp),
            ) {
                Text(stringResource(Res.string.cancel))
            }
            OutlinedButton(
                onClick = {
                    onCloseClick()
                    onValidPropertyChanged(conditionalProperty, null)
                },
                enabled = conditionalProperty.isValid(),
                modifier = Modifier.padding(end = 16.dp),
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }
}

@Composable
private fun AddElseIfBlockContainer(
    onAddClicked: () -> Unit,
) {
    val addElseIf = stringResource(Res.string.add_else_if_block)
    Tooltip(addElseIf) {
        ComposeFlowIconButton(
            onClick = {
                onAddClicked()
            },
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            ComposeFlowIcon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = addElseIf,
                tint = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun <P : AssignableProperty> IfBlock(
    project: Project,
    node: ComposeNode,
    initialIfThen: ConditionalProperty.IfThenBlock,
    defaultValue: P,
    onIfExpressionChanged: (AssignableProperty) -> Unit,
    onMetIfTrueChanged: (Boolean) -> Unit,
    onThenValueChanged: (P) -> Unit,
    elseIf: Boolean = false,
    onDeleteBlock: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp),
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(88.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = if (elseIf) "ELSE IF" else "IF",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp),
                )
                AssignableBooleanPropertyEditor(
                    project = project,
                    node = node,
                    initialProperty = initialIfThen.ifExpression,
                    onValidPropertyChanged = { property, _ ->
                        onIfExpressionChanged(property)
                    },
                    onInitializeProperty = {
                        onIfExpressionChanged(BooleanProperty.Empty)
                    },
                    modifier = Modifier.width(360.dp)
                        .padding(top = 12.dp),
                )
                Text(
                    text = "IS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp),
                )

                ComposeFlowSwitch(
                    checked = initialIfThen.metIfTrue,
                    onCheckedChange = {
                        onMetIfTrueChanged(it)
                    },
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "THEN",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp),
                )

                defaultValue.Editor(
                    project = project,
                    node = node,
                    initialProperty = initialIfThen.thenValue,
                    label = "",
                    onValidPropertyChanged = { property, _ ->
                        @Suppress("UNCHECKED_CAST")
                        onThenValueChanged(property as P)
                    },
                    modifier = Modifier,
                    destinationStateId = null,
                    onInitializeProperty = null,
                    validateInput = null,
                    editable = true,
                    functionScopeProperties = emptyList(),
                )
            }
        }

        if (elseIf) {
            ComposeFlowIconButton(
                onClick = {
                    onDeleteBlock?.let { it() }
                },
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                ComposeFlowIcon(
                    imageVector = Icons.Outlined.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null,
                )
            }
        }
    }
}
