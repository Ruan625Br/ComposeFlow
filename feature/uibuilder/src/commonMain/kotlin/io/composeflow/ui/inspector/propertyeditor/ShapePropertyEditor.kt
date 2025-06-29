package io.composeflow.ui.inspector.propertyeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BorderInner
import androidx.compose.material.icons.outlined.BorderOuter
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.CropDin
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.RoundedCorner
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.composeflow.editor.validator.CornerValidator
import io.composeflow.model.parameter.wrapper.CornerValueHolder
import io.composeflow.model.parameter.wrapper.ShapeCornerSpec
import io.composeflow.model.parameter.wrapper.ShapeWrapper
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconToggleButton
import io.composeflow.ui.inspector.ParamInspectorHeaderRow
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.BasicEditableTextProperty

@Composable
fun ShapePropertyEditor(
    initialShape: ShapeWrapper?,
    onShapeUpdated: (ShapeWrapper) -> Unit,
) {
    Column(modifier = Modifier.hoverOverlay().padding(vertical = 4.dp)) {
        ParamInspectorHeaderRow(
            label = "Shape",
        )

        @Composable
        fun runIconToggleButton(
            shape: ShapeWrapper,
            selected: Boolean,
            imageVector: ImageVector,
            contentDesc: String,
            iconModifier: Modifier = Modifier,
        ) = run {
            Tooltip(contentDesc) {
                ComposeFlowIconToggleButton(
                    checked = true,
                    onCheckedChange = {
                        onShapeUpdated(shape)
                    },
                    modifier =
                        Modifier.then(
                            if (selected) {
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
                    ComposeFlowIcon(
                        imageVector = imageVector,
                        contentDescription = contentDesc,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = iconModifier,
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.heightIn(min = TextFieldDefaults.MinHeight),
        ) {
            var initialCornerValues by remember {
                mutableStateOf(
                    when (initialShape) {
                        is ShapeWrapper.CutCorner -> {
                            CornerValueHolderImpl(
                                initialShape.topStart,
                                initialShape.topEnd,
                                initialShape.bottomEnd,
                                initialShape.bottomStart,
                            )
                        }

                        is ShapeWrapper.RoundedCorner -> {
                            CornerValueHolderImpl(
                                initialShape.topStart,
                                initialShape.topEnd,
                                initialShape.bottomEnd,
                                initialShape.bottomStart,
                            )
                        }

                        else -> {
                            null
                        }
                    },
                )
            }
            var cornerValues by remember {
                mutableStateOf(
                    initialCornerValues ?: CornerValueHolderImpl(
                        0.dp,
                        0.dp,
                        0.dp,
                        0.dp,
                    ),
                )
            }
            runIconToggleButton(
                shape = ShapeWrapper.Rectangle,
                selected = initialShape is ShapeWrapper.Rectangle,
                imageVector = Icons.Outlined.Rectangle,
                contentDesc = "Rectangle",
            )
            runIconToggleButton(
                shape = ShapeWrapper.Circle,
                selected = initialShape is ShapeWrapper.Circle,
                imageVector = Icons.Outlined.Circle,
                contentDesc = "Circle",
            )
            runIconToggleButton(
                shape =
                    ShapeWrapper.RoundedCorner(
                        cornerValues.topStart,
                        cornerValues.topEnd,
                        cornerValues.bottomEnd,
                        cornerValues.bottomStart,
                    ),
                selected = initialShape is ShapeWrapper.RoundedCorner,
                imageVector = Icons.Outlined.RoundedCorner,
                contentDesc = "Rounded corner",
            )
            runIconToggleButton(
                shape =
                    ShapeWrapper.CutCorner(
                        cornerValues.topStart,
                        cornerValues.topEnd,
                        cornerValues.bottomEnd,
                        cornerValues.bottomStart,
                    ),
                selected = initialShape is ShapeWrapper.CutCorner,
                imageVector = Icons.Outlined.CropDin,
                contentDesc = "Cut corner",
                iconModifier = Modifier.rotate(45f),
            )

            when (initialShape) {
                is ShapeWrapper.Circle -> {}
                is ShapeWrapper.Rectangle -> {}
                is ShapeWrapper.CutCorner -> {
                    CornerSizeInspector(
                        initialShape = initialShape,
                        cornerValues = cornerValues,
                        onValidCornersChanged = { topStart, topEnd, bottomEnd, bottomStart ->
                            val newShape =
                                ShapeWrapper.CutCorner(topStart, topEnd, bottomEnd, bottomStart)
                            cornerValues =
                                CornerValueHolderImpl(topStart, topEnd, bottomEnd, bottomStart)

                            onShapeUpdated(newShape)
                        },
                    )
                }

                is ShapeWrapper.RoundedCorner -> {
                    CornerSizeInspector(
                        initialShape = initialShape,
                        cornerValues = cornerValues,
                        onValidCornersChanged = { topStart, topEnd, bottomEnd, bottomStart ->
                            val newShape =
                                ShapeWrapper.RoundedCorner(topStart, topEnd, bottomEnd, bottomStart)
                            cornerValues =
                                CornerValueHolderImpl(topStart, topEnd, bottomEnd, bottomStart)
                            onShapeUpdated(newShape)
                        },
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun CornerSizeInspector(
    initialShape: ShapeWrapper?,
    cornerValues: CornerValueHolder,
    onValidCornersChanged: (
        topStart: Dp,
        topEnd: Dp,
        bottomEnd: Dp,
        bottomStart: Dp,
    ) -> Unit,
) {
    var selectedPaddingSpec by remember {
        mutableStateOf(
            initialShape?.let {
                if (initialShape is CornerValueHolder) {
                    initialShape.spec()
                } else {
                    ShapeCornerSpec.All
                }
            } ?: ShapeCornerSpec.All,
        )
    }

    @Composable
    fun runSpecToggleButton(
        shapeSpec: ShapeCornerSpec,
        imageVector: ImageVector? = null,
        contentDesc: String,
    ) = run {
        val thisItemSelected = selectedPaddingSpec == shapeSpec
        Tooltip(contentDesc) {
            ComposeFlowIconToggleButton(
                checked = thisItemSelected,
                onCheckedChange = {
                    selectedPaddingSpec = shapeSpec
                },
                modifier =
                    Modifier.then(
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
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
        Row {
            runSpecToggleButton(
                shapeSpec = ShapeCornerSpec.All,
                imageVector = Icons.Outlined.BorderOuter,
                contentDesc = "All",
            )
            runSpecToggleButton(
                shapeSpec = ShapeCornerSpec.Individual,
                imageVector = Icons.Outlined.BorderInner,
                contentDesc = "Individual",
            )
        }
        when (selectedPaddingSpec) {
            ShapeCornerSpec.All -> {
                BasicEditableTextProperty(
                    initialValue =
                        cornerValues.topStart.value
                            .toInt()
                            .toString(),
                    placeholder = "Corner size",
                    label = "All",
                    validateInput = CornerValidator()::validate,
                    onValidValueChanged = {
                        val dp = Dp(it.toFloat())
                        onValidCornersChanged(dp, dp, dp, dp)
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }

            ShapeCornerSpec.Individual -> {
                Column {
                    Row {
                        BasicEditableTextProperty(
                            initialValue =
                                cornerValues.topStart.value
                                    .toInt()
                                    .toString(),
                            label = "Top start",
                            validateInput = CornerValidator()::validate,
                            onValidValueChanged = {
                                onValidCornersChanged(
                                    Dp(it.toFloat()),
                                    cornerValues.topEnd,
                                    cornerValues.bottomEnd,
                                    cornerValues.bottomStart,
                                )
                            },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                        )
                        BasicEditableTextProperty(
                            initialValue =
                                cornerValues.topEnd.value
                                    .toInt()
                                    .toString(),
                            label = "Top end",
                            validateInput = CornerValidator()::validate,
                            onValidValueChanged = {
                                onValidCornersChanged(
                                    cornerValues.topStart,
                                    Dp(it.toFloat()),
                                    cornerValues.bottomEnd,
                                    cornerValues.bottomStart,
                                )
                            },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                        )
                    }
                    Row {
                        BasicEditableTextProperty(
                            initialValue =
                                cornerValues.bottomStart.value
                                    .toInt()
                                    .toString(),
                            label = "Bottom start",
                            validateInput = CornerValidator()::validate,
                            onValidValueChanged = {
                                onValidCornersChanged(
                                    cornerValues.topStart,
                                    cornerValues.topEnd,
                                    cornerValues.bottomEnd,
                                    Dp(it.toFloat()),
                                )
                            },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                        )
                        BasicEditableTextProperty(
                            initialValue =
                                cornerValues.bottomEnd.value
                                    .toInt()
                                    .toString(),
                            label = "Bottom end",
                            validateInput = CornerValidator()::validate,
                            onValidValueChanged = {
                                onValidCornersChanged(
                                    cornerValues.topStart,
                                    cornerValues.topEnd,
                                    Dp(it.toFloat()),
                                    cornerValues.bottomStart,
                                )
                            },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

data class CornerValueHolderImpl(
    override val topStart: Dp,
    override val topEnd: Dp,
    override val bottomEnd: Dp,
    override val bottomStart: Dp,
) : CornerValueHolder
