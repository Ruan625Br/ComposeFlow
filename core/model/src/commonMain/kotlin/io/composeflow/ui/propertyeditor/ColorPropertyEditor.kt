package io.composeflow.ui.propertyeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import io.composeflow.Res
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.delete_color
import io.composeflow.model.parameter.wrapper.ColorWrapper
import io.composeflow.model.parameter.wrapper.Material3ColorWrapper
import io.composeflow.select_color
import io.composeflow.serializer.asString
import io.composeflow.theme_colors
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.common.ComposeFlowTheme
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.labeledbox.LabeledBorderBox
import io.composeflow.ui.modifier.hoverIconClickable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ColorPropertyEditor(
    label: String,
    initialColor: ColorWrapper?,
    fallbackColor: Color = Color.Unspecified,
    onColorUpdated: (Color) -> Unit,
    onThemeColorSelected: (Material3ColorWrapper) -> Unit,
    modifier: Modifier = Modifier,
    onColorDeleted: (() -> Unit)? = null,
) {
    var dialogOpen by remember { mutableStateOf(false) }

    @Composable
    fun ColorBox(color: Color) {
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(8.dp),
                    ).border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    ),
        )
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .wrapContentHeight()
                .hoverIconClickable()
                .clickable {
                    dialogOpen = true
                },
    ) {
        val colorExpression =
            if (initialColor?.themeColor != null) {
                initialColor.themeColor.colorName
            } else {
                initialColor?.getColor()?.asString() ?: "Unspecified"
            }

        LabeledBorderBox(
            label = label.ifEmpty { "Color" },
            modifier = Modifier.weight(1f),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ColorBox(color = initialColor?.getColor() ?: fallbackColor)
                Text(
                    text = colorExpression,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 8.dp),
                )
                if (initialColor != null && onColorDeleted != null) {
                    Spacer(Modifier.weight(1f))
                    ComposeFlowIconButton(
                        onClick = {
                            onColorDeleted()
                        },
                    ) {
                        ComposeFlowIcon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(Res.string.delete_color),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    if (dialogOpen) {
        onAnyDialogIsShown()
        ColorPropertyDialog(
            initialColor = initialColor,
            fallbackColor = fallbackColor,
            onThemeColorSelected = onThemeColorSelected,
            onColorUpdated = onColorUpdated,
            onCloseClick = {
                dialogOpen = false
                onAllDialogsClosed()
            },
        )
    }
}

@Composable
private fun ColorPropertyDialog(
    initialColor: ColorWrapper?,
    fallbackColor: Color,
    onThemeColorSelected: (Material3ColorWrapper) -> Unit,
    onColorUpdated: (Color) -> Unit,
    onCloseClick: () -> Unit,
) {
    DialogWindow(
        onCloseRequest = {
            onCloseClick()
        },
        state =
            rememberDialogState(
                position = WindowPosition(Alignment.TopCenter),
                size = DpSize(800.dp, 1060.dp),
            ),
        title = "Select color",
        undecorated = true,
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
        content = {
            ColorPropertyDialogContent(
                initialColor = initialColor,
                fallbackColor = fallbackColor,
                onThemeColorSelected = onThemeColorSelected,
                onCloseClick = onCloseClick,
                onColorUpdated = onColorUpdated,
            )
        },
    )
}

@Composable
fun ColorPropertyDialogContent(
    initialColor: ColorWrapper?,
    fallbackColor: Color,
    onThemeColorSelected: (Material3ColorWrapper) -> Unit,
    onCloseClick: () -> Unit,
    onColorUpdated: (Color) -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxSize()
                .size(width = 760.dp, height = 680.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .padding(all = 16.dp),
        ) {
            Text(
                text = stringResource(Res.string.select_color),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            val resolvedColor = initialColor?.getColor() ?: fallbackColor
            var currentColor by remember {
                mutableStateOf(HsvColor.from(resolvedColor))
            }

            Row {
                ClassicColorPicker(
                    color = currentColor,
                    modifier =
                        Modifier
                            .width(540.dp)
                            .height(300.dp)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                    onColorChanged = { hsvColor: HsvColor ->
                        // Triggered when the color changes, do something with the newly picked color here!
                        currentColor = hsvColor
                    },
                )

                ColorPreviewInfo(currentColor.toColor().convert(ColorSpaces.Srgb))
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(148.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
            ) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    },
                ) {
                    Text(
                        text = stringResource(Res.string.theme_colors),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                items(
                    Material3ColorWrapper.entries.toTypedArray(),
                ) { themeColor ->

                    val borderModifier =
                        if (initialColor?.themeColor == themeColor) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(8.dp),
                            )
                        } else {
                            Modifier
                        }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            borderModifier
                                .size(
                                    width = 164.dp,
                                    height = 64.dp,
                                ).padding(8.dp)
                                .background(
                                    color = themeColor.getAppColor(),
                                    shape = RoundedCornerShape(8.dp),
                                ).border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                ).clickable {
                                    onThemeColorSelected(themeColor)
                                    onCloseClick()
                                }.hoverIconClickable(),
                    ) {
                        Text(
                            text = themeColor.colorName,
                            color = themeColor.getTextColor(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier =
                                Modifier
                                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.padding(top = 16.dp),
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
                        onColorUpdated(currentColor.toColor())
                        onCloseClick()
                    },
                    modifier = Modifier.padding(end = 16.dp),
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            }
        }
    }
}

@Composable
fun ColorPreviewInfo(
    color: Color,
    showAlpha: Boolean = true,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val red: Int = color.red.times(255).toInt()
        val green: Int = color.green.times(255).toInt()
        val blue: Int = color.blue.times(255).toInt()
        Text(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.secondary,
            text =
                if (showAlpha) {
                    "A: ${color.alpha} \n"
                } else {
                    ""
                } +
                    "R: $red \n" +
                    "G: $green \n" +
                    "B: $blue",
        )
        Box(
            modifier =
                Modifier
                    .padding(start = 16.dp)
                    .background(
                        color,
                        shape = RoundedCornerShape(8.dp),
                    ).size(72.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    ),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ThemedColorPropertyEditorPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        ColorPropertyEditor(
            label = "Background Color",
            initialColor = ColorWrapper(Material3ColorWrapper.Primary),
            onColorUpdated = {},
            onThemeColorSelected = {},
            onColorDeleted = {},
        )
    }
}

@Preview
@Composable
fun ColorPropertyEditorPreview_Light() {
    ThemedColorPropertyEditorPreview(useDarkTheme = false)
}

@Preview
@Composable
fun ColorPropertyEditorPreview_Dark() {
    ThemedColorPropertyEditorPreview(useDarkTheme = true)
}

@Composable
private fun ThemedColorPropertyDialogContentPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        ColorPropertyDialogContent(
            initialColor = ColorWrapper(Material3ColorWrapper.PrimaryContainer),
            fallbackColor = Color.Gray,
            onThemeColorSelected = {},
            onCloseClick = {},
            onColorUpdated = {},
        )
    }
}

@Preview
@Composable
fun ColorPropertyDialogContentPreview_Light() {
    ThemedColorPropertyDialogContentPreview(useDarkTheme = false)
}

@Preview
@Composable
fun ColorPropertyDialogContentPreview_Dark() {
    ThemedColorPropertyDialogContentPreview(useDarkTheme = true)
}

@Composable
private fun ThemedColorPreviewInfoPreview(useDarkTheme: Boolean) {
    ComposeFlowTheme(useDarkTheme = useDarkTheme) {
        ColorPreviewInfo(
            color = Color(0xFF1976D2),
        )
    }
}

@Preview
@Composable
fun ColorPreviewInfoPreview_Light() {
    ThemedColorPreviewInfoPreview(useDarkTheme = false)
}

@Preview
@Composable
fun ColorPreviewInfoPreview_Dark() {
    ThemedColorPreviewInfoPreview(useDarkTheme = true)
}
