package io.composeflow.ui.inspector.propertyeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerticalAlignBottom
import androidx.compose.material.icons.outlined.VerticalAlignCenter
import androidx.compose.material.icons.outlined.VerticalAlignTop
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import io.composeflow.model.parameter.wrapper.ArrangementVerticalWrapper
import io.composeflow.platform.AsyncImage
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconToggleButton
import io.composeflow.ui.inspector.ParamInspectorHeaderRow
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.Res
import io.composeflow.vertical_arrangement
import io.composeflow.vertical_bottom_arrangement
import io.composeflow.vertical_center_arrangement
import io.composeflow.vertical_space_around_arrangement
import io.composeflow.vertical_space_between_arrangement
import io.composeflow.vertical_space_evenly_arrangement
import io.composeflow.vertical_top_arrangement
import org.jetbrains.compose.resources.stringResource

@Composable
fun ArrangementVerticalPropertyEditor(
    initialValue: ArrangementVerticalWrapper?,
    onArrangementSelected: (ArrangementVerticalWrapper) -> Unit,
) {
    Column(modifier = Modifier.hoverOverlay().padding(vertical = 4.dp)) {
        ParamInspectorHeaderRow(
            label = stringResource(Res.string.vertical_arrangement),
            modifier = Modifier.padding(vertical = 4.dp),
        )

        @Composable
        fun runIconToggleButton(
            verticalArrangement: ArrangementVerticalWrapper,
            imageVector: ImageVector? = null,
            resourcePath: String? = null,
            contentDesc: String,
        ) = run {
            val thisItemSelected = initialValue == verticalArrangement
            Tooltip(contentDesc) {
                ComposeFlowIconToggleButton(
                    checked = thisItemSelected,
                    onCheckedChange = {
                        onArrangementSelected(verticalArrangement)
                    },
                    modifier = Modifier.then(
                        if (thisItemSelected) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = 0.3f,
                                    ),
                                )
                        } else {
                            Modifier
                        },
                    ),
                ) {
                    imageVector?.let {
                        ComposeFlowIcon(
                            imageVector = imageVector,
                            contentDescription = contentDesc,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    // Load the image from the local resource if the icon isn't available as
                    // part of the material icons
                    val density = LocalDensity.current
                    resourcePath?.let {
                        AsyncImage(
                            load = {
                                useResource(resourcePath) {
                                    loadSvgPainter(
                                        it,
                                        density,
                                    )
                                }
                            },
                            painterFor = { it },
                            contentDescription = contentDesc,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
        FlowRow {
            runIconToggleButton(
                ArrangementVerticalWrapper.Top,
                imageVector = Icons.Outlined.VerticalAlignTop,
                contentDesc = stringResource(Res.string.vertical_top_arrangement),
            )
            runIconToggleButton(
                ArrangementVerticalWrapper.Center,
                imageVector = Icons.Outlined.VerticalAlignCenter,
                contentDesc = stringResource(Res.string.vertical_center_arrangement),
            )
            runIconToggleButton(
                ArrangementVerticalWrapper.Bottom,
                imageVector = Icons.Outlined.VerticalAlignBottom,
                contentDesc = stringResource(Res.string.vertical_bottom_arrangement),
            )

            runIconToggleButton(
                ArrangementVerticalWrapper.SpaceBetween,
                resourcePath = "vertical_align_space_between.svg",
                contentDesc = stringResource(Res.string.vertical_space_between_arrangement),
            )
            runIconToggleButton(
                ArrangementVerticalWrapper.SpaceEvenly,
                resourcePath = "vertical_align_space_even.svg",
                contentDesc = stringResource(Res.string.vertical_space_evenly_arrangement),
            )
            runIconToggleButton(
                ArrangementVerticalWrapper.SpaceAround,
                resourcePath = "vertical_align_space_around.svg",
                contentDesc = stringResource(Res.string.vertical_space_around_arrangement),
            )
        }
    }
}
