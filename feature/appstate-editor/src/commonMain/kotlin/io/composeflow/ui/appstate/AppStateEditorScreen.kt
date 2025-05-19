package io.composeflow.ui.appstate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.datatype.DataType
import io.composeflow.model.datatype.DataTypeDefaultValue
import io.composeflow.model.datatype.DefaultValuesParseResult
import io.composeflow.model.datatype.ParseDefaultValuesJsonTextField
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.model.project.Project
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.state.AppState
import io.composeflow.model.state.AppStateWithDataTypeId
import io.composeflow.model.state.copy
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.modifier.backgroundContainerNeutral
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.text.EditableText
import io.composeflow.ui.textfield.SmallOutlinedTextField
import io.composeflow.editor.validator.FloatValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.editor.validator.KotlinVariableNameValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.Res
import io.composeflow.add_app_state
import io.composeflow.app_state
import io.composeflow.app_state_tooltip
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.data_type
import io.composeflow.default_value
import io.composeflow.delete_app_state
import io.composeflow.field_name
import io.composeflow.is_list
import io.composeflow.no_data_type_defined
import io.composeflow.set_default_values_from_json
import io.composeflow.set_from_json
import io.composeflow.state_name
import io.composeflow.type
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppStateEditor(
    projectId: String,
    modifier: Modifier = Modifier,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel = viewModel(modelClass = AppStateEditorViewModel::class) {
        AppStateEditorViewModel(firebaseIdToken = firebaseIdToken, projectId = projectId)
    }
    Surface(modifier = modifier.fillMaxSize()) {
        Row {
            val projectUiState by viewModel.projectUiState.collectAsState()
            AppStateHeaderContainer()
            AppStateDetail(
                projectUiState = projectUiState,
                onAppStateAdded = viewModel::onAppStateAdded,
                onAppStateDeleted = viewModel::onAppStateDeleted,
                onAppStateUpdated = viewModel::onAppStateUpdated,
                onDataTypeListDefaultValueUpdated = viewModel::onDataTypeListDefaultValueUpdated,
            )
        }
    }
}

@Composable
private fun AppStateHeaderContainer() {
    Column(
        Modifier
            .padding(16.dp)
            .width(240.dp),
    ) {
        AppStateHeader()
    }
}

@Composable
private fun AppStateHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(Res.string.app_state),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val contentDesc = stringResource(Res.string.app_state_tooltip)
            Tooltip(contentDesc) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = contentDesc,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun AppStateDetail(
    projectUiState: LoadedProjectUiState,
    onAppStateAdded: (AppState<*>) -> Unit,
    onAppStateDeleted: (AppState<*>) -> Unit,
    onAppStateUpdated: (AppState<*>) -> Unit,
    onDataTypeListDefaultValueUpdated: (AppState<*>, List<DataTypeDefaultValue>) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .backgroundContainerNeutral()
            .padding(16.dp),
    ) {
        Spacer(Modifier.weight(1f))
        AppStateDetailContent(
            projectUiState = projectUiState,
            onAppStateAdded = onAppStateAdded,
            onAppStateDeleted = onAppStateDeleted,
            onAppStateUpdated = onAppStateUpdated,
            onDataTypeListDefaultValueUpdated = onDataTypeListDefaultValueUpdated,
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AppStateDetailContent(
    projectUiState: LoadedProjectUiState,
    onAppStateAdded: (AppState<*>) -> Unit,
    onAppStateDeleted: (AppState<*>) -> Unit,
    onAppStateUpdated: (AppState<*>) -> Unit,
    onDataTypeListDefaultValueUpdated: (AppState<*>, List<DataTypeDefaultValue>) -> Unit,
) {
    var addAppStateDialogOpen by remember { mutableStateOf(false) }
    var appStateToBeDeleted by remember { mutableStateOf<AppState<*>?>(null) }
    var appStateToBeUpdated by remember { mutableStateOf<AppState<*>?>(null) }

    Column(
        modifier = Modifier
            .width(960.dp)
            .fillMaxHeight()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row {
                Text(
                    stringResource(Res.string.app_state),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp).padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            AppStateDetailContentHeader()

            when (projectUiState) {
                is LoadedProjectUiState.Error -> {}
                LoadedProjectUiState.Loading -> {
                    CircularProgressIndicator()
                }

                LoadedProjectUiState.NotFound -> {}
                is LoadedProjectUiState.Success -> {
                    val project = projectUiState.project
                    val appStates =
                        project.globalStateHolder.getStates(project).map { it as AppState<*> }
                    LazyColumn(modifier = Modifier.heightIn(max = 800.dp)) {
                        items(appStates) { appState ->
                            AppStateDetailRow(
                                project = project,
                                appState = appState,
                                onAppStateUpdated = onAppStateUpdated,
                                onDeleteAppStateDialogOpen = {
                                    appStateToBeDeleted = it
                                },
                                onDataTypeListDefaultValueUpdated = onDataTypeListDefaultValueUpdated
                            )
                        }
                    }
                    TextButton(
                        onClick = {
                            addAppStateDialogOpen = true
                        },
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("+ ${stringResource(Res.string.add_app_state)}")
                    }

                    val onAnyDialogIsOpen = LocalOnAnyDialogIsShown.current
                    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
                    if (addAppStateDialogOpen) {
                        onAnyDialogIsOpen()
                        val dialogClosed = {
                            addAppStateDialogOpen = false
                            appStateToBeUpdated = null
                            onAllDialogsClosed()
                        }

                        AddAppStateDialog(
                            project = project,
                            initialValue = appStateToBeUpdated,
                            onAppStateAdded = {
                                onAppStateAdded(it)
                                dialogClosed()
                            },
                            onAppStateUpdated = { appState: AppState<*> ->
                                dialogClosed()
                                onAppStateUpdated(appState)
                            },
                            onDialogClosed = dialogClosed,
                        )
                    }

                    appStateToBeDeleted?.let { toBeDeleted ->
                        onAnyDialogIsOpen()
                        val closeDialog = {
                            appStateToBeDeleted = null
                            onAllDialogsClosed()
                        }
                        SimpleConfirmationDialog(
                            text = stringResource(Res.string.delete_app_state) + "?",
                            onCloseClick = {
                                closeDialog()
                            },
                            onConfirmClick = {
                                closeDialog()
                                onAppStateDeleted(toBeDeleted)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppStateDetailContentHeader() {
    Column {
        Row {
            Text(
                stringResource(Res.string.state_name),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(200.dp),
            )

            Text(
                stringResource(Res.string.data_type),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(200.dp),
            )

            Text(
                stringResource(Res.string.is_list),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(120.dp),
            )

            Text(
                stringResource(Res.string.default_value),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(280.dp),
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun AppStateDetailRow(
    project: Project,
    appState: AppState<*>,
    onAppStateUpdated: (AppState<*>) -> Unit,
    onDeleteAppStateDialogOpen: (AppState<*>) -> Unit,
    onDataTypeListDefaultValueUpdated: (AppState<*>, List<DataTypeDefaultValue>) -> Unit,
) {
    var dataTypeForDefaultValues: DataType? by remember { mutableStateOf(null) }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditableText(
                initialText = appState.name,
                onValueChange = {
                    onAppStateUpdated(appState.copy(name = it))
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.width(200.dp),
            )

            Text(
                text = appState.valueType(project).displayName(project),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.width(200.dp),
            )

            Text(
                text = appState.isList.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(120.dp),
            )


            Column(modifier = Modifier.width(280.dp)) {
                Text(
                    appState.defaultValue?.toString() ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (appState is AppState.CustomDataTypeListAppState) {
                    Text(
                        text = "${appState.defaultValue.size} items",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    TextButton(onClick = {
                        dataTypeForDefaultValues = project.findDataTypeOrNull(appState.dataTypeId)
                    }) {
                        Text(text = "+ " + stringResource(Res.string.set_from_json))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    onDeleteAppStateDialogOpen(appState)
                },
            ) {
                val contentDesc = stringResource(Res.string.delete_app_state)
                Tooltip(contentDesc) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = contentDesc,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp),
        )
    }
    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogClosed = LocalOnAllDialogsClosed.current
    dataTypeForDefaultValues?.let { dataType ->
        onAnyDialogIsShown()
        val dialogClosed = {
            onAllDialogClosed()
            dataTypeForDefaultValues = null
        }
        DataTypeListDefaultValuesFromJsonDialog(
            dataType = dataType,
            onCloseClick = dialogClosed,
            onDataTypeDefaultValueAdded = {
                onDataTypeListDefaultValueUpdated(appState, it)
            }
        )
    }
}

private const val emptyJSON = "Empty JSON"

@Composable
private fun DataTypeListDefaultValuesFromJsonDialog(
    dataType: DataType,
    onCloseClick: () -> Unit,
    onDataTypeDefaultValueAdded: (List<DataTypeDefaultValue>) -> Unit,
) {
    var parseResult: DefaultValuesParseResult? by remember { mutableStateOf(null) }
    val parsedFields: List<DataTypeDefaultValue>? by remember(parseResult) {
        mutableStateOf(
            when (val result = parseResult) {
                is DefaultValuesParseResult.Failure -> null
                is DefaultValuesParseResult.Success -> result.defaultValues
                is DefaultValuesParseResult.SuccessWithWarning -> result.defaultValues
                DefaultValuesParseResult.EmptyInput -> null
                null -> null
            }
        )
    }

    val errorMessage: String? by remember(parseResult) {
        mutableStateOf(
            when (val result = parseResult) {
                DefaultValuesParseResult.EmptyInput -> emptyJSON
                is DefaultValuesParseResult.Failure -> result.message
                is DefaultValuesParseResult.Success -> null
                is DefaultValuesParseResult.SuccessWithWarning -> result.warningMessage
                null -> emptyJSON
            }
        )
    }

    val isFormValid by remember {
        derivedStateOf {
            parseResult?.isSuccess() == true
        }
    }

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
        val (first, second, third) = remember { FocusRequester.createRefs() }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 680.dp, height = 620.dp)
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.set_default_values_from_json),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ParseDefaultValuesJsonTextField(
                    dataType = dataType,
                    onJsonParsed = {
                        parseResult = it
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .focusRequester(first)
                        .weight(1f)
                )
                if (errorMessage != null) {
                    Column(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                } else {
                    Spacer(Modifier.size(24.dp))
                }

                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .focusRequester(second),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            parsedFields?.let {
                                onDataTypeDefaultValueAdded(it)
                            }
                            onCloseClick()
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAppStateDialog(
    project: Project,
    initialValue: AppState<*>? = null,
    onAppStateAdded: (AppState<*>) -> Unit,
    onAppStateUpdated: (AppState<*>) -> Unit,
    onDialogClosed: () -> Unit,
) {
    var editedAppState by remember {
        mutableStateOf(
            initialValue ?: AppState.StringAppState(name = ""),
        )
    }

    val initialDataTypeSelected = when (initialValue) {
        is AppState.CustomDataTypeAppState -> {
            if (project.dataTypeHolder.dataTypes.any { it.id == initialValue.dataTypeId }) {
                project.dataTypeHolder.dataTypes.indexOfFirst { it.id == initialValue.dataTypeId }
            } else {
                0
            }
        }

        else -> 0
    }
    var dataTypeSelectedIndex by remember { mutableStateOf(initialDataTypeSelected) }

    val onAppStateConfirmed = {
        if (initialValue == null) {
            onAppStateAdded(editedAppState)
        } else {
            onAppStateUpdated(editedAppState)
        }
        onDialogClosed()
    }

    PositionCustomizablePopup(
        onDismissRequest = {
            onDialogClosed()
        },
        onKeyEvent = {
            if (it.key == Key.Escape) {
                onDialogClosed()
                true
            } else {
                false
            }
        },
    ) {
        val (first, second, third, fourth) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 320.dp, height = 380.dp)
                    .padding(16.dp),
            ) {
                val fieldWidth = 300.dp
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.add_app_state),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }

                var appStateNameValidateResult by remember {
                    mutableStateOf(KotlinVariableNameValidator().validate(editedAppState.name))
                }
                SmallOutlinedTextField(
                    value = editedAppState.name,
                    onValueChange = {
                        editedAppState = editedAppState.copy(name = it)
                        appStateNameValidateResult = KotlinVariableNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            text = stringResource(Res.string.field_name),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.6f),
                        )
                    },
                    singleLine = true,
                    isError = appStateNameValidateResult is ValidateResult.Failure,
                    shape = RoundedCornerShape(8.dp),
                    supportingText = (appStateNameValidateResult as? ValidateResult.Failure)?.let {
                        {
                            Text(it.message)
                        }
                    },
                    modifier = Modifier.focusRequester(first)
                        .moveFocusOnTab()
                        .width(width = fieldWidth)
                        .onKeyEvent {
                            if (it.key == Key.Enter && editedAppState.name.isNotEmpty()) {
                                onAppStateConfirmed()
                                true
                            } else {
                                false
                            }
                        },
                )
                var dropDownSelectedIndex by remember {
                    mutableStateOf(
                        initialValue?.let { AppState.indexOf(it) } ?: 0,
                    )
                }

                BasicDropdownPropertyEditor(
                    items = AppState.entries()
                        .map {
                            it.valueType(project).displayName(
                                project = project,
                                listAware = false,
                            )
                        },
                    onValueChanged = { index, _ ->
                        val appState = AppState.fromOrdinal(index)
                        val dataTypes = project.dataTypeHolder.dataTypes
                        val selectedDataTypeId = if (dataTypes.isNotEmpty()) {
                            dataTypes[dataTypeSelectedIndex].id
                        } else {
                            null
                        }
                        editedAppState = appState.copy(
                            id = editedAppState.id,
                            name = editedAppState.name,
                            isList = editedAppState.isList,
                            argDataTypeId = selectedDataTypeId,
                        )
                        dropDownSelectedIndex = index
                    },
                    selectedIndex = dropDownSelectedIndex,
                    label = stringResource(Res.string.data_type),
                    modifier = Modifier.padding(top = 8.dp)
                        .focusRequester(second)
                        .moveFocusOnTab(),
                )

                if (editedAppState is AppStateWithDataTypeId) {
                    val dataTypes = project.dataTypeHolder.dataTypes
                    if (dataTypes.isEmpty()) {
                        Tooltip(stringResource(Res.string.no_data_type_defined)) {
                            BasicDropdownPropertyEditor(
                                items = dataTypes.map { it.className },
                                onValueChanged = { _, _ ->
                                },
                                selectedIndex = dataTypeSelectedIndex,
                                label = stringResource(Res.string.type),
                            )
                        }
                    } else {
                        BasicDropdownPropertyEditor(
                            items = dataTypes.map { it.className },
                            onValueChanged = { index, _ ->
                                when (val appState = editedAppState) {
                                    is AppState.CustomDataTypeAppState -> {
                                        editedAppState = appState.copy(
                                            dataTypeId = dataTypes[index].id,
                                        )
                                    }

                                    else -> {}
                                }
                                dataTypeSelectedIndex = index
                            },
                            selectedIndex = dataTypeSelectedIndex,
                            label = stringResource(Res.string.type),
                        )
                    }
                }

                BooleanPropertyEditor(
                    checked = editedAppState.isList,
                    onCheckedChange = {
                        editedAppState = editedAppState.copy(
                            id = editedAppState.id,
                            name = editedAppState.name,
                            isList = it,
                        )
                    },
                    label = stringResource(Res.string.is_list),
                )

                var defaultValueError by remember { mutableStateOf<String?>(null) }
                val isFormValid by remember {
                    derivedStateOf {
                        defaultValueError == null &&
                                appStateNameValidateResult is ValidateResult.Success &&
                                if (editedAppState is AppStateWithDataTypeId) {
                                    project.dataTypeHolder.dataTypes.isNotEmpty()
                                } else {
                                    true
                                }
                    }
                }
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    when (editedAppState) {
                        is AppState.BooleanAppState -> {
                            BooleanPropertyEditor(
                                checked = editedAppState.defaultValue as Boolean,
                                onCheckedChange = {
                                    editedAppState =
                                        (editedAppState as AppState.BooleanAppState).copy(
                                            id = editedAppState.id,
                                            name = editedAppState.name,
                                            defaultValue = it,
                                        )
                                },
                                label = stringResource(Res.string.default_value),
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab(),
                            )
                        }

                        is AppState.BooleanListAppState -> {}

                        is AppState.IntAppState -> {
                            var value by remember { mutableStateOf(editedAppState.defaultValue.toString()) }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        IntValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            editedAppState =
                                                (editedAppState as AppState.IntAppState).copy(
                                                    id = editedAppState.id,
                                                    name = editedAppState.name,
                                                    defaultValue = it.toInt(),
                                                )
                                            defaultValueError = null
                                        }

                                        is ValidateResult.Failure -> {
                                            defaultValueError = validateResult.message
                                        }
                                    }
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                },
                                supportingText = defaultValueError?.let {
                                    {
                                        Text(it)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueError != null,
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onAppStateConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is AppState.IntListAppState -> {}

                        is AppState.FloatAppState -> {
                            var value by remember { mutableStateOf(editedAppState.defaultValue.toString()) }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        FloatValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            editedAppState =
                                                (editedAppState as AppState.FloatAppState).copy(
                                                    id = editedAppState.id,
                                                    name = editedAppState.name,
                                                    defaultValue = it.toFloat(),
                                                )
                                            defaultValueError = null
                                        }

                                        is ValidateResult.Failure -> {
                                            defaultValueError = validateResult.message
                                        }
                                    }
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                },
                                supportingText = defaultValueError?.let {
                                    {
                                        Text(it)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueError != null,
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onAppStateConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is AppState.FloatListAppState -> {}

                        is AppState.StringAppState -> {
                            SmallOutlinedTextField(
                                value = editedAppState.defaultValue.toString(),
                                onValueChange = {
                                    editedAppState =
                                        (editedAppState as AppState.StringAppState).copy(
                                            id = editedAppState.id,
                                            name = editedAppState.name,
                                            defaultValue = it,
                                        )
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onAppStateConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is AppState.StringListAppState -> {}

                        is AppState.InstantAppState -> {}
                        is AppState.InstantListAppState -> {}
                        is AppState.CustomDataTypeAppState -> {}
                        is AppState.CustomDataTypeListAppState -> {}
                    }
                }

                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onDialogClosed()
                        },
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .focusRequester(third),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onAppStateConfirmed()
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .focusRequester(fourth),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}
