package io.composeflow.ui.themeeditor

import io.composeflow.font.FontFamilyWrapper
import io.composeflow.model.project.theme.TextStyleOverrides

data class FontEditableParams(
    var primaryFontFamily: FontFamilyWrapper,
    var secondaryFontFamily: FontFamilyWrapper,
    var textStyleOverrides: TextStyleOverrides,
)
