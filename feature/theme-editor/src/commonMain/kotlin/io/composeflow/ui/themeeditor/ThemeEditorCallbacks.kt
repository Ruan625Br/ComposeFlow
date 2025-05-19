package io.composeflow.ui.themeeditor

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import io.composeflow.font.FontFamilyWrapper
import io.composeflow.model.enumwrapper.TextStyleWrapper
import io.composeflow.model.project.theme.TextStyleOverride
import com.materialkolor.PaletteStyle

data class ThemeEditorCallbacks(
    val onColorSchemeUpdated: (
        sourceColor: Color,
        paletteStyle: PaletteStyle,
        lightScheme: ColorScheme,
        darkScheme: ColorScheme,
    ) -> Unit,
    val onColorResetToDefault: () -> Unit,
    val onPrimaryFontFamilyChanged: (FontFamilyWrapper) -> Unit,
    val onSecondaryFontFamilyChanged: (FontFamilyWrapper) -> Unit,
    val onTextStyleOverridesChanged: (TextStyleWrapper, TextStyleOverride) -> Unit,
    val onApplyFontEditableParams: () -> Unit,
    val onResetFonts: () -> Unit,
)