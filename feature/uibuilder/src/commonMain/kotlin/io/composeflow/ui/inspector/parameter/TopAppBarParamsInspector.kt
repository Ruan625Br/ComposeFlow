package io.composeflow.ui.inspector.parameter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.add_action_icon
import io.composeflow.model.parameter.IconTrait
import io.composeflow.model.parameter.PlaceholderText
import io.composeflow.model.parameter.ScrollBehaviorWrapper
import io.composeflow.model.parameter.TopAppBarTrait
import io.composeflow.model.parameter.TopAppBarTypeWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNodeCallbacks
import io.composeflow.model.project.appscreen.screen.composenode.TopAppBarNode
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.model.property.StringProperty
import io.composeflow.placeholder_content_desc
import io.composeflow.scroll_behavior_supporting_text
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIcon
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.inspector.propertyeditor.IconPropertyEditor
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.propertyeditor.AssignableEditableTextPropertyEditor
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.propertyeditor.EditableTextProperty
import org.jetbrains.compose.resources.stringResource

@Composable
fun TopAppBarParamsInspector(
    node: ComposeNode,
    project: Project,
    composeNodeCallbacks: ComposeNodeCallbacks,
) {
    val topAppBarTrait = node.trait.value as TopAppBarTrait
    val topAppBarNode = node as TopAppBarNode
    val currentEditable = project.screenHolder.currentEditable()
    if (currentEditable !is Screen) return
    Column {
        BasicDropdownPropertyEditor(
            project = project,
            items = TopAppBarTypeWrapper.entries,
            selectedItem = topAppBarTrait.topAppBarType,
            label = "AppBar Type",
            onValueChanged = { _, selected ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    topAppBarTrait.copy(
                        topAppBarType = selected,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay(),
        )
        AssignableEditableTextPropertyEditor(
            project = project,
            node = node,
            initialProperty =
                if (currentEditable.showOnNavigation.value) {
                    StringProperty.StringIntrinsicValue(currentEditable.title.value)
                } else {
                    topAppBarTrait.title
                },
            onValidPropertyChanged = { property, _ ->
                if (property is StringProperty.StringIntrinsicValue) {
                    currentEditable.title.value = property.value
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        topAppBarTrait.copy(title = property),
                    )
                } else {
                    composeNodeCallbacks.onTraitUpdated(
                        node,
                        topAppBarTrait.copy(title = property),
                    )
                }
            },
            onInitializeProperty = {
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    topAppBarTrait.copy(title = StringProperty.StringIntrinsicValue("")),
                )
            },
            label = "Title",
            modifier = Modifier.hoverOverlay().fillMaxWidth(),
            // The variable is only visible if the TopAppBar is
            variableAssignable = !currentEditable.showOnNavigation.value,
        )

        if (topAppBarTrait.title !is IntrinsicProperty<*>) {
            val placeholderContentDesc = stringResource(Res.string.placeholder_content_desc)
            Tooltip(placeholderContentDesc) {
                BooleanPropertyEditor(
                    checked = topAppBarTrait.titlePlaceholderText is PlaceholderText.Used,
                    onCheckedChange = { placeHolderUsed ->
                        val usage =
                            if (placeHolderUsed) {
                                PlaceholderText.Used()
                            } else {
                                PlaceholderText.NoUsage
                            }
                        composeNodeCallbacks.onTraitUpdated(
                            node,
                            topAppBarTrait.copy(titlePlaceholderText = usage),
                        )
                    },
                    label = "Use placeholder text",
                    modifier =
                        Modifier
                            .hoverOverlay(),
                )
            }

            when (val usage = topAppBarTrait.titlePlaceholderText) {
                PlaceholderText.NoUsage -> {}
                is PlaceholderText.Used -> {
                    EditableTextProperty(
                        initialValue = usage.value.transformedValueExpression(project),
                        onValidValueChanged = {
                            composeNodeCallbacks.onTraitUpdated(
                                node,
                                topAppBarTrait.copy(
                                    titlePlaceholderText =
                                        PlaceholderText.Used(
                                            StringProperty.StringIntrinsicValue(
                                                it,
                                            ),
                                        ),
                                ),
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .hoverOverlay(),
                        label = "Placeholder text",
                        placeholder = "placeholder text",
                        singleLine = true,
                        valueSetFromVariable = false,
                    )
                }
            }
        }

        val navIcon = topAppBarNode.getTopAppBarNavigationIcon()
        IconPropertyEditor(
            label = "Navigation Icon",
            onIconDeleted = {
                val navIconTrait = navIcon?.trait?.value as? IconTrait
                navIconTrait?.let {
                    composeNodeCallbacks.onTraitUpdated(
                        navIcon,
                        navIconTrait.copy(imageVectorHolder = null),
                    )
                }
            },
            onIconSelected = {
                val navIconTrait = (navIcon?.trait?.value as? IconTrait) ?: IconTrait()
                val navIconComposeNode =
                    navIcon ?: ComposeNode(
                        label = mutableStateOf("Nav Icon"),
                        trait =
                            mutableStateOf(
                                IconTrait(imageVectorHolder = null),
                            ),
                    )
                composeNodeCallbacks.onTraitUpdated(
                    navIconComposeNode,
                    navIconTrait.copy(imageVectorHolder = it),
                )
            },
            currentIcon = (navIcon?.trait?.value as? IconTrait)?.imageVectorHolder?.imageVector,
            modifier =
                Modifier
                    .hoverOverlay(),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .padding(horizontal = 4.dp)
                    .height(42.dp),
        ) {
            Text(
                text = "Action Icons",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodySmall,
            )

            ComposeFlowIconButton(
                onClick = {
                    topAppBarNode.addTopAppBarActionIcon()
                    composeNodeCallbacks.onTraitUpdated(node, topAppBarTrait)
                },
                modifier =
                    Modifier
                        .padding(start = 28.dp)
                        .hoverOverlay()
                        .hoverIconClickable(),
            ) {
                val contentDesc = stringResource(Res.string.add_action_icon)
                Tooltip(contentDesc) {
                    ComposeFlowIcon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = contentDesc,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        val actionIcons = topAppBarNode.getTopAppBarActionIcons()
        actionIcons.forEachIndexed { i, icon ->
            val iconTrait = icon.trait.value as? IconTrait
            IconPropertyEditor(
                label = "Icon $i",
                onIconDeleted = {
                    topAppBarNode.removeTopAppBarActionIcon(i)
                    composeNodeCallbacks.onTraitUpdated(node, topAppBarTrait)
                },
                onIconSelected = { selected ->
                    iconTrait?.let {
                        icon.trait.value =
                            iconTrait.copy(imageVectorHolder = selected)
                        composeNodeCallbacks.onTraitUpdated(icon, icon.trait.value)
                    }
                },
                currentIcon = iconTrait?.imageVectorHolder?.imageVector,
                modifier =
                    Modifier
                        .hoverOverlay()
                        .padding(start = 24.dp),
            )
        }
        BasicDropdownPropertyEditor(
            project = project,
            items = ScrollBehaviorWrapper.entries,
            selectedItem = topAppBarTrait.scrollBehaviorWrapper,
            label = "Scroll Behavior",
            onValueChanged = { _, selected ->
                composeNodeCallbacks.onTraitUpdated(
                    node,
                    topAppBarTrait.copy(
                        scrollBehaviorWrapper = selected,
                    ),
                )
            },
            modifier = Modifier.hoverOverlay(),
            supportTooltipText = stringResource(Res.string.scroll_behavior_supporting_text),
        )
    }
}
