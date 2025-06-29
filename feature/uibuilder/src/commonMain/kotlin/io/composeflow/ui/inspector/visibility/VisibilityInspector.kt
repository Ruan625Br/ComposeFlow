package io.composeflow.ui.inspector.visibility

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DesktopMac
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TabletMac
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.form_factor_compact
import io.composeflow.form_factor_expanded
import io.composeflow.form_factor_medium
import io.composeflow.model.enumwrapper.EnumWrapper
import io.composeflow.model.enumwrapper.NodeVisibility
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.property.BooleanProperty
import io.composeflow.model.property.EnumProperty
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconToggleButton
import io.composeflow.ui.inspector.ParamInspectorHeaderRow
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableBooleanPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.utils.TreeExpanderInverse
import io.composeflow.visibility
import io.composeflow.visible_in
import org.jetbrains.compose.resources.stringResource

@Composable
fun VisibilityInspector(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    val initiallyExpanded = !node.visibilityParams.value.alwaysVisible()
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        var expanded by remember(node.id) { mutableStateOf(initiallyExpanded) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expanded = !expanded }
                    .hoverIconClickable(),
        ) {
            Text(
                text = stringResource(Res.string.visibility),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Spacer(modifier = Modifier.weight(1f))

            TreeExpanderInverse(
                expanded = expanded,
                onClick = { expanded = !expanded },
            )
        }

        if (expanded) {
            VisibilityInspectorContent(
                project = project,
                node = node,
                composeNodeCallbacks = composeNodeCallbacks,
            )
        }
    }
}

@Composable
private fun VisibilityInspectorContent(
    project: Project,
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val nodeVisibility = node.visibilityParams.value.nodeVisibility
        Row {
            BasicDropdownPropertyEditor(
                project = project,
                items = NodeVisibility.entries,
                label = "Conditional visibility",
                selectedIndex = (nodeVisibility as? EnumProperty)?.value?.enumValue()?.ordinal ?: 0,
                onValueChanged = { _, item ->
                    composeNodeCallbacks.onVisibilityParamsUpdated(
                        node,
                        node.visibilityParams.value.copy(
                            nodeVisibility = EnumProperty(item as EnumWrapper),
                        ),
                    )
                },
                modifier =
                    Modifier
                        .hoverOverlay()
                        .weight(3f)
                        .padding(end = 8.dp),
            )

            BooleanPropertyEditor(
                checked = node.visibilityParams.value.visibleInUiBuilder,
                label = "Visible in UI builder",
                onCheckedChange = {
                    composeNodeCallbacks.onVisibilityParamsUpdated(
                        node,
                        node.visibilityParams.value.copy(
                            visibleInUiBuilder = it,
                        ),
                    )
                },
                modifier = Modifier.weight(4f).hoverOverlay(),
            )
        }

        when (node.visibilityParams.value.nodeVisibilityValue()) {
            NodeVisibility.AlwaysVisible -> {
            }

            NodeVisibility.Conditional -> {
                AssignableBooleanPropertyEditor(
                    project = project,
                    node = node,
                    label = "Visible if",
                    initialProperty = node.visibilityParams.value.visibilityCondition,
                    onValidPropertyChanged = { property, _ ->
                        composeNodeCallbacks.onVisibilityParamsUpdated(
                            node,
                            node.visibilityParams.value.copy(
                                visibilityCondition = property,
                            ),
                        )
                    },
                    onInitializeProperty = {
                        composeNodeCallbacks.onVisibilityParamsUpdated(
                            node,
                            node.visibilityParams.value.copy(
                                visibilityCondition = BooleanProperty.Empty,
                            ),
                        )
                    },
                    modifier =
                        Modifier
                            .hoverOverlay()
                            .fillMaxWidth(),
                )
            }
        }

        FormFactorVisibilityInspector(
            node = node,
            composeNodeCallbacks = composeNodeCallbacks,
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .padding(4.dp),
        )
    }
}

@Composable
private fun FormFactorVisibilityInspector(
    node: ComposeNode,
    composeNodeCallbacks: ComposeNodeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.hoverOverlay()) {
        ParamInspectorHeaderRow(label = stringResource(Res.string.visible_in))

        val formFactorVisibility = node.visibilityParams.value.formFactorVisibility

        @Composable
        fun FormFactorVisibilityIconToggleButton(
            checked: Boolean,
            iconImageVector: ImageVector,
            contentDescription: String,
            onCheckedChange: (Boolean) -> Unit,
        ) {
            Tooltip(contentDescription) {
                ComposeFlowIconToggleButton(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    modifier =
                        Modifier
                            .padding(2.dp)
                            .hoverIconClickable()
                            .then(
                                if (checked) {
                                    Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.3f,
                                            ),
                                        )
                                } else {
                                    Modifier.alpha(0.5f)
                                },
                            ),
                ) {
                    ComposeFlowIcon(
                        imageVector = iconImageVector,
                        contentDescription = contentDescription,
                    )
                }
            }
        }

        Row {
            FormFactorVisibilityIconToggleButton(
                checked = formFactorVisibility.visibleInCompact,
                iconImageVector = Icons.Outlined.Smartphone,
                contentDescription = stringResource(Res.string.form_factor_compact),
                onCheckedChange = {
                    composeNodeCallbacks.onVisibilityParamsUpdated(
                        node,
                        node.visibilityParams.value.copy(
                            formFactorVisibility = formFactorVisibility.copy(visibleInCompact = it),
                        ),
                    )
                },
            )
            FormFactorVisibilityIconToggleButton(
                checked = formFactorVisibility.visibleInMedium,
                iconImageVector = Icons.Outlined.TabletMac,
                contentDescription = stringResource(Res.string.form_factor_medium),
                onCheckedChange = {
                    composeNodeCallbacks.onVisibilityParamsUpdated(
                        node,
                        node.visibilityParams.value.copy(
                            formFactorVisibility = formFactorVisibility.copy(visibleInMedium = it),
                        ),
                    )
                },
            )
            FormFactorVisibilityIconToggleButton(
                checked = formFactorVisibility.visibleInExpanded,
                iconImageVector = Icons.Outlined.DesktopMac,
                contentDescription = stringResource(Res.string.form_factor_expanded),
                onCheckedChange = {
                    composeNodeCallbacks.onVisibilityParamsUpdated(
                        node,
                        node.visibilityParams.value.copy(
                            formFactorVisibility = formFactorVisibility.copy(visibleInExpanded = it),
                        ),
                    )
                },
            )
        }
    }
}
