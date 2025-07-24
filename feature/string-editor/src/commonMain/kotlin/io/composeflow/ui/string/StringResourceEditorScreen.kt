package io.composeflow.ui.string

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CursorDropdownMenu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.action_add
import io.composeflow.add_locale
import io.composeflow.add_new_locale
import io.composeflow.add_new_string_resource
import io.composeflow.add_string_resource
import io.composeflow.auth.LocalFirebaseIdToken
import io.composeflow.cancel
import io.composeflow.default_locale_label
import io.composeflow.delete_string_resource
import io.composeflow.delete_string_resource_confirmation
import io.composeflow.model.project.Project
import io.composeflow.model.project.string.StringResource
import io.composeflow.more_options
import io.composeflow.remove
import io.composeflow.remove_locale
import io.composeflow.remove_locale_confirmation
import io.composeflow.set_as_default_locale
import io.composeflow.string_resource_default_value_placeholder
import io.composeflow.string_resource_description
import io.composeflow.string_resource_description_placeholder
import io.composeflow.string_resource_key
import io.composeflow.string_resource_key_placeholder
import io.composeflow.string_resource_language_code_placeholder
import io.composeflow.string_resource_region_code_placeholder
import io.composeflow.string_resources
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.modifier.backgroundContainerNeutral
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.textfield.SmallOutlinedTextField
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun StringResourceEditorScreen(
    project: Project,
    modifier: Modifier = Modifier,
) {
    val firebaseIdToken = LocalFirebaseIdToken.current
    val viewModel =
        viewModel(StringResourceEditorViewModel::class) {
            StringResourceEditorViewModel(firebaseIdToken, project)
        }
    Surface(modifier = modifier.fillMaxSize()) {
        StringResourceEditorContent(
            project = project,
            onAddStringResource = viewModel::onAddStringResource,
            onUpdateStringResourceKey = viewModel::onUpdateStringResourceKey,
            onUpdateStringResourceDescription = viewModel::onUpdateStringResourceDescription,
            onUpdateStringResourceValue = viewModel::onUpdateStringResourceValue,
            onDeleteStringResource = viewModel::onDeleteStringResource,
            onAddLocale = viewModel::onAddLocale,
            onRemoveLocale = viewModel::onRemoveLocale,
            onUpdateDefaultLocale = viewModel::onUpdateDefaultLocale,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun StringResourceEditorContent(
    project: Project,
    onAddStringResource: (key: String, description: String, defaultValue: String) -> Unit,
    onUpdateStringResourceKey: (StringResource, String) -> Unit,
    onUpdateStringResourceDescription: (StringResource, String) -> Unit,
    onUpdateStringResourceValue: (StringResource, StringResource.Locale, String) -> Unit,
    onDeleteStringResource: (StringResource) -> Unit,
    onAddLocale: (language: String, region: String) -> Unit,
    onRemoveLocale: (StringResource.Locale) -> Unit,
    onUpdateDefaultLocale: (StringResource.Locale) -> Unit,
    modifier: Modifier = Modifier,
) {
    var addResourceDialogOpen by remember { mutableStateOf(false) }
    var addLocaleDialogOpen by remember { mutableStateOf(false) }
    var localeToDelete by remember { mutableStateOf<StringResource.Locale?>(null) }
    var resourceToDelete by remember { mutableStateOf<StringResource?>(null) }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current

    val defaultLocale by project.stringResourceHolder.defaultLocale
    val supportedLocales =
        project.stringResourceHolder.supportedLocales.sortedWith { a, b ->
            when {
                // Default locale always comes first
                a == defaultLocale -> -1
                b == defaultLocale -> 1
                // Otherwise sort alphabetically
                else -> a.toString().compareTo(b.toString())
            }
        }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .backgroundContainerNeutral()
                .padding(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.string_resources),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp).padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        addLocaleDialogOpen = true
                    },
                ) {
                    Text(stringResource(Res.string.add_locale))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val horizontalScrollState = rememberScrollState()
            Column(
                modifier = Modifier.horizontalScroll(horizontalScrollState),
            ) {
                var tableWidthPx by remember { mutableStateOf(0) }
                val tableWidthDp = with(LocalDensity.current) { tableWidthPx.toDp() }

                StringResourceTableHeader(
                    supportedLocales = supportedLocales,
                    defaultLocale = defaultLocale,
                    onUpdateDefaultLocale = onUpdateDefaultLocale,
                    onRemoveLocale = { locale -> localeToDelete = locale },
                    modifier = Modifier.width(tableWidthDp),
                )

                TextButton(
                    shape = RectangleShape,
                    onClick = {
                        addResourceDialogOpen = true
                    },
                    modifier = Modifier.width(tableWidthDp),
                ) {
                    Text(text = stringResource(Res.string.add_string_resource))
                }
                HorizontalDivider(modifier = Modifier.width(tableWidthDp))

                LazyColumn(
                    modifier =
                        Modifier
                            .weight(1f)
                            .onSizeChanged { tableWidthPx = it.width },
                ) {
                    items(project.stringResourceHolder.stringResources) { resource ->
                        StringResourceTableRow(
                            resource = resource,
                            supportedLocales = supportedLocales,
                            defaultLocale = defaultLocale,
                            onUpdateKey = { onUpdateStringResourceKey(resource, it) },
                            onUpdateDescription = { onUpdateStringResourceDescription(resource, it) },
                            onUpdateValue = { locale, value ->
                                onUpdateStringResourceValue(resource, locale, value)
                            },
                            onDelete = {
                                resourceToDelete = resource
                            },
                        )
                    }
                }
            }
        }
    }

    if (addResourceDialogOpen) {
        onAnyDialogIsShown()
        AddNewResourceDialog(
            defaultLocale = defaultLocale,
            onAddResource = { key, description, defaultValue ->
                onAddStringResource(key, description, defaultValue)
                addResourceDialogOpen = false
                onAllDialogsClosed()
            },
            onDismiss = {
                addResourceDialogOpen = false
                onAllDialogsClosed()
            },
        )
    }

    if (addLocaleDialogOpen) {
        onAnyDialogIsShown()
        AddNewLocaleDialog(
            onAddLocale = { language, region ->
                onAddLocale(language, region)
                addLocaleDialogOpen = false
                onAllDialogsClosed()
            },
            onDismiss = {
                addLocaleDialogOpen = false
                onAllDialogsClosed()
            },
        )
    }

    localeToDelete?.let { locale ->
        onAnyDialogIsShown()
        SimpleConfirmationDialog(
            text = stringResource(Res.string.remove_locale_confirmation, locale.toString()),
            onConfirmClick = {
                onRemoveLocale(locale)
                localeToDelete = null
                onAllDialogsClosed()
            },
            onCloseClick = {
                localeToDelete = null
                onAllDialogsClosed()
            },
            positiveText = stringResource(Res.string.remove),
        )
    }

    resourceToDelete?.let { resource ->
        onAnyDialogIsShown()
        SimpleConfirmationDialog(
            text = stringResource(Res.string.delete_string_resource_confirmation, resource.key),
            onConfirmClick = {
                onDeleteStringResource(resource)
                resourceToDelete = null
                onAllDialogsClosed()
            },
            onCloseClick = {
                resourceToDelete = null
                onAllDialogsClosed()
            },
            positiveText = stringResource(Res.string.remove),
        )
    }
}

@Composable
private fun StringResourceTableHeader(
    supportedLocales: List<StringResource.Locale>,
    defaultLocale: StringResource.Locale,
    onUpdateDefaultLocale: (StringResource.Locale) -> Unit,
    onRemoveLocale: (StringResource.Locale) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(Modifier.height(IntrinsicSize.Max)) {
            Box(
                modifier = Modifier.width(200.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = stringResource(Res.string.string_resource_key),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            VerticalDivider()
            Box(
                modifier = Modifier.width(200.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = stringResource(Res.string.string_resource_description),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
            supportedLocales.forEach { locale ->
                VerticalDivider()
                LocaleHeaderCell(
                    locale = locale,
                    isDefault = locale == defaultLocale,
                    onSetAsDefault = { onUpdateDefaultLocale(locale) },
                    onRemove = { onRemoveLocale(locale) },
                )
            }
            VerticalDivider()
            Spacer(Modifier.width(40.dp))
        }
        HorizontalDivider()
    }
}

@Composable
private fun StringResourceTableRow(
    resource: StringResource,
    supportedLocales: List<StringResource.Locale>,
    defaultLocale: StringResource.Locale,
    onUpdateKey: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateValue: (StringResource.Locale, String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.width(IntrinsicSize.Max)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.height(IntrinsicSize.Max),
        ) {
            Box(
                modifier = Modifier.width(200.dp).fillMaxHeight(),
                contentAlignment = Alignment.CenterStart,
            ) {
                CellEditableText(
                    initialText = resource.key,
                    onValueChange = onUpdateKey,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            VerticalDivider()

            Box(
                modifier = Modifier.width(200.dp).fillMaxHeight(),
                contentAlignment = Alignment.CenterStart,
            ) {
                CellEditableText(
                    initialText = resource.description ?: "",
                    onValueChange = onUpdateDescription,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            supportedLocales.forEach { locale ->
                VerticalDivider()
                Box(
                    modifier = Modifier.width(200.dp).fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    CellEditableText(
                        initialText = resource.localizedValues[locale] ?: "",
                        onValueChange = { onUpdateValue(locale, it) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        enabled = locale == defaultLocale,
                    )
                }
            }

            VerticalDivider()

            IconButton(
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(Res.string.delete_string_resource),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun LocaleHeaderCell(
    locale: StringResource.Locale,
    isDefault: Boolean,
    onSetAsDefault: () -> Unit,
    onRemove: () -> Unit,
) {
    var showDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.width(200.dp).padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text =
                buildString {
                    append(locale.toString())
                    if (isDefault) {
                        append(stringResource(Res.string.default_locale_label))
                    }
                },
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { showDropdown = true },
            modifier = Modifier.size(20.dp),
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(Res.string.more_options),
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(16.dp),
            )
        }

        CursorDropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
        ) {
            if (!isDefault) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.set_as_default_locale)) },
                    onClick = {
                        onSetAsDefault()
                        showDropdown = false
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.remove_locale)) },
                onClick = {
                    onRemove()
                    showDropdown = false
                },
                enabled = !isDefault,
            )
        }
    }
}

@Composable
private fun AddNewResourceDialog(
    defaultLocale: StringResource.Locale,
    onAddResource: (key: String, description: String, defaultValue: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var resourceKey by remember { mutableStateOf("") }
    var resourceDescription by remember { mutableStateOf("") }
    var resourceDefaultValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.add_new_string_resource)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                SmallOutlinedTextField(
                    value = resourceKey,
                    onValueChange = { resourceKey = it },
                    placeholder = { Text(stringResource(Res.string.string_resource_key_placeholder)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .consumeTabKeyEventForFocus(focusManager),
                )
                SmallOutlinedTextField(
                    value = resourceDescription,
                    onValueChange = { resourceDescription = it },
                    placeholder = { Text(stringResource(Res.string.string_resource_description_placeholder)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .consumeTabKeyEventForFocus(focusManager),
                )
                SmallOutlinedTextField(
                    value = resourceDefaultValue,
                    onValueChange = { resourceDefaultValue = it },
                    placeholder = { Text(stringResource(Res.string.string_resource_default_value_placeholder, defaultLocale.toString())) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .consumeTabKeyEventForFocus(focusManager),
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddResource(resourceKey, resourceDescription, resourceDefaultValue)
                },
            ) {
                Text(stringResource(Res.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

@Composable
private fun AddNewLocaleDialog(
    onAddLocale: (language: String, region: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var localeLanguage by remember { mutableStateOf("") }
    var localeRegion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.add_new_locale)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val focusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                SmallOutlinedTextField(
                    value = localeLanguage,
                    onValueChange = { localeLanguage = it.lowercase() },
                    placeholder = { Text(stringResource(Res.string.string_resource_language_code_placeholder)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .consumeTabKeyEventForFocus(focusManager),
                )
                SmallOutlinedTextField(
                    value = localeRegion,
                    onValueChange = { localeRegion = it.uppercase() },
                    placeholder = { Text(stringResource(Res.string.string_resource_region_code_placeholder)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .consumeTabKeyEventForFocus(focusManager),
                )
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAddLocale(localeLanguage, localeRegion)
                },
            ) {
                Text(stringResource(Res.string.action_add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

private fun Modifier.consumeTabKeyEventForFocus(focusManager: FocusManager): Modifier =
    this.onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyDown && event.key == Key.Tab) {
            focusManager.moveFocus(if (event.isShiftPressed) FocusDirection.Previous else FocusDirection.Next)
            true
        } else {
            false
        }
    }
