package io.composeflow.ui.propertyeditor.variable

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.app_states
import io.composeflow.cancel
import io.composeflow.component_parameters
import io.composeflow.component_states
import io.composeflow.conditional_property
import io.composeflow.confirm
import io.composeflow.dynamic_items
import io.composeflow.enable_auth_to_proceed
import io.composeflow.function_parameters
import io.composeflow.index_at
import io.composeflow.initialize
import io.composeflow.is_not_assignable_state
import io.composeflow.model.apieditor.isList
import io.composeflow.model.datatype.DataField
import io.composeflow.model.datatype.DataFieldType
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.FieldType
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.CanvasEditable
import io.composeflow.model.project.ParameterWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.component.Component
import io.composeflow.model.project.findApiDefinitionOrNull
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.project.findDataTypeOrThrow
import io.composeflow.model.project.findLocalStateOrNull
import io.composeflow.model.property.ApiResultProperty
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.property.ComposableParameterProperty
import io.composeflow.model.property.ConditionalProperty
import io.composeflow.model.property.CustomEnumValuesProperty
import io.composeflow.model.property.FirestoreCollectionProperty
import io.composeflow.model.property.FunctionScopeParameterProperty
import io.composeflow.model.property.IntProperty
import io.composeflow.model.property.IntrinsicProperty
import io.composeflow.model.property.ValueFromDynamicItem
import io.composeflow.model.property.ValueFromGlobalProperty
import io.composeflow.model.property.ValueFromState
import io.composeflow.model.state.AppState
import io.composeflow.model.state.AuthenticatedUserState
import io.composeflow.model.state.StateHolderType
import io.composeflow.model.state.StateId
import io.composeflow.model.type.ComposeFlowType
import io.composeflow.model.type.emptyDocumentIdType
import io.composeflow.parameters
import io.composeflow.screen_parameters
import io.composeflow.screen_states
import io.composeflow.select_json_element
import io.composeflow.set_from_state_for
import io.composeflow.ui.Tooltip
import io.composeflow.ui.datafield.FieldRow
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.propertyeditor.ConditionalPropertyEditor
import io.composeflow.ui.state.AssignablePropertyLabel
import io.composeflow.ui.state.StateLabel
import io.composeflow.ui.utils.TreeExpander
import org.jetbrains.compose.resources.stringResource

@Composable
fun <P : AssignableProperty> SetFromStateDialog(
    project: Project,
    initialProperty: AssignableProperty?,
    node: ComposeNode,
    acceptableType: ComposeFlowType,
    defaultValue: P, // To pass the type parameter to the ConditionalProperty
    onCloseClick: () -> Unit,
    onValidPropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    onInitializeProperty: (() -> Unit)? = null,
    destinationStateId: StateId? = null,
    functionScopeProperties: List<FunctionScopeParameterProperty> = emptyList(),
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
    ) {
        var conditionalPropertyDialogOpen by remember(initialProperty) {
            mutableStateOf(
                initialProperty is ConditionalProperty,
            )
        }
        val overlayModifier =
            if (conditionalPropertyDialogOpen) {
                Modifier.alpha(0.2f)
            } else {
                Modifier
            }
        Surface(
            modifier = Modifier.size(width = 860.dp, height = 920.dp),
        ) {
            Column(
                modifier =
                    overlayModifier
                        .padding(all = 16.dp),
            ) {
                var selectedProperty by remember(initialProperty) { mutableStateOf(initialProperty) }
                var selectedLazyListSource by remember(initialProperty) {
                    mutableStateOf<LazyListChildParams?>(
                        node.lazyListChildParams.value,
                    )
                }

                var searchParams by remember {
                    mutableStateOf(
                        SearchStatesParams(
                            acceptableType = acceptableType,
                        ),
                    )
                }

                val isPropertyValid by remember(selectedProperty) {
                    derivedStateOf {
                        selectedProperty?.transformedValueType(project)?.let {
                            acceptableType.isAbleToAssign(it)
                        } ?: false
                    }
                }

                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    Column(
                        modifier = Modifier.weight(4f),
                    ) {
                        Text(
                            buildAnnotatedString {
                                val textStyle = MaterialTheme.typography.bodyMedium
                                withStyle(
                                    style =
                                        SpanStyle(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = textStyle.fontSize,
                                            fontWeight = textStyle.fontWeight,
                                            fontStyle = textStyle.fontStyle,
                                        ),
                                ) {
                                    append(stringResource(Res.string.set_from_state_for))
                                    withStyle(
                                        style =
                                            SpanStyle(
                                                color = MaterialTheme.colorScheme.tertiary,
                                                fontSize = textStyle.fontSize,
                                                fontWeight = textStyle.fontWeight,
                                                fontStyle = textStyle.fontStyle,
                                            ),
                                    ) {
                                        append(acceptableType.displayName(project))
                                    }
                                }
                            },
                            modifier = Modifier.padding(bottom = 16.dp),
                        )

                        SearchStateArea(
                            params = searchParams,
                            onSearchParamsUpdated = {
                                searchParams = it
                            },
                        )

                        val nodeToDynamicItems =
                            node
                                .findNodesUntilRoot()
                                .filter { it.dynamicItems.value != null }
                                .associateWith { it.dynamicItems.value!! }

                        val currentEditable = project.screenHolder.currentEditable()

                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            AuthenticatedUserStateViewer(
                                project = project,
                                onPropertySelected = {
                                    selectedProperty = it
                                },
                                properties =
                                    AuthenticatedUserState.entries().map {
                                        ValueFromGlobalProperty(it)
                                    },
                                selectedProperty = selectedProperty,
                                searchParams = searchParams,
                            )

                            if (functionScopeProperties.isNotEmpty()) {
                                FunctionParameterViewer(
                                    project = project,
                                    functionParameters = functionScopeProperties,
                                    onFunctionParameterSelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                )
                            }

                            AppStateViewer(
                                project = project,
                                properties =
                                    currentEditable
                                        .getStateResults(project)
                                        .filter { it.first == StateHolderType.Global }
                                        .map { it.second }
                                        .map { ValueFromState(it.id) },
                                onStateSelected = {
                                    selectedProperty = it
                                },
                                searchParams = searchParams,
                                selectedProperty = selectedProperty,
                                destinationStateId = destinationStateId,
                            )

                            if (currentEditable is Screen) {
                                ScreenStateViewer(
                                    project = project,
                                    screen = currentEditable,
                                    properties =
                                        currentEditable
                                            .getStateResults(project)
                                            .filter { it.first is StateHolderType.Screen }
                                            .map { it.second }
                                            .map { ValueFromState(it.id) },
                                    onStateSelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                    destinationStateId = destinationStateId,
                                )
                            } else if (currentEditable is Component) {
                                ComponentStateViewer(
                                    project = project,
                                    component = currentEditable,
                                    properties =
                                        currentEditable
                                            .getStateResults(project)
                                            .filter { it.first is StateHolderType.Component }
                                            .map { it.second }
                                            .map { ValueFromState(it.id) },
                                    onStateSelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                    destinationStateId = destinationStateId,
                                )
                            }
                            if (project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections
                                    .isNotEmpty()
                            ) {
                                FirestoreCollectionViewer(
                                    project = project,
                                    properties =
                                        project.firebaseAppInfoHolder.firebaseAppInfo.firestoreCollections.map {
                                            FirestoreCollectionProperty(collectionId = it.id)
                                        },
                                    onPropertySelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                )
                            }
                            if (project.customEnumHolder.enumList.isNotEmpty()) {
                                EnumViewer(
                                    project = project,
                                    properties =
                                        project.customEnumHolder.enumList.map {
                                            CustomEnumValuesProperty(customEnumId = it.customEnumId)
                                        },
                                    onPropertySelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                )
                            }
                            if (project.apiHolder.getValidApiDefinitions().isNotEmpty()) {
                                ApiDefinitionViewer(
                                    project = project,
                                    properties =
                                        project.apiHolder.apiDefinitions.map {
                                            ApiResultProperty(it.id)
                                        },
                                    onPropertySelected = {
                                        selectedProperty = it
                                    },
                                    searchParams = searchParams,
                                    selectedProperty = selectedProperty,
                                )
                            }
                            val editable = project.screenHolder.currentEditable()
                            ParameterViewer(
                                project = project,
                                parameters = editable.parameters,
                                canvasEditable = editable,
                                onParameterFieldSelected = { parameter, dataFieldType ->
                                    selectedProperty =
                                        ComposableParameterProperty(parameter.id, dataFieldType)
                                },
                                searchParams = searchParams,
                                selectedProperty = selectedProperty,
                            )
                            DynamicItemsViewer(
                                project = project,
                                nodeToDynamicItems = nodeToDynamicItems,
                                onAssignablePropertyChanged = { assignableProperty, lazyListSource ->
                                    selectedProperty = assignableProperty
                                    selectedLazyListSource = lazyListSource
                                },
                                searchParams = searchParams,
                                selectedProperty = selectedProperty,
                            )
                            val lazyLists =
                                node.findNodesUntilRoot().filter { it.trait.value.isLazyList() }
                            lazyLists.forEach { lazyList ->
                                LazyListChildViewer(
                                    project = project,
                                    property =
                                        IntProperty.ValueFromLazyListIndex(
                                            lazyListNodeId = lazyList.id,
                                        ),
                                    onAssignablePropertyChanged = { assignableProperty, _ ->
                                        selectedProperty = assignableProperty
                                    },
                                    selectedProperty = selectedProperty,
                                    searchParams = searchParams,
                                )
                            }
                            TextButton(onClick = {
                                conditionalPropertyDialogOpen = true
                            }) {
                                Text(stringResource(Res.string.conditional_property))
                            }

                            if (conditionalPropertyDialogOpen) {
                                val closeConditionalPropertyDialog = {
                                    conditionalPropertyDialogOpen = false
                                }
                                PositionCustomizablePopup(
                                    onDismissRequest = closeConditionalPropertyDialog,
                                ) {
                                    Surface(
                                        modifier =
                                            Modifier
                                                .width(616.dp)
                                                .height(720.dp),
                                    ) {
                                        Column(
                                            modifier =
                                                Modifier
                                                    .background(
                                                        MaterialTheme.colorScheme.surfaceContainer,
                                                    ).fillMaxSize(),
                                        ) {
                                            val conditionalProperty =
                                                if (initialProperty is ConditionalProperty) {
                                                    initialProperty
                                                } else {
                                                    ConditionalProperty(defaultValue = defaultValue)
                                                }
                                            ConditionalPropertyEditor(
                                                project = project,
                                                initialProperty = conditionalProperty,
                                                node = node,
                                                onCloseClick = closeConditionalPropertyDialog,
                                                onValidPropertyChanged = { property, _ ->
                                                    selectedProperty = property
                                                },
                                                defaultValue = defaultValue,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.weight(3f)) {
                        VerticalDivider(
                            modifier =
                                Modifier.padding(
                                    end = 16.dp,
                                    bottom = 16.dp,
                                ),
                        )
                        SelectedPropertyEditor(
                            project = project,
                            node = node,
                            acceptableType = acceptableType,
                            initialProperty = selectedProperty,
                            isPropertyValid = isPropertyValid,
                            onValidPropertyEdited = {
                                selectedProperty = it
                            },
                        )
                    }
                }

                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    val issues = node.generateTrackableIssues(project)
                    if (initialProperty !is IntrinsicProperty<*> ||
                        initialProperty.propertyTransformers.isNotEmpty() ||
                        issues.isNotEmpty()
                    ) {
                        OutlinedButton(
                            onClick = {
                                onInitializeProperty?.let {
                                    it()
                                } ?: {
                                    onValidPropertyChanged(
                                        acceptableType.defaultValue(),
                                        null,
                                    )
                                }
                                onCloseClick()
                            },
                        ) {
                            Text(
                                text = stringResource(Res.string.initialize),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
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
                            selectedProperty?.let {
                                onCloseClick()
                                onValidPropertyChanged(it, selectedLazyListSource)
                            }
                        },
                        enabled = isPropertyValid,
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppStateViewer(
    project: Project,
    properties: List<AssignableProperty>,
    destinationStateId: StateId?,
    onStateSelected: (AssignableProperty) -> Unit,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
    searchParams: SearchStatesParams,
) {
    StateViewer(
        project = project,
        sectionLabel = stringResource(Res.string.app_states),
        properties = properties,
        destinationStateId = destinationStateId,
        onStateSelected = onStateSelected,
        modifier = modifier,
        selectedProperty = selectedProperty,
        searchParams = searchParams,
    )
}

@Composable
private fun ScreenStateViewer(
    project: Project,
    screen: Screen,
    properties: List<AssignableProperty>,
    destinationStateId: StateId?,
    onStateSelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    StateViewer(
        project = project,
        sectionLabel = stringResource(Res.string.screen_states) + " [${screen.name}]",
        properties = properties,
        destinationStateId = destinationStateId,
        searchParams = searchParams,
        onStateSelected = onStateSelected,
        modifier = modifier,
        selectedProperty = selectedProperty,
    )
}

@Composable
private fun ComponentStateViewer(
    project: Project,
    component: Component,
    properties: List<AssignableProperty>,
    destinationStateId: StateId?,
    onStateSelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    StateViewer(
        project = project,
        sectionLabel = stringResource(Res.string.component_states) + " [${component.name}]",
        properties = properties,
        destinationStateId = destinationStateId,
        onStateSelected = onStateSelected,
        searchParams = searchParams,
        modifier = modifier,
        selectedProperty = selectedProperty,
    )
}

@Composable
private fun AuthenticatedUserStateViewer(
    project: Project,
    onPropertySelected: (AssignableProperty) -> Unit,
    modifier: Modifier = Modifier,
    properties: List<AssignableProperty>,
    selectedProperty: AssignableProperty? = null,
    searchParams: SearchStatesParams,
) {
    val authEnabled = project.firebaseAppInfoHolder.firebaseAppInfo.authenticationEnabled.value
    val filteredProperties =
        if (!authEnabled) {
            emptyList()
        } else {
            properties.filter {
                searchParams.matchCriteria(
                    project,
                    it,
                )
            }
        }
    val initiallyExpanded =
        if (selectedProperty is ValueFromGlobalProperty &&
            selectedProperty.readableState is AuthenticatedUserState
        ) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }

    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val authEnabledAndValidPropertiesExist = authEnabled && filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (authEnabledAndValidPropertiesExist) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        val enableAuthToProceed = stringResource(Res.string.enable_auth_to_proceed)
        if (authEnabled) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .then(switchedModifier),
            ) {
                TreeExpander(
                    expanded = expanded,
                    enabled = authEnabledAndValidPropertiesExist,
                    onClick = {
                        onExpandClick()
                    },
                )
                Text(
                    text = "Authenticated user (${filteredProperties.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            Tooltip(enableAuthToProceed) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .then(switchedModifier),
                ) {
                    TreeExpander(
                        expanded = expanded,
                        enabled = false,
                        onClick = {
                            onExpandClick()
                        },
                    )
                    Text(
                        text = "Authenticated user (${filteredProperties.size})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier =
                            Modifier
                                .padding(vertical = 8.dp)
                                .alpha(0.5f),
                    )
                }
            }
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { property ->
                    val selectedModifier =
                        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }
                    AssignablePropertyLabelRow(
                        project = project,
                        onLabelClicked = onPropertySelected,
                        property = property,
                        modifier = selectedModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun StateViewer(
    project: Project,
    sectionLabel: String,
    properties: List<AssignableProperty>,
    destinationStateId: StateId?,
    onStateSelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredProperties = properties.filter { searchParams.matchCriteria(project, it) }
    val initiallyExpanded =
        if (properties.any { selectedProperty != null && it.isIdentical(selectedProperty) }) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            Text(
                text = "$sectionLabel (${filteredProperties.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { property ->
                    val selectedModifier =
                        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }
                    val isDestinationEqualToSource =
                        destinationStateId != null &&
                            destinationStateId == (property as? ValueFromState)?.readFromStateId

                    val isValidState = !isDestinationEqualToSource
                    val onLabelClicked: ((AssignableProperty) -> Unit)? =
                        if (isValidState) {
                            {
                                onStateSelected(it)
                            }
                        } else {
                            null
                        }

                    if (isValidState) {
                        AssignablePropertyLabel(
                            project = project,
                            enabled = true,
                            onLabelClicked = onLabelClicked,
                            property = property,
                            modifier = selectedModifier,
                        )
                    } else {
                        val destinationState =
                            destinationStateId?.let { project.findLocalStateOrNull(it) }
                        val tooltipText =
                            stringResource(
                                Res.string.is_not_assignable_state,
                                property.displayText(project),
                                destinationState?.name ?: "",
                            )
                        Tooltip(tooltipText) {
                            AssignablePropertyLabel(
                                project = project,
                                enabled = false,
                                onLabelClicked = onLabelClicked,
                                property = property,
                                modifier = selectedModifier,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignablePropertyLabelRow(
    project: Project,
    onLabelClicked: ((AssignableProperty) -> Unit)?,
    property: AssignableProperty,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        AssignablePropertyLabel(
            project = project,
            property = property,
            onLabelClicked = onLabelClicked,
            enabled = enabled,
        )
    }
}

@Composable
private fun EnumViewer(
    project: Project,
    properties: List<AssignableProperty>,
    onPropertySelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredProperties = properties.filter { searchParams.matchCriteria(project, it) }
    val initiallyExpanded =
        if (properties.any { selectedProperty != null && selectedProperty.isIdentical(it) }) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            Text(
                text = "Enums (${filteredProperties.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { property ->
                    val selectedModifier =
                        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            modifier
                                .clickable {
                                    onPropertySelected(property)
                                }.then(selectedModifier),
                    ) {
                        Text(
                            property.displayText(project),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            "List<String>",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FirestoreCollectionViewer(
    project: Project,
    properties: List<AssignableProperty>,
    onPropertySelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredProperties = properties.filter { searchParams.matchCriteria(project, it) }
    val initiallyExpanded =
        if (properties.any { selectedProperty != null && it.isIdentical(selectedProperty) }) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            Text(
                text = "Firestore collection (${filteredProperties.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { property ->
                    val selectedModifier =
                        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            modifier
                                .clickable {
                                    onPropertySelected(property)
                                }.then(selectedModifier),
                    ) {
                        Text(
                            property.displayText(project),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            property.valueType(project).displayName(project),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ApiDefinitionViewer(
    project: Project,
    properties: List<ApiResultProperty>,
    onPropertySelected: (AssignableProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredProperties = properties.filter { searchParams.matchCriteria(project, it) }
    val initiallyExpanded =
        if (properties.any { selectedProperty != null && it.isIdentical(selectedProperty) }) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            Text(
                text = "API (${filteredProperties.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { property ->
                    val selectedModifier =
                        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                            modifier
                                .clickable {
                                    onPropertySelected(property)
                                }.then(selectedModifier),
                    ) {
                        Text(
                            property.displayText(project),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        val api = property.apiId?.let { project.findApiDefinitionOrNull(it) }
                        val type =
                            if (api?.exampleJsonResponse?.jsonElement?.isList() == true) {
                                "List<JsonElement>"
                            } else {
                                "JsonElement"
                            }
                        Text(
                            type,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterViewer(
    project: Project,
    parameters: List<ParameterWrapper<*>>,
    canvasEditable: CanvasEditable,
    onParameterFieldSelected: (ParameterWrapper<*>, DataFieldType) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredParameters = parameters.filter { searchParams.matchCriteria(project, it) }

    val initiallyExpanded =
        if (selectedProperty is ComposableParameterProperty &&
            parameters.any { it.id == selectedProperty.parameterId }
        ) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredParameters.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredParameters.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            val paramName =
                when (canvasEditable) {
                    is Screen -> stringResource(Res.string.screen_parameters)
                    is Component -> stringResource(Res.string.component_parameters)
                    else -> stringResource(Res.string.parameters)
                }
            Text(
                text = "$paramName (${filteredParameters.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredParameters.forEach { parameter ->
                    val selectedModifier =
                        if (
                            selectedProperty is ComposableParameterProperty &&
                            selectedProperty.parameterId == parameter.id
                        ) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }
                    if (parameter.parameterType is ComposeFlowType.CustomDataType && !parameter.parameterType.isList) {
                        val dataType =
                            project.findDataTypeOrNull((parameter.parameterType as ComposeFlowType.CustomDataType).dataTypeId)
                        dataType?.let {
                            DataTypeViewer(
                                project = project,
                                fieldName = parameter.variableName,
                                dataType = dataType,
                                selectedProperty = selectedProperty,
                                initiallyExpanded = {
                                    selectedProperty is ComposableParameterProperty &&
                                        selectedProperty.parameterId == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.FieldInDataType
                                },
                                dataFieldSelected = { dataField ->
                                    selectedProperty is ComposableParameterProperty &&
                                        selectedProperty.parameterId == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.FieldInDataType &&
                                        selectedProperty.dataFieldType.fieldId == dataField.id
                                },
                                primitiveFieldSelected = {
                                    selectedProperty is ComposableParameterProperty &&
                                        selectedProperty.parameterId == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.Primitive
                                },
                                onDataFieldSelected = { selectedDataField ->
                                    onParameterFieldSelected(parameter, selectedDataField)
                                },
                            )
                        }
                    } else {
                        FieldRow(
                            project = project,
                            fieldName = parameter.variableName,
                            type = parameter.parameterType,
                            modifier =
                                Modifier
                                    .padding(top = 4.dp)
                                    .then(selectedModifier),
                            onClick = {
                                onParameterFieldSelected(parameter, DataFieldType.Primitive)
                            },
                            displayNameListAware = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FunctionParameterViewer(
    project: Project,
    functionParameters: List<FunctionScopeParameterProperty>,
    onFunctionParameterSelected: (FunctionScopeParameterProperty) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredProperties = functionParameters.filter { searchParams.matchCriteria(project, it) }
    val initiallyExpanded =
        if (selectedProperty is FunctionScopeParameterProperty &&
            functionParameters.any {
                it.id == selectedProperty.id
            }
        ) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredProperties.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredProperties.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(switchedModifier),
        ) {
            TreeExpander(
                expanded = expanded,
                enabled = enabled,
                onClick = {
                    onExpandClick()
                },
            )
            Text(
                text = "${stringResource(Res.string.function_parameters)} (${functionParameters.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        if (expanded) {
            Column {
                filteredProperties.forEach { parameter ->
                    if (parameter.variableType is ComposeFlowType.CustomDataType) {
                        val dataType = project.findDataTypeOrNull(parameter.variableType.dataTypeId)
                        dataType?.let {
                            DataTypeViewer(
                                project = project,
                                dataType = dataType,
                                fieldName = dataType.className,
                                selectedProperty = selectedProperty,
                                initiallyExpanded = {
                                    selectedProperty is FunctionScopeParameterProperty &&
                                        selectedProperty.id == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.FieldInDataType
                                },
                                dataFieldSelected = { dataField ->
                                    selectedProperty is FunctionScopeParameterProperty &&
                                        selectedProperty.id == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.FieldInDataType &&
                                        dataField.id == selectedProperty.dataFieldType.fieldId
                                },
                                primitiveFieldSelected = {
                                    selectedProperty is FunctionScopeParameterProperty &&
                                        selectedProperty.id == parameter.id &&
                                        selectedProperty.dataFieldType is DataFieldType.Primitive
                                },
                                onDataFieldSelected = { selectedDataField ->
                                    onFunctionParameterSelected(
                                        parameter.copy(dataFieldType = selectedDataField),
                                    )
                                },
                            )
                        }
                    } else {
                        val selectedModifier =
                            if (
                                selectedProperty is FunctionScopeParameterProperty &&
                                selectedProperty.id == parameter.id
                            ) {
                                Modifier
                                    .padding(start = 24.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp),
                                    ).padding(8.dp)
                            } else {
                                Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                            }
                        FieldRow(
                            project = project,
                            fieldName = parameter.variableName,
                            type = parameter.variableType,
                            modifier =
                                Modifier
                                    .padding(top = 4.dp)
                                    .then(selectedModifier),
                            onClick = {
                                onFunctionParameterSelected(parameter)
                            },
                            displayNameListAware = true,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DataTypeViewer(
    project: Project,
    dataType: DataType,
    fieldName: String,
    initiallyExpanded: () -> Boolean,
    primitiveFieldSelected: () -> Boolean,
    dataFieldSelected: (DataField) -> Boolean,
    onDataFieldSelected: (DataFieldType) -> Unit,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
    level: Int = 1,
) {
    var dataTypeExpanded by remember { mutableStateOf(initiallyExpanded()) }

    val basePadding = (level * 32 - 8).dp
    Column(
        modifier =
            modifier
                .animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = basePadding, bottom = 4.dp),
        ) {
            TreeExpander(
                expanded = dataTypeExpanded,
                onClick = {
                    dataTypeExpanded = !dataTypeExpanded
                },
                modifier = Modifier.padding(top = 4.dp),
            )
            val selectedModifier =
                if (primitiveFieldSelected()) {
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                        ).padding(4.dp)
                        .padding(bottom = 4.dp)
                } else {
                    Modifier
                        .padding(4.dp)
                        .padding(bottom = 4.dp)
                }
            FieldRow(
                project = project,
                fieldName = fieldName,
                type = ComposeFlowType.CustomDataType(dataTypeId = dataType.id),
                modifier =
                    Modifier
                        .then(selectedModifier)
                        .padding(top = 4.dp),
                onClick = {
                    // TODO: Consider nested DataType
                    onDataFieldSelected(DataFieldType.Primitive)
                },
            )
        }
        if (dataTypeExpanded) {
            Column(modifier = Modifier.padding(start = basePadding)) {
                dataType.fields.forEach { field ->
                    val selectedModifier =
                        if (dataFieldSelected(field)) {
                            Modifier
                                .padding(start = 24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                ).padding(8.dp)
                        } else {
                            Modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                        }
                    if (field.fieldType is FieldType.CustomDataType) {
                        val childDataType = project.findDataTypeOrThrow(field.fieldType.dataTypeId)
                        DataTypeViewer(
                            project = project,
                            dataType = childDataType,
                            fieldName = childDataType.className,
                            onDataFieldSelected = onDataFieldSelected,
                            selectedProperty = selectedProperty,
                            level = level + 1,
                            initiallyExpanded = initiallyExpanded,
                            dataFieldSelected = dataFieldSelected,
                            primitiveFieldSelected = primitiveFieldSelected,
                        )
                    } else {
                        FieldRow(
                            project = project,
                            fieldName = field.variableName,
                            type = field.fieldType.type(),
                            modifier = selectedModifier,
                            onClick = {
                                onDataFieldSelected(
                                    DataFieldType.FieldInDataType(
                                        dataTypeId = dataType.id,
                                        fieldId = field.id,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicItemsViewer(
    project: Project,
    nodeToDynamicItems: Map<ComposeNode, AssignableProperty>,
    onAssignablePropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
    selectedProperty: AssignableProperty? = null,
) {
    val filteredNodeToDynamicItems =
        nodeToDynamicItems.filter {
            searchParams.matchCriteria(
                project,
                displayText = it.value.displayText(project),
                type = it.value.valueType(project).copyWith(newIsList = false),
            )
        }

    val initiallyExpanded =
        if (nodeToDynamicItems.any {
                selectedProperty is ValueFromDynamicItem &&
                    it.key.id == selectedProperty.composeNodeId
            }
        ) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                filteredNodeToDynamicItems.isNotEmpty()
            } else {
                false
            }
        }
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val enabled = filteredNodeToDynamicItems.entries.isNotEmpty()
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .then(switchedModifier),
    ) {
        TreeExpander(
            expanded = expanded,
            enabled = enabled,
            onClick = {
                onExpandClick()
            },
        )
        Text(
            text = "${stringResource(Res.string.dynamic_items)} (${filteredNodeToDynamicItems.entries.size})",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
    Column(
        modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        if (expanded) {
            filteredNodeToDynamicItems.entries.forEach {
                DynamicItemsEntry(
                    project = project,
                    dynamicItemEntry = it,
                    onAssignablePropertyChanged = onAssignablePropertyChanged,
                    selectedProperty = selectedProperty,
                )
            }
        }
    }
}

@Composable
private fun DynamicItemsEntry(
    project: Project,
    dynamicItemEntry: Map.Entry<ComposeNode, AssignableProperty>,
    onAssignablePropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    selectedProperty: AssignableProperty?,
) {
    Column {
        when (dynamicItemEntry.value) {
            is ApiResultProperty -> {
                JsonElementContent(project, dynamicItemEntry, onAssignablePropertyChanged)
            }

            else -> {
                DynamicItemsListContent(
                    project = project,
                    dynamicItemEntry = dynamicItemEntry,
                    onAssignablePropertyChanged = onAssignablePropertyChanged,
                    selectedProperty = selectedProperty,
                )
            }
        }
    }
}

@Composable
private fun JsonElementContent(
    project: Project,
    dynamicItemEntry: Map.Entry<ComposeNode, AssignableProperty>,
    onAssignablePropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val onExpandClick = { expanded = !expanded }
    val property = dynamicItemEntry.value
    check(property is ApiResultProperty)
    var openJsonElementSelectionDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 32.dp)
                .clickable {
                    onExpandClick()
                },
    ) {
        Row {
            Text(
                text = property.displayText(project),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = 8.dp),
            )

            Text(
                text =
                    property
                        .transformedValueType(project)
                        .copyWith(newIsList = false)
                        .displayName(project),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        TextButton(onClick = {
            openJsonElementSelectionDialog = true
        }) {
            Text(stringResource(Res.string.select_json_element))
        }
    }

    if (openJsonElementSelectionDialog && property.apiId != null) {
        project.apiHolder.findApiDefinitionOrNull(property.apiId)?.let { apiDefinition ->
            JsonElementSelectionDialog(
                apiDefinition = apiDefinition,
                onCloseDialog = {
                    openJsonElementSelectionDialog = false
                },
                onElementClicked = {
                    onAssignablePropertyChanged(
                        it,
                        LazyListChildParams.DynamicItemsSource(dynamicItemEntry.key.id),
                    )
                },
            )
        }
    }
}

@Composable
private fun DynamicItemsListContent(
    project: Project,
    dynamicItemEntry: Map.Entry<ComposeNode, AssignableProperty>,
    onAssignablePropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    selectedProperty: AssignableProperty?,
    modifier: Modifier = Modifier,
) {
    val (composeNode, dynamicItem) = dynamicItemEntry
    val sourceType = dynamicItem.transformedValueType(project)
    if (sourceType.isPrimitive()) {
        val selectedModifier =
            if (
                selectedProperty is ValueFromDynamicItem &&
                selectedProperty.composeNodeId == composeNode.id
            ) {
                modifier
                    .padding(start = 24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(8.dp)
            } else {
                modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
            }
        FieldRow(
            project = project,
            fieldName = "item: ${dynamicItem.transformedValueExpression(project)}",
            type = sourceType,
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .then(selectedModifier),
            onClick = {
                onAssignablePropertyChanged(
                    ValueFromDynamicItem(
                        composeNodeId = composeNode.id,
                        fieldType = DataFieldType.Primitive,
                    ),
                    LazyListChildParams.DynamicItemsSource(composeNode.id),
                )
            },
        )
    } else {
        check(sourceType is ComposeFlowType.CustomDataType)
        val dataType = project.findDataTypeOrThrow(sourceType.dataTypeId)

        var expanded by remember { mutableStateOf(true) }
        val onExpandClick = { expanded = !expanded }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp),
        ) {
            TreeExpander(
                expanded = expanded,
                onClick = {
                    onExpandClick()
                },
            )
            val selectedDataTypeId =
                (selectedProperty as? ValueFromDynamicItem)?.let {
                    when (it.fieldType) {
                        is DataFieldType.DataType -> it.fieldType.dataTypeId
                        is DataFieldType.FieldInDataType -> null
                        DataFieldType.Primitive -> null
                        is DataFieldType.DocumentId -> null
                    }
                }
            val selectedModifier =
                if (dataType.id == selectedDataTypeId) {
                    Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                        ).padding(8.dp)
                } else {
                    Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                }
            Row(
                modifier =
                    Modifier
                        .then(selectedModifier)
                        .clickable {
                            onAssignablePropertyChanged(
                                ValueFromDynamicItem(
                                    composeNodeId = composeNode.id,
                                    fieldType = DataFieldType.DataType(dataTypeId = dataType.id),
                                ),
                                LazyListChildParams.DynamicItemsSource(composeNode.id),
                            )
                        },
            ) {
                Text(
                    text = dynamicItem.displayText(project),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = dataType.className,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        if (expanded) {
            val initialDataFieldId =
                (selectedProperty as? ValueFromDynamicItem)?.let {
                    when (it.fieldType) {
                        is DataFieldType.FieldInDataType -> it.fieldType.fieldId
                        is DataFieldType.DataType -> null
                        DataFieldType.Primitive -> null
                        is DataFieldType.DocumentId -> null
                    }
                }

            @Composable
            fun SelectedModifier(selected: Boolean): Modifier =
                if (selected) {
                    Modifier
                        .padding(start = 56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                        ).padding(8.dp)
                } else {
                    Modifier.padding(start = 64.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                }

            if (dynamicItemEntry.value is FirestoreCollectionProperty) {
                val collectionId =
                    (dynamicItemEntry.value as? FirestoreCollectionProperty)?.collectionId
                        ?: emptyDocumentIdType.firestoreCollectionId
                FieldRow(
                    project = project,
                    fieldName = "item: ",
                    type = ComposeFlowType.DocumentIdType(collectionId),
                    onClick = {
                        onAssignablePropertyChanged(
                            ValueFromDynamicItem(
                                composeNodeId = composeNode.id,
                                fieldType = DataFieldType.DocumentId(collectionId),
                            ),
                            LazyListChildParams.DynamicItemsSource(composeNode.id),
                        )
                    },
                    modifier =
                        Modifier
                            .padding(top = 4.dp)
                            .then(
                                SelectedModifier(
                                    selected =
                                        selectedProperty is ValueFromDynamicItem &&
                                            selectedProperty.fieldType is DataFieldType.DocumentId &&
                                            selectedProperty.fieldType.firestoreCollectionId == collectionId,
                                ),
                            ),
                )
            }

            dataType.fields.forEach { dataField ->
                FieldRow(
                    project = project,
                    fieldName = "item: ${dataField.variableName}",
                    type = dataField.fieldType.type(),
                    modifier =
                        Modifier
                            .padding(top = 4.dp)
                            .then(
                                SelectedModifier(
                                    selected = dataField.id == initialDataFieldId,
                                ),
                            ),
                    onClick = {
                        onAssignablePropertyChanged(
                            ValueFromDynamicItem(
                                composeNodeId = composeNode.id,
                                fieldType =
                                    DataFieldType.FieldInDataType(
                                        dataTypeId = dataType.id,
                                        fieldId = dataField.id,
                                    ),
                            ),
                            LazyListChildParams.DynamicItemsSource(composeNode.id),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun LazyListChildViewer(
    project: Project,
    property: AssignableProperty,
    onAssignablePropertyChanged: (AssignableProperty, lazyListSource: LazyListChildParams?) -> Unit,
    selectedProperty: AssignableProperty?,
    searchParams: SearchStatesParams,
    modifier: Modifier = Modifier,
) {
    val initiallyExpanded =
        if (selectedProperty != null && selectedProperty.isIdentical(property)) {
            true
        } else {
            if (searchParams.isFilterEnabled) {
                searchParams.matchCriteria(project, property)
            } else {
                false
            }
        }

    val enabled =
        if (searchParams.isFilterEnabled) searchParams.matchCriteria(project, property) else true
    var expanded by remember(searchParams.isFilterEnabled) { mutableStateOf(initiallyExpanded) }
    val onExpandClick = { expanded = !expanded }
    val switchedModifier =
        if (enabled) {
            Modifier.clickable {
                onExpandClick()
            }
        } else {
            Modifier.alpha(0.6f)
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .fillMaxWidth()
                .clickable {
                    onExpandClick()
                }.then(switchedModifier),
    ) {
        TreeExpander(
            expanded = expanded,
            onClick = {
                onExpandClick()
            },
            enabled = enabled,
        )
        Text(
            text = "${property.displayText(project)} properties",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }

    Column(
        modifier = Modifier.animateContentSize(keyframes { durationMillis = 100 }),
    ) {
        if (expanded) {
            val selectedModifier =
                if (selectedProperty != null && selectedProperty.isIdentical(property)) {
                    modifier
                        .padding(start = 24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                        ).padding(8.dp)
                } else {
                    modifier.padding(start = 32.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                }
            StateLabel(
                project = project,
                state = AppState.IntAppState(name = stringResource(Res.string.index_at)),
                onLabelClicked = {
                    onAssignablePropertyChanged(
                        property,
                        null,
                    )
                },
                modifier = selectedModifier,
            )
        }
    }
}
