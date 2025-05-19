package io.composeflow.ui.datatype

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.model.datatype.DataField
import io.composeflow.model.datatype.DataTypeParseResult
import io.composeflow.model.datatype.FieldType
import io.composeflow.model.datatype.ParseDataTypeJsonTextField
import io.composeflow.model.project.LoadedProjectUiState
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.Tooltip
import io.composeflow.ui.icon.ComposeFlowIconButton
import io.composeflow.ui.modifier.backgroundContainerNeutral
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.modifier.hoverOverlay
import io.composeflow.ui.modifier.moveFocusOnTab
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.propertyeditor.BooleanPropertyEditor
import io.composeflow.ui.text.EditableText
import io.composeflow.ui.textfield.SmallOutlinedTextField
import io.composeflow.editor.validator.FloatValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.editor.validator.KotlinClassNameValidator
import io.composeflow.editor.validator.KotlinIdentifierValidator.MUST_NOT_BE_EMPTY
import io.composeflow.editor.validator.KotlinVariableNameValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.Res
import io.composeflow.add_data_type
import io.composeflow.add_data_type_from_json
import io.composeflow.add_field
import io.composeflow.cancel
import io.composeflow.confirm
import io.composeflow.data_type
import io.composeflow.data_type_name
import io.composeflow.data_type_tooltip
import io.composeflow.default_value
import io.composeflow.delete
import io.composeflow.delete_data_field
import io.composeflow.delete_data_type
import io.composeflow.delete_enum
import io.composeflow.enum
import io.composeflow.enum_tooltip
import io.composeflow.field_name
import io.composeflow.field_type
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

enum class DataTypeTab {
    DataType,
    Enum,
}

@Composable
fun DataTypeEditor(
    projectId: String,
    modifier: Modifier = Modifier,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel = viewModel(modelClass = DataTypeEditorViewModel::class) {
        DataTypeEditorViewModel(firebaseIdToken = firebaseIdToken, projectId = projectId)
    }
    val projectUiState by viewModel.projectUiState.collectAsState()

    var deleteDataTypeDialogOpen by remember { mutableStateOf(false) }
    var deleteEnumDialogOpen by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(DataTypeTab.DataType) }

    Surface(modifier = modifier.fillMaxSize()) {
        Row {
            LeftPane(
                projectUiState = projectUiState,
                focusedDataTypeIndex = viewModel.focusedDataTypeIndex,
                focusedEnumIndex = viewModel.focusedEnumIndex,
                onDataTypeAdded = viewModel::onDataTypeAdded,
                onDataTypeWithFieldsAdded = viewModel::onDataTypeWithFieldsAdded,
                onDataTypeFocusedIndexUpdated = viewModel::onFocusedDataTypeIndexUpdated,
                onEnumFocusedIndexUpdated = viewModel::onFocusedEnumIndexUpdated,
                onEnumAdded = viewModel::onEnumAdded,
                onSelectedTabChanged = {
                    selectedTab = it
                },
                selectedTab = selectedTab,
            )
            when (selectedTab) {
                DataTypeTab.DataType -> {
                    DataTypeDetail(
                        projectUiState,
                        focusedDataTypeIndex = viewModel.focusedDataTypeIndex,
                        onDataFieldAdded = viewModel::onDataFieldAdded,
                        onDataFieldNameUpdated = viewModel::onDataFieldNameUpdated,
                        onDeleteDataTypeIconClicked = {
                            deleteDataTypeDialogOpen = true
                        },
                        onDeleteDataFieldOfIndex = viewModel::onDeleteDataField,
                    )
                }

                DataTypeTab.Enum -> {
                    EnumDetail(
                        projectUiState,
                        focusedEnumIndex = viewModel.focusedEnumIndex,
                        onEnumValueAdded = viewModel::onEnumValueAdded,
                        onEnumValueUpdated = viewModel::onEnumValueUpdated,
                        onDeleteEnumIconClicked = {
                            deleteEnumDialogOpen = true
                        },
                        onDeleteEnumValueOfIndex = viewModel::onDeleteEnumValue,
                        onSwapEnumValueIndexes = viewModel::onSwapEnumValueIndexes,
                    )
                }
            }
        }
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogClosed = LocalOnAllDialogsClosed.current
    if (deleteDataTypeDialogOpen) {
        onAnyDialogIsShown()
        val closeDeleteDataTypeDialog = {
            deleteDataTypeDialogOpen = false
            onAllDialogClosed()
        }
        DeleteDataTypeDialog(
            onCloseClick = {
                closeDeleteDataTypeDialog()
            },
            onDeleteDataType = {
                viewModel.onDataTypeDeleted()
                closeDeleteDataTypeDialog()
            },
        )
    }

    if (deleteEnumDialogOpen) {
        onAnyDialogIsShown()
        val closeDeleteEnumDialog = {
            deleteEnumDialogOpen = false
            onAllDialogClosed()
        }

        SimpleConfirmationDialog(
            text = stringResource(Res.string.delete_enum) + "?",
            onCloseClick = closeDeleteEnumDialog,
            onConfirmClick = {
                viewModel.onEnumDeleted()
                closeDeleteEnumDialog()
            }
        )
    }
}

@Composable
private fun LeftPane(
    projectUiState: LoadedProjectUiState,
    focusedDataTypeIndex: Int?,
    focusedEnumIndex: Int?,
    onDataTypeAdded: (String) -> Unit,
    onDataTypeWithFieldsAdded: (String, List<DataField>) -> Unit,
    onDataTypeFocusedIndexUpdated: (Int) -> Unit,
    onEnumFocusedIndexUpdated: (Int) -> Unit,
    onEnumAdded: (String) -> Unit,
    onSelectedTabChanged: (DataTypeTab) -> Unit,
    selectedTab: DataTypeTab,
) {
    Column(
        modifier = Modifier.width(320.dp)
    ) {
        val dataType = stringResource(Res.string.data_type)
        val enum = stringResource(Res.string.enum)

        when (selectedTab) {
            DataTypeTab.DataType -> {
                DataTypeListHeader(
                    onDataTypeAdded = onDataTypeAdded,
                    onDataTypeWithFieldsAdded = onDataTypeWithFieldsAdded,
                    modifier = Modifier.padding(16.dp)
                        .padding(bottom = 16.dp)
                )
            }

            DataTypeTab.Enum -> {
                EnumListHeader(
                    onEnumAdded = onEnumAdded,
                    modifier = Modifier.padding(16.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab.ordinal,
        ) {
            val dataTypeTooltip = stringResource(Res.string.data_type_tooltip)
            Tooltip(dataTypeTooltip) {
                Tab(
                    selected = selectedTab == DataTypeTab.DataType,
                    onClick = {
                        onSelectedTabChanged(DataTypeTab.DataType)
                    },
                    text = {
                        Text(dataType)
                    }
                )
            }

            val enumTooltip = stringResource(Res.string.enum_tooltip)
            Tooltip(enumTooltip) {
                Tab(
                    selected = selectedTab == DataTypeTab.Enum,
                    onClick = {
                        onSelectedTabChanged(DataTypeTab.Enum)
                    },
                    text = {
                        Text(enum)
                    }
                )
            }
        }

        when (selectedTab) {
            DataTypeTab.DataType -> {
                DataTypeList(
                    projectUiState = projectUiState,
                    dataTypeFocusedIndex = focusedDataTypeIndex,
                    onFocusedIndexUpdated = onDataTypeFocusedIndexUpdated,
                )
            }

            DataTypeTab.Enum -> {
                EnumList(
                    projectUiState = projectUiState,
                    enumFocusedIndex = focusedEnumIndex,
                    onFocusedEnumIndexUpdated = onEnumFocusedIndexUpdated,
                )
            }
        }
    }
}

@Composable
private fun DataTypeList(
    projectUiState: LoadedProjectUiState,
    dataTypeFocusedIndex: Int?,
    onFocusedIndexUpdated: (Int) -> Unit,
) {
    Column(
        Modifier
            .padding(16.dp)
    ) {
        when (projectUiState) {
            is LoadedProjectUiState.Error -> {}
            LoadedProjectUiState.Loading -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    CircularProgressIndicator()
                }
            }

            LoadedProjectUiState.NotFound -> {}
            is LoadedProjectUiState.Success -> {
                val dataTypes = projectUiState.project.dataTypeHolder.dataTypes
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    itemsIndexed(dataTypes) { i, dataType ->
                        val focusedModifier = if (i == dataTypeFocusedIndex) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    MaterialTheme.colorScheme.tertiaryContainer.copy(
                                        alpha = 0.8f,
                                    ),
                                )
                        } else {
                            Modifier.alpha(0.4f)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .hoverIconClickable()
                                .then(focusedModifier)
                                .clickable {
                                    onFocusedIndexUpdated(i)
                                },
                        ) {
                            Text(
                                dataType.className,
                                modifier = Modifier.padding(horizontal = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DataTypeListHeader(
    onDataTypeAdded: (String) -> Unit,
    onDataTypeWithFieldsAdded: (String, List<DataField>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var nameDialogOpen by remember { mutableStateOf(false) }
    var nameDialogFromJsonOpen by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(Res.string.data_type),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val contentDesc = stringResource(Res.string.data_type_tooltip)
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

        Spacer(Modifier.weight(1f))
        val addDataType = stringResource(Res.string.add_data_type)
        Tooltip(addDataType) {
            ComposeFlowIconButton(
                onClick = {
                    nameDialogOpen = true
                },
                modifier = Modifier
                    .hoverIconClickable()
                    .hoverOverlay(),
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = addDataType,
                )
            }
        }

        val addDataTypeFromJson = stringResource(Res.string.add_data_type_from_json)
        Tooltip(addDataTypeFromJson) {
            ComposeFlowIconButton(
                onClick = {
                    nameDialogFromJsonOpen = true
                },
                modifier = Modifier
                    .hoverIconClickable()
                    .hoverOverlay(),
            ) {
                Icon(
                    Icons.Outlined.DataObject,
                    contentDescription = addDataTypeFromJson,
                )
            }
        }

    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogClosed = LocalOnAllDialogsClosed.current
    if (nameDialogOpen) {
        onAnyDialogIsShown()
        val dialogClosed = {
            onAllDialogClosed()
            nameDialogOpen = false
        }
        NewNameDialog(
            label = stringResource(Res.string.add_data_type),
            onCloseClick = {
                dialogClosed()
            },
            onNameConfirmed = {
                onDataTypeAdded(it)
                dialogClosed()
            },
        )
    }

    if (nameDialogFromJsonOpen) {
        onAnyDialogIsShown()
        val dialogClosed = {
            onAllDialogClosed()
            nameDialogFromJsonOpen = false
        }
        DataTypeFromJsonDialog(
            onCloseClick = {
                dialogClosed()
            },
            onDataTypeWithFieldsAdded = onDataTypeWithFieldsAdded,
        )
    }
}

@Composable
fun NewNameDialog(
    label: String,
    onCloseClick: () -> Unit,
    onNameConfirmed: (String) -> Unit,
) {
    var projectName by remember { mutableStateOf("") }
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
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 280.dp, height = 160.dp)
                    .padding(16.dp),
            ) {
                var validateResult by remember {
                    mutableStateOf<ValidateResult>(
                        ValidateResult.Failure(
                            MUST_NOT_BE_EMPTY
                        )
                    )
                }
                SmallOutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        validateResult = KotlinClassNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            label,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.8f),
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = validateResult is ValidateResult.Failure,
                    supportingText = (validateResult as? ValidateResult.Failure)?.let {
                        {
                            Text(it.message)
                        }
                    },

                    modifier = Modifier.focusRequester(first)
                        .moveFocusOnTab()
                        .fillMaxWidth()
                        .onKeyEvent {
                            if (it.key == Key.Enter && projectName.isNotEmpty()) {
                                onNameConfirmed(projectName)
                                true
                            } else {
                                false
                            }
                        },
                )

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
                            onNameConfirmed(projectName)
                        },
                        enabled = validateResult is ValidateResult.Success,
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

const val emptyJSON = "Empty JSON"

@Composable
private fun DataTypeFromJsonDialog(
    onCloseClick: () -> Unit,
    onDataTypeWithFieldsAdded: (String, List<DataField>) -> Unit,
) {
    var projectName by remember { mutableStateOf("") }
    var validateResult by remember {
        mutableStateOf<ValidateResult>(
            ValidateResult.Failure(
                MUST_NOT_BE_EMPTY
            )
        )
    }

    var parseResult: DataTypeParseResult? by remember { mutableStateOf(null) }
    val parsedFields: List<DataField>? by remember(parseResult) {
        mutableStateOf(
            when (val result = parseResult) {
                is DataTypeParseResult.Failure -> null
                is DataTypeParseResult.Success -> result.dataType.fields
                is DataTypeParseResult.SuccessWithWarning -> result.dataType.fields
                DataTypeParseResult.EmptyInput -> null
                null -> null
            }
        )
    }
    val errorMessage: String? by remember(parseResult) {
        mutableStateOf(
            when (val result = parseResult) {
                DataTypeParseResult.EmptyInput -> emptyJSON
                is DataTypeParseResult.Failure -> result.message
                is DataTypeParseResult.Success -> null
                is DataTypeParseResult.SuccessWithWarning -> result.warningMessage
                null -> emptyJSON
            }
        )
    }

    val isFormValid by remember {
        derivedStateOf {
            parseResult?.isSuccess() == true &&
                    validateResult is ValidateResult.Success
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
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 680.dp, height = 620.dp)
                    .padding(16.dp),
            ) {
                SmallOutlinedTextField(
                    value = projectName,
                    onValueChange = {
                        projectName = it
                        validateResult = KotlinClassNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            stringResource(Res.string.data_type_name),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.8f),
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    isError = validateResult is ValidateResult.Failure,
                    supportingText = (validateResult as? ValidateResult.Failure)?.let {
                        {
                            Text(it.message)
                        }
                    },
                    modifier = Modifier.focusRequester(first)
                        .moveFocusOnTab()
                        .fillMaxWidth()
                )

                ParseDataTypeJsonTextField(
                    onJsonParsed = {
                        parseResult = it
                    },
                    modifier = Modifier
                        .padding(top = 16.dp)
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
                                onDataTypeWithFieldsAdded(projectName, it)
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
private fun DataTypeDetail(
    projectUiState: LoadedProjectUiState,
    focusedDataTypeIndex: Int?,
    onDataFieldAdded: (DataField) -> Unit,
    onDataFieldNameUpdated: (Int, String) -> Unit,
    onDeleteDataTypeIconClicked: () -> Unit,
    onDeleteDataFieldOfIndex: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .backgroundContainerNeutral()
            .padding(16.dp),
    ) {
        Spacer(Modifier.weight(1f))
        DataTypeDetailContent(
            projectUiState = projectUiState,
            focusedDataTypeIndex = focusedDataTypeIndex,
            onDataFieldAdded = onDataFieldAdded,
            onDataFieldNameUpdated = onDataFieldNameUpdated,
            onDeleteDataTypeIconClicked = onDeleteDataTypeIconClicked,
            onDeleteDataFieldOfIndex = onDeleteDataFieldOfIndex,
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun DataTypeDetailContent(
    projectUiState: LoadedProjectUiState,
    focusedDataTypeIndex: Int?,
    onDataFieldAdded: (DataField) -> Unit,
    onDataFieldNameUpdated: (Int, String) -> Unit,
    onDeleteDataTypeIconClicked: () -> Unit,
    onDeleteDataFieldOfIndex: (Int) -> Unit,
) {
    var addDataFieldDialogOpen by remember { mutableStateOf(false) }
    var indexOfDataFieldToBeEdited by remember { mutableStateOf<Int?>(null) }
    var indexOfDataFieldToBeDeleted by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .width(960.dp)
            .fillMaxHeight()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surface),
    ) {

        when (projectUiState) {
            is LoadedProjectUiState.Error -> {}
            LoadedProjectUiState.Loading -> {
                CircularProgressIndicator()
            }

            LoadedProjectUiState.NotFound -> {}
            is LoadedProjectUiState.Success -> {
                val dataType =
                    focusedDataTypeIndex?.let { projectUiState.project.dataTypeHolder.dataTypes[it] }
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    dataType?.let {
                        Row {
                            Text(
                                it.className,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 8.dp).padding(bottom = 16.dp),
                            )
                            Spacer(Modifier.weight(1f))
                            val contentDesc = stringResource(Res.string.delete_data_type)
                            Tooltip(contentDesc) {
                                IconButton(onClick = {
                                    onDeleteDataTypeIconClicked()
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = contentDesc,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        }

                        DataTypeDetailContentHeader()
                        LazyColumn(modifier = Modifier.heightIn(max = 800.dp)) {
                            itemsIndexed(dataType.fields) { i, dataField ->
                                DataTypeDetailFieldRow(
                                    dataField = dataField,
                                    index = i,
                                    onDataFieldNameUpdated = onDataFieldNameUpdated,
                                    onDeleteDataFieldDialogOpen = {
                                        indexOfDataFieldToBeDeleted = i
                                    },
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                addDataFieldDialogOpen = true
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Text("+ ${stringResource(Res.string.add_field)}")
                        }
                    }
                }

                val onAnyDialogIsOpen = LocalOnAnyDialogIsShown.current
                val onAllDialogsClosed = LocalOnAllDialogsClosed.current
                if (addDataFieldDialogOpen) {
                    onAnyDialogIsOpen()
                    val dialogClosed = {
                        addDataFieldDialogOpen = false
                        onAllDialogsClosed()
                    }
                    val initialValue = indexOfDataFieldToBeEdited?.let {
                        dataType?.fields?.get(it)
                    }
                    AddDataFieldDialog(
                        initialValue = initialValue,
                        updateIndex = indexOfDataFieldToBeEdited,
                        onDataFieldAdded = {
                            onDataFieldAdded(it)
                            dialogClosed()
                        },
                        onDataFieldNameUpdated = { i, inputName ->
                            onDataFieldNameUpdated(i, inputName)
                            dialogClosed()
                            indexOfDataFieldToBeEdited = null
                        },
                        onDialogClosed = dialogClosed,
                    )
                }

                indexOfDataFieldToBeDeleted?.let { indexToBeDeleted ->
                    onAnyDialogIsOpen()
                    DeleteDataFieldDialog(
                        index = indexToBeDeleted,
                        onCloseClick = {
                            indexOfDataFieldToBeDeleted = null
                            onAllDialogsClosed()
                        },
                        onDeleteDataFieldOfIndex = {
                            onDeleteDataFieldOfIndex(it)
                            onAllDialogsClosed()
                            indexOfDataFieldToBeDeleted = null
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun AddDataFieldDialog(
    initialValue: DataField? = null,
    updateIndex: Int? = null,
    onDataFieldAdded: (DataField) -> Unit,
    onDataFieldNameUpdated: (Int, String) -> Unit,
    onDialogClosed: () -> Unit,
) {
    var fieldName by remember { mutableStateOf(initialValue?.variableName ?: "") }
    var fieldType by remember { mutableStateOf(initialValue?.fieldType ?: FieldType.String()) }

    val onDataFieldConfirmed = {
        if (updateIndex == null) {
            onDataFieldAdded(
                DataField(
                    name = fieldName,
                    fieldType = fieldType,
                ),
            )
        } else {
            onDataFieldNameUpdated(
                updateIndex,
                fieldName
            )
        }
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
        val (first, second, third, fourth, fifth) = remember { FocusRequester.createRefs() }
        LaunchedEffect(Unit) {
            first.requestFocus()
        }
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 320.dp, height = 320.dp)
                    .padding(16.dp),
            ) {
                val fieldWidth = 300.dp
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.add_field),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }

                var fieldNameValidateResult by remember {
                    mutableStateOf<ValidateResult>(
                        ValidateResult.Failure(MUST_NOT_BE_EMPTY),
                    )
                }
                SmallOutlinedTextField(
                    value = fieldName,
                    onValueChange = {
                        fieldName = it
                        fieldNameValidateResult = KotlinVariableNameValidator().validate(it)
                    },
                    label = {
                        Text(
                            stringResource(Res.string.field_name),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.alpha(0.6f),
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    supportingText = (fieldNameValidateResult as? ValidateResult.Failure)?.let {
                        {
                            Text(it.message)
                        }
                    },
                    isError = fieldNameValidateResult is ValidateResult.Failure,
                    modifier = Modifier.focusRequester(first)
                        .moveFocusOnTab()
                        .width(width = fieldWidth)
                        .onKeyEvent {
                            if (it.key == Key.Enter && fieldName.isNotEmpty()) {
                                onDataFieldConfirmed()
                                true
                            } else {
                                false
                            }
                        },
                )
                var dropDownSelectedIndex by remember { mutableStateOf(0) }

                BasicDropdownPropertyEditor(
                    items = FieldType.entries(),
                    onValueChanged = { index, item ->
                        fieldType = item
                        dropDownSelectedIndex = index
                    },
                    selectedItem = fieldType,
                    label = stringResource(Res.string.field_type),
                    modifier = Modifier.padding(top = 8.dp)
                        .focusRequester(second)
                        .moveFocusOnTab(),
                )

                var defaultValueValidateResult by remember {
                    mutableStateOf<ValidateResult>(
                        ValidateResult.Success,
                    )
                }
                val isFormValid by remember {
                    derivedStateOf {
                        defaultValueValidateResult is ValidateResult.Success &&
                                fieldNameValidateResult is ValidateResult.Success
                    }
                }
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    when (fieldType) {
                        is FieldType.Boolean -> {
                            BooleanPropertyEditor(
                                checked = if (fieldType.defaultValue() is Boolean) fieldType.defaultValue() as Boolean else false,
                                onCheckedChange = {
                                    fieldType = fieldType.copyWithDefaultValue(it)
                                },
                                label = stringResource(Res.string.default_value),
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab(),
                            )
                        }

                        is FieldType.Int -> {
                            var value by remember {
                                mutableStateOf(
                                    if (fieldType.defaultValue() is Int) {
                                        fieldType.defaultValue()
                                            .toString()
                                    } else {
                                        ""
                                    },
                                )
                            }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        IntValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            fieldType = fieldType.copyWithDefaultValue(it.toInt())
                                        }

                                        is ValidateResult.Failure -> {}
                                    }
                                    defaultValueValidateResult = validateResult
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                supportingText = (defaultValueValidateResult as? ValidateResult.Failure)?.let {
                                    {
                                        Text(it.message)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueValidateResult is ValidateResult.Failure,
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onDataFieldConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is FieldType.Float -> {
                            var value by remember {
                                mutableStateOf(
                                    if (fieldType.defaultValue() is Float) {
                                        fieldType.defaultValue()
                                            .toString()
                                    } else {
                                        ""
                                    },
                                )
                            }
                            SmallOutlinedTextField(
                                value = value,
                                onValueChange = {
                                    val validateResult =
                                        FloatValidator().validate(input = it)
                                    when (validateResult) {
                                        ValidateResult.Success -> {
                                            fieldType = fieldType.copyWithDefaultValue(it.toFloat())
                                        }

                                        is ValidateResult.Failure -> {}
                                    }
                                    defaultValueValidateResult = validateResult
                                    value = it
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                supportingText = (defaultValueValidateResult as? ValidateResult.Failure)?.let {
                                    {
                                        Text(it.message)
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                isError = defaultValueValidateResult is ValidateResult.Failure,
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onDataFieldConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is FieldType.String -> {
                            SmallOutlinedTextField(
                                value = if (fieldType.defaultValue() is String) {
                                    fieldType.defaultValue()
                                        .toString()
                                } else {
                                    ""
                                },
                                onValueChange = {
                                    fieldType = fieldType.copyWithDefaultValue(it)
                                },
                                label = {
                                    Text(
                                        stringResource(Res.string.default_value),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.alpha(0.6f),
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.focusRequester(third)
                                    .moveFocusOnTab()
                                    .width(width = fieldWidth)
                                    .onKeyEvent {
                                        if (it.key == Key.Enter && isFormValid) {
                                            onDataFieldConfirmed()
                                            true
                                        } else {
                                            false
                                        }
                                    },
                            )
                        }

                        is FieldType.Instant -> {}
                        is FieldType.CustomDataType -> {}
                        is FieldType.DocumentId -> {}
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
                            .focusRequester(fourth),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onDataFieldConfirmed()
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .focusRequester(fifth),
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun DataTypeDetailContentHeader() {
    Column {
        Row {
            Text(
                stringResource(Res.string.field_name),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(240.dp),
            )

            Text(
                stringResource(Res.string.field_type),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(240.dp),
            )

            Text(
                stringResource(Res.string.default_value),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(300.dp),
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun DataTypeDetailFieldRow(
    dataField: DataField,
    index: Int,
    onDataFieldNameUpdated: (Int, String) -> Unit,
    onDeleteDataFieldDialogOpen: (Int) -> Unit,
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditableText(
                initialText = dataField.variableName,
                onValueChange = {
                    onDataFieldNameUpdated(index, it)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.width(240.dp),
            )

            Text(
                text = dataField.fieldType.asDropdownText(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.width(240.dp),
            )

            Text(
                dataField.fieldType.defaultValue()?.toString() ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(300.dp),
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    onDeleteDataFieldDialogOpen(index)
                },
            ) {
                val contentDesc = stringResource(Res.string.delete_data_field)
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
}

@Composable
fun DeleteDataFieldDialog(
    index: Int,
    onCloseClick: () -> Unit,
    onDeleteDataFieldOfIndex: (Int) -> Unit,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 300.dp, height = 160.dp)
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.delete_data_field) + "?",
                )

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier = Modifier
                            .padding(end = 16.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onDeleteDataFieldOfIndex(index)
                        },
                        modifier = Modifier,
                    ) {
                        Text(
                            text = stringResource(Res.string.delete),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteDataTypeDialog(
    onCloseClick: () -> Unit,
    onDeleteDataType: () -> Unit,
) {
    PositionCustomizablePopup(
        onDismissRequest = {
            onCloseClick()
        },
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                onCloseClick()
                true
            } else {
                false
            }
        },
    ) {
        Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
            Column(
                modifier = Modifier
                    .size(width = 300.dp, height = 160.dp)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Delete data type?",
                )

                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    TextButton(
                        onClick = {
                            onCloseClick()
                        },
                        modifier = Modifier
                            .padding(end = 16.dp),
                    ) {
                        Text("Cancel")
                    }
                    OutlinedButton(
                        onClick = {
                            onDeleteDataType()
                        },
                        modifier = Modifier,
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
