package io.composeflow.ui.palette

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.model.palette.PaletteDraggable
import io.composeflow.model.palette.PaletteNodeCallbacks
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.palette.emptyPaletteNodeCallbacks
import io.composeflow.model.parameter.ColumnTrait
import io.composeflow.model.parameter.ComposeTrait
import io.composeflow.model.parameter.entries
import io.composeflow.model.project.Project
import io.composeflow.palette_icon_for
import io.composeflow.ui.Tooltip
import io.composeflow.ui.common.ComposeFlowTheme
import io.composeflow.ui.draggableFromPalette
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.switchByHovered
import io.composeflow.ui.utils.TreeExpanderInverse
import io.composeflow.ui.zoomablecontainer.ZoomableContainerStateHolder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

const val PALETTE_TEST_TAG = "Palette"

@Composable
fun PaletteTab(
    project: Project,
    paletteNodeCallbacks: PaletteNodeCallbacks,
    zoomableContainerStateHolder: ZoomableContainerStateHolder,
    modifier: Modifier = Modifier,
) {
    @Composable
    fun PaletteItem(paletteDraggable: PaletteDraggable) {
        @Composable
        fun Icon(modifier: Modifier = Modifier) =
            run {
                PaletteIcon(
                    modifier = modifier.fillMaxSize(),
                    imageVector = paletteDraggable.icon(),
                    iconText = paletteDraggable.iconText(),
                    contentDescription =
                        stringResource(Res.string.palette_icon_for, paletteDraggable.iconText()),
                )
            }

        val tooltipText = stringResource(paletteDraggable.tooltipResource())

        Box(
            modifier =
                Modifier
                    .width(68.dp)
                    .height(58.dp)
                    .switchByHovered(
                        hovered =
                            Modifier
                                .clip(shape = RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)),
                        notHovered = Modifier.alpha(0.7f),
                    ),
        ) {
            Tooltip(tooltipText) {
                Icon(
                    modifier =
                        Modifier
                            .testTag("$PALETTE_TEST_TAG/${paletteDraggable.iconText()}")
                            .draggableFromPalette(
                                project = project,
                                paletteNodeCallbacks = paletteNodeCallbacks,
                                paletteDraggable = paletteDraggable,
                                zoomableContainerStateHolder = zoomableContainerStateHolder,
                            ),
                )
            }
        }
    }

    @Composable
    fun CategoryRow(
        categoryName: String,
        expanded: Boolean,
        onExpandClick: () -> Unit,
        modifier: Modifier = Modifier,
        tooltipTextResource: StringResource? = null,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                modifier
                    .padding(bottom = 4.dp)
                    .wrapContentSize()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onExpandClick()
                    },
        ) {
            Text(
                text = categoryName,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
            )
            tooltipTextResource?.let {
                val text = stringResource(tooltipTextResource)

                Spacer(Modifier.size(8.dp))
                Tooltip(text) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = text,
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            TreeExpanderInverse(
                expanded = expanded,
                onClick = onExpandClick,
            )
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
    ) {
        var commonExpanded by remember { mutableStateOf(true) }
        var layoutExpanded by remember { mutableStateOf(true) }
        var basicExpanded by remember { mutableStateOf(true) }
        var screenOnlyExpanded by remember { mutableStateOf(true) }
        var navigationItemExpanded by remember { mutableStateOf(true) }
        var authExpanded by remember { mutableStateOf(true) }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            modifier = modifier.fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Commonly used",
                    expanded = layoutExpanded,
                    onExpandClick = { commonExpanded = !commonExpanded },
                )
            }
            if (commonExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.Common in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Layout",
                    expanded = layoutExpanded,
                    onExpandClick = { layoutExpanded = !layoutExpanded },
                )
            }
            if (layoutExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.Layout in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Basic",
                    expanded = basicExpanded,
                    onExpandClick = { basicExpanded = !basicExpanded },
                )
            }
            if (basicExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.Basic in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Screen elements",
                    expanded = screenOnlyExpanded,
                    onExpandClick = { screenOnlyExpanded = !screenOnlyExpanded },
                    tooltipTextResource = TraitCategory.ScreenOnly.tooltipResource,
                )
            }
            if (screenOnlyExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.ScreenOnly in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Navigation drawer items",
                    expanded = navigationItemExpanded,
                    onExpandClick = { navigationItemExpanded = !navigationItemExpanded },
                )
            }
            if (navigationItemExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.NavigationItem in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CategoryRow(
                    categoryName = "Auth",
                    expanded = authExpanded,
                    onExpandClick = { authExpanded = !authExpanded },
                )
            }
            if (authExpanded) {
                ComposeTrait.entries
                    .filter { TraitCategory.Auth in it.paletteCategories() }
                    .forEach {
                        item {
                            PaletteItem(
                                paletteDraggable = it,
                            )
                        }
                    }
            }
        }
    }
}

@Composable
fun PaletteIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    iconText: String,
    contentDescription: String,
) {
    Box(
        modifier =
            modifier
                .width(80.dp)
                .height(64.dp),
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = contentDescription,
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier =
                Modifier
                    .width(42.dp)
                    .align(Alignment.TopCenter)
                    .padding(4.dp),
        )

        Text(
            text = iconText,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp),
        )
    }
}

@Composable
private fun ThemedPaletteTabPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        val mockProject = Project()
        val mockZoomableContainerStateHolder = ZoomableContainerStateHolder()
        PaletteTab(
            project = mockProject,
            paletteNodeCallbacks = emptyPaletteNodeCallbacks,
            zoomableContainerStateHolder = mockZoomableContainerStateHolder,
        )
    }
}

@Preview
@Composable
fun PaletteTabPreview_Light() {
    ThemedPaletteTabPreview(useDarkTheme = false)
}

@Preview
@Composable
fun PaletteTabPreview_Dark() {
    ThemedPaletteTabPreview(useDarkTheme = true)
}

@Composable
private fun ThemedPaletteIconPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        val columnTrait = ColumnTrait()
        PaletteIcon(
            imageVector = columnTrait.icon(),
            iconText = columnTrait.iconText(),
            contentDescription = "Preview icon",
        )
    }
}

@Preview
@Composable
fun PaletteIconPreview_Light() {
    ThemedPaletteIconPreview(useDarkTheme = false)
}

@Preview
@Composable
fun PaletteIconPreview_Dark() {
    ThemedPaletteIconPreview(useDarkTheme = true)
}
