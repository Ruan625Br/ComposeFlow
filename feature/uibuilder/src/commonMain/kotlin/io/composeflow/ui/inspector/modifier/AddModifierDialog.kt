package io.composeflow.ui.inspector.modifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.composeflow.getSubsequenceMatchIndices
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.textfield.SmallOutlinedTextField

@Composable
fun AddModifierDialog(
    modifiers: List<Pair<Int, ModifierWrapper>>,
    onModifierSelected: (Int) -> Unit,
    onCloseClick: () -> Unit,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
        content = {
            AddModifierDialogContent(
                modifiers = modifiers,
                onModifierSelected = onModifierSelected,
                onCloseClick = onCloseClick,
            )
        },
    )
}

@Composable
fun AddModifierDialogContent(
    modifiers: List<Pair<Int, ModifierWrapper>>,
    onModifierSelected: (Int) -> Unit,
    onCloseClick: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxSize()
                .size(width = 800.dp, height = 960.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(all = 16.dp),
        ) {
            Text(
                text = "Add modifier",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            )

            val focusRequester = remember { FocusRequester() }
            var searchText by remember { mutableStateOf("") }
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            // Zip with the indices with the indices that match the sub sequence of the search text
            val filteredModifiers: List<Pair<Pair<Int, ModifierWrapper>, Set<Int>>> =
                if (searchText.isBlank()) {
                    modifiers.map {
                        it to emptySet()
                    }
                } else {
                    modifiers.mapNotNull {
                        val display = it.second.displayName().lowercase()
                        val indices = display.getSubsequenceMatchIndices(searchText.lowercase())
                        if (indices.isNotEmpty()) it to indices else null
                    }
                }

            SmallOutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                leadingIcon = {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search modifier",
                    )
                },
                singleLine = true,
                modifier =
                    Modifier
                        .width(280.dp)
                        .padding(start = 16.dp)
                        .focusRequester(focusRequester)
                        .onKeyEvent {
                            if (it.key == Key.Enter && filteredModifiers.size == 1) {
                                onModifierSelected(filteredModifiers[0].first.first)
                                true
                            } else {
                                false
                            }
                        },
            )
            Spacer(Modifier.size(8.dp))

            val filteredCategories =
                filteredModifiers
                    .map {
                        it.first.second.category()
                    }.distinct()

            LazyVerticalGrid(
                columns = GridCells.Adaptive(148.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                filteredCategories.forEach { category ->
                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Text(
                            text = category.name,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                    items(items = filteredModifiers.filter { it.first.second.category() == category }) { pair ->
                        Tooltip(pair.first.second.tooltipText()) {
                            AssistChip(
                                onClick = {
                                    onModifierSelected(pair.first.first)
                                    onCloseClick()
                                },
                                label = {
                                    HighlightedText(
                                        fullText = pair.first.second.displayName(),
                                        indicesToHighlight = pair.second,
                                    )
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(end = 8.dp, bottom = 8.dp),
                            )
                        }
                    }
                    item(span = {
                        GridItemSpan(maxLineSpan)
                    }) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = {
                    onCloseClick()
                },
                modifier = Modifier.padding(end = 16.dp),
            ) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun HighlightedText(
    fullText: String,
    indicesToHighlight: Set<Int>,
    normalColor: Color = MaterialTheme.colorScheme.onSurface,
    highlightColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    val annotated =
        buildAnnotatedString {
            fullText.forEachIndexed { index, char ->
                val style =
                    if (index in indicesToHighlight) {
                        SpanStyle(color = highlightColor)
                    } else {
                        SpanStyle(color = normalColor)
                    }
                withStyle(style) {
                    append(char)
                }
            }
        }

    Text(text = annotated)
}
