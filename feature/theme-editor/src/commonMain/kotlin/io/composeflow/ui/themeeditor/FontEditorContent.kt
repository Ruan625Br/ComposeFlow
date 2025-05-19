package io.composeflow.ui.themeeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.apply_change
import io.composeflow.change_font_family
import io.composeflow.editor.validator.FloatValidator
import io.composeflow.editor.validator.IntValidator
import io.composeflow.editor.validator.ValidateResult
import io.composeflow.font.FontFamilyWrapper
import io.composeflow.font.FontWeightWrapper
import io.composeflow.font_family
import io.composeflow.font_size
import io.composeflow.font_weight
import io.composeflow.letter_spacing
import io.composeflow.lorem_ipsum
import io.composeflow.model.enumwrapper.TextStyleWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.theme.FontFamilyCandidate
import io.composeflow.model.project.theme.TextStyleOverride
import io.composeflow.model.project.theme.generateWithOverrides
import io.composeflow.reset
import io.composeflow.reset_fonts
import io.composeflow.reset_fonts_to_defaults
import io.composeflow.style_name
import io.composeflow.ui.LocalOnAllDialogsClosed
import io.composeflow.ui.LocalOnAnyDialogIsShown
import io.composeflow.ui.LocalOnShowSnackbar
import io.composeflow.ui.modifier.hoverIconClickable
import io.composeflow.ui.popup.PositionCustomizablePopup
import io.composeflow.ui.popup.SimpleConfirmationDialog
import io.composeflow.ui.propertyeditor.BasicDropdownPropertyEditor
import io.composeflow.ui.textfield.SmallOutlinedTextField
import io.composeflow.updated_fonts
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun FontEditorContent(
    project: Project,
    callbacks: ThemeEditorCallbacks,
    fontEditableParams: FontEditableParams,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Spacer(Modifier.weight(1f))
        FontEditorDetailContent(
            project = project,
            callbacks = callbacks,
            fontEditableParams = fontEditableParams,
            modifier = modifier
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun FontEditorDetailContent(
    project: Project,
    callbacks: ThemeEditorCallbacks,
    fontEditableParams: FontEditableParams,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .width(1120.dp)
            .fillMaxHeight()
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp)) {
            FontDetailContentHeader(
                project = project,
                callbacks = callbacks,
                fontEditableParams = fontEditableParams,
            )

            Spacer(modifier = Modifier.size(48.dp))

            FontTableHeader()
            FontTable(
                callbacks = callbacks,
                fontEditableParams = fontEditableParams,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FontDetailContentHeader(
    project: Project,
    callbacks: ThemeEditorCallbacks,
    fontEditableParams: FontEditableParams,
) {
    var changeFontFamilyCallback: ((FontFamilyWrapper) -> Unit)? by remember { mutableStateOf(null) }
    var openResetConfirmation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Row {
        Text(
            text = "Fonts",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.weight(1f))
        val primaryFontFamily = fontEditableParams.primaryFontFamily
        val secondaryFontFamily = fontEditableParams.secondaryFontFamily
        SmallOutlinedTextField(
            value = primaryFontFamily.name,
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = primaryFontFamily.asFontFamily()),
            label = { Text("Primary font family") },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                changeFontFamilyCallback = callbacks.onPrimaryFontFamilyChanged
                            }
                        }
                    }
                },
        )
        Spacer(Modifier.size(16.dp))
        SmallOutlinedTextField(
            value = secondaryFontFamily.name,
            onValueChange = {},
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = secondaryFontFamily.asFontFamily()),
            label = { Text("Secondary font family") },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                changeFontFamilyCallback = callbacks.onSecondaryFontFamilyChanged
                            }
                        }
                    }
                },
        )

        val onShowSnackbar = LocalOnShowSnackbar.current
        Spacer(Modifier.size(16.dp))
        val updatedFonts = stringResource(Res.string.updated_fonts)
        val existingTextOverrides =
            project.themeHolder.fontHolder.textStyleOverrides.entries.map { it.toPair() }.toSet()
        val editedTextOverriddes =
            fontEditableParams.textStyleOverrides.entries.map { it.toPair() }.toSet()
        TextButton(
            onClick = {
                callbacks.onApplyFontEditableParams()
                coroutineScope.launch {
                    onShowSnackbar(updatedFonts, null)
                }
            },
            enabled = project.themeHolder.fontHolder.primaryFontFamily != fontEditableParams.primaryFontFamily ||
                    project.themeHolder.fontHolder.secondaryFontFamily != fontEditableParams.secondaryFontFamily ||
                    existingTextOverrides != editedTextOverriddes
        ) {
            Text(stringResource(Res.string.apply_change))
        }
        Spacer(Modifier.size(16.dp))

        TextButton(
            onClick = {
                openResetConfirmation = true
            },
        ) {
            Text(
                stringResource(Res.string.reset_fonts),
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(Modifier.size(32.dp))
    }

    val onAnyDialogIsShown = LocalOnAnyDialogIsShown.current
    val onAllDialogsClosed = LocalOnAllDialogsClosed.current
    changeFontFamilyCallback?.let {
        onAnyDialogIsShown()
        val closeDialog = {
            onAllDialogsClosed()
            changeFontFamilyCallback = null
        }
        PositionCustomizablePopup(
            onDismissRequest = closeDialog,
        ) {
            Surface {
                ChangeFontFamilyDialogContent(
                    closeDialog = closeDialog,
                    changeFontFamilyCallback = it
                )
            }
        }
    }

    if (openResetConfirmation) {
        val resetFontsToDefaults = stringResource(Res.string.reset_fonts_to_defaults)
        onAnyDialogIsShown()
        val closeDialog = {
            onAllDialogsClosed()
            openResetConfirmation = false
        }
        val onShowSnackbar = LocalOnShowSnackbar.current
        SimpleConfirmationDialog(
            text = "$resetFontsToDefaults?",
            positiveText = stringResource(Res.string.reset),
            onCloseClick = {
                onAllDialogsClosed()
                closeDialog()
            },
            onConfirmClick = {
                callbacks.onResetFonts()
                closeDialog()
                coroutineScope.launch {
                    onShowSnackbar(resetFontsToDefaults, null)
                }
            }
        )
    }
}

@Composable
private fun ChangeFontFamilyDialogContent(
    closeDialog: () -> Unit,
    changeFontFamilyCallback: (FontFamilyWrapper) -> Unit,
) {
    Column(
        modifier = Modifier.size(980.dp)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.change_font_family),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.size(32.dp))

        LazyColumn {
            items(FontFamilyWrapper.entries) { fontFamily ->
                ElevatedCard(
                    onClick = {
                        changeFontFamilyCallback(fontFamily)
                        closeDialog()
                    },
                    modifier = Modifier.fillMaxWidth()
                        .hoverIconClickable()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = fontFamily.displayName,
                            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamilyWrapper.Roboto.asFontFamily()),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = stringResource(Res.string.lorem_ipsum),
                            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = fontFamily.asFontFamily()),
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
                Spacer(Modifier.size(16.dp))
            }
        }
    }
}

private val styleNameDp = 480.dp
private val fontSizeDp = 90.dp
private val letterSpacingDp = 120.dp
private val fontWeightDp = 200.dp
private val fontFamilyDp = 200.dp

@Composable
private fun FontTableHeader() {
    Column {
        Row {
            Text(
                stringResource(Res.string.style_name),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(styleNameDp).weight(1f),
            )

            Text(
                stringResource(Res.string.font_size),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(fontSizeDp),
            )

            Text(
                stringResource(Res.string.letter_spacing),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(letterSpacingDp),
            )

            Text(
                stringResource(Res.string.font_weight),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(fontWeightDp),
            )

            Text(
                stringResource(Res.string.font_family),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(fontFamilyDp),
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun FontTable(
    callbacks: ThemeEditorCallbacks,
    fontEditableParams: FontEditableParams,
    modifier: Modifier,
) {
    val typography = MaterialTheme.typography.generateWithOverrides(
        fontEditableParams.primaryFontFamily,
        fontEditableParams.secondaryFontFamily,
        fontEditableParams.textStyleOverrides
    )
    LazyColumn(modifier = modifier) {
        items(TextStyleWrapper.entries) { textStyleWrapper ->
            val textStyle = typography.matchingTextStyle(textStyleWrapper)
            val textStyleOverride =
                fontEditableParams.textStyleOverrides[textStyleWrapper] ?: TextStyleOverride()
            var fontSizeStr by remember {
                mutableStateOf(
                    textStyle.fontSize.value.toInt().toString()
                )
            }
            val fontSizeValidateResult: ValidateResult by remember(fontSizeStr) {
                mutableStateOf(
                    IntValidator(
                        allowLessThanZero = false,
                        maxValue = 120
                    ).validate(fontSizeStr)
                )
            }
            var letterSpacingStr by remember { mutableStateOf(textStyle.letterSpacing.value.toString()) }
            val letterSpacingValidateResult: ValidateResult by remember(letterSpacingStr) {
                mutableStateOf(
                    FloatValidator().validate(letterSpacingStr)
                )
            }

            Column(modifier = Modifier.heightIn(max = 120.dp)) {
                Spacer(Modifier.size(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        textStyleWrapper.displayName,
                        style = textStyle,
                        modifier = Modifier.width(styleNameDp).weight(1f),
                    )
                    SmallOutlinedTextField(
                        value = fontSizeStr,
                        onValueChange = {
                            fontSizeStr = it
                            if (fontSizeValidateResult is ValidateResult.Success) {
                                callbacks.onTextStyleOverridesChanged(
                                    textStyleWrapper,
                                    textStyleOverride.copy(fontSize = fontSizeStr.toIntOrNull())
                                )
                            }
                        },
                        isError = fontSizeValidateResult is ValidateResult.Failure,
                        modifier = Modifier.width(fontSizeDp).padding(end = 16.dp)
                    )
                    SmallOutlinedTextField(
                        value = letterSpacingStr,
                        onValueChange = {
                            letterSpacingStr = it
                            if (letterSpacingValidateResult is ValidateResult.Success) {
                                callbacks.onTextStyleOverridesChanged(
                                    textStyleWrapper,
                                    textStyleOverride.copy(letterSpacing = letterSpacingStr.toFloatOrNull())
                                )
                            }
                        },
                        isError = letterSpacingValidateResult is ValidateResult.Failure,
                        modifier = Modifier.width(letterSpacingDp).padding(end = 16.dp)
                    )
                    BasicDropdownPropertyEditor(
                        items = FontWeightWrapper.entries,
                        onValueChanged = { _, item ->
                            callbacks.onTextStyleOverridesChanged(
                                textStyleWrapper,
                                textStyleOverride.copy(fontWeight = item)
                            )
                        },
                        selectedItem = FontWeightWrapper.fromFontWeight(textStyle.fontWeight),
                        modifier = Modifier.width(fontWeightDp).padding(end = 16.dp),
                    )
                    BasicDropdownPropertyEditor(
                        items = FontFamilyCandidate.entries,
                        onValueChanged = { _, item ->
                            callbacks.onTextStyleOverridesChanged(
                                textStyleWrapper,
                                textStyleOverride.copy(fontFamilyCandidate = item)
                            )
                        },
                        selectedItem = textStyleOverride.fontFamilyCandidate,
                        modifier = Modifier.width(fontWeightDp).padding(end = 16.dp),
                    )
                }
                Spacer(Modifier.size(8.dp))
            }
        }
    }
}

fun Typography.matchingTextStyle(textStyleWrapper: TextStyleWrapper): TextStyle {
    return when (textStyleWrapper) {
        TextStyleWrapper.DisplayLarge -> displayLarge
        TextStyleWrapper.DisplayMedium -> displayMedium
        TextStyleWrapper.DisplaySmall -> displaySmall
        TextStyleWrapper.HeadlineLarge -> headlineLarge
        TextStyleWrapper.HeadlineMedium -> headlineMedium
        TextStyleWrapper.HeadlineSmall -> headlineSmall
        TextStyleWrapper.TitleLarge -> titleLarge
        TextStyleWrapper.TitleMedium -> titleMedium
        TextStyleWrapper.TitleSmall -> titleSmall
        TextStyleWrapper.BodyLarge -> bodyLarge
        TextStyleWrapper.BodyMedium -> bodyMedium
        TextStyleWrapper.BodySmall -> bodySmall
        TextStyleWrapper.LabelLarge -> labelLarge
        TextStyleWrapper.LabelMedium -> labelMedium
        TextStyleWrapper.LabelSmall -> labelSmall
    }
}