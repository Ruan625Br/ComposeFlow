package io.composeflow.font

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.composeflow.BaskervvilleSC_Regular
import io.composeflow.BungeeTint_Regular
import io.composeflow.Caveat_Bold
import io.composeflow.Caveat_Medium
import io.composeflow.Caveat_Regular
import io.composeflow.Caveat_SemiBold
import io.composeflow.CrimsonText_Bold
import io.composeflow.CrimsonText_BoldItalic
import io.composeflow.CrimsonText_Italic
import io.composeflow.CrimsonText_Regular
import io.composeflow.CrimsonText_SemiBold
import io.composeflow.CrimsonText_SemiBoldItalic
import io.composeflow.DancingScript_Bold
import io.composeflow.DancingScript_Medium
import io.composeflow.DancingScript_Regular
import io.composeflow.DancingScript_SemiBold
import io.composeflow.Handjet_Black
import io.composeflow.Handjet_Bold
import io.composeflow.Handjet_ExtraBold
import io.composeflow.Handjet_ExtraLight
import io.composeflow.Handjet_Light
import io.composeflow.Handjet_Medium
import io.composeflow.Handjet_Regular
import io.composeflow.Handjet_SemiBold
import io.composeflow.Handjet_Thin
import io.composeflow.Lato_Black
import io.composeflow.Lato_BlackItalic
import io.composeflow.Lato_Bold
import io.composeflow.Lato_BoldItalic
import io.composeflow.Lato_Italic
import io.composeflow.Lato_Light
import io.composeflow.Lato_LightItalic
import io.composeflow.Lato_Regular
import io.composeflow.Lato_Thin
import io.composeflow.Lato_ThinItalic
import io.composeflow.LibreBaskerville_Bold
import io.composeflow.LibreBaskerville_Italic
import io.composeflow.LibreBaskerville_Regular
import io.composeflow.Montserrat_Black
import io.composeflow.Montserrat_BlackItalic
import io.composeflow.Montserrat_Bold
import io.composeflow.Montserrat_BoldItalic
import io.composeflow.Montserrat_ExtraBold
import io.composeflow.Montserrat_ExtraBoldItalic
import io.composeflow.Montserrat_ExtraLight
import io.composeflow.Montserrat_ExtraLightItalic
import io.composeflow.Montserrat_Italic
import io.composeflow.Montserrat_Light
import io.composeflow.Montserrat_LightItalic
import io.composeflow.Montserrat_Medium
import io.composeflow.Montserrat_MediumItalic
import io.composeflow.Montserrat_Regular
import io.composeflow.Montserrat_SemiBold
import io.composeflow.Montserrat_SemiBoldItalic
import io.composeflow.Montserrat_Thin
import io.composeflow.Montserrat_ThinItalic
import io.composeflow.NerkoOne_Regular
import io.composeflow.NotoSansJP_Regular
import io.composeflow.Oswald_Bold
import io.composeflow.Oswald_ExtraLight
import io.composeflow.Oswald_Light
import io.composeflow.Oswald_Medium
import io.composeflow.Oswald_Regular
import io.composeflow.Oswald_SemiBold
import io.composeflow.PTSans_Bold
import io.composeflow.PTSans_BoldItalic
import io.composeflow.PTSans_Italic
import io.composeflow.PTSans_Regular
import io.composeflow.PTSerif_Bold
import io.composeflow.PTSerif_BoldItalic
import io.composeflow.PTSerif_Italic
import io.composeflow.PTSerif_Regular
import io.composeflow.PlayfairDisplay_Black
import io.composeflow.PlayfairDisplay_BlackItalic
import io.composeflow.PlayfairDisplay_Bold
import io.composeflow.PlayfairDisplay_BoldItalic
import io.composeflow.PlayfairDisplay_ExtraBold
import io.composeflow.PlayfairDisplay_ExtraBoldItalic
import io.composeflow.PlayfairDisplay_Italic
import io.composeflow.PlayfairDisplay_Medium
import io.composeflow.PlayfairDisplay_MediumItalic
import io.composeflow.PlayfairDisplay_Regular
import io.composeflow.PlayfairDisplay_SemiBold
import io.composeflow.PlayfairDisplay_SemiBoldItalic
import io.composeflow.ProtestGuerrilla_Regular
import io.composeflow.Raleway_Black
import io.composeflow.Raleway_BlackItalic
import io.composeflow.Raleway_Bold
import io.composeflow.Raleway_BoldItalic
import io.composeflow.Raleway_ExtraBold
import io.composeflow.Raleway_ExtraBoldItalic
import io.composeflow.Raleway_ExtraLight
import io.composeflow.Raleway_ExtraLightItalic
import io.composeflow.Raleway_Italic
import io.composeflow.Raleway_Light
import io.composeflow.Raleway_LightItalic
import io.composeflow.Raleway_Medium
import io.composeflow.Raleway_MediumItalic
import io.composeflow.Raleway_Regular
import io.composeflow.Raleway_SemiBold
import io.composeflow.Raleway_SemiBoldItalic
import io.composeflow.Raleway_Thin
import io.composeflow.Raleway_ThinItalic
import io.composeflow.Res
import io.composeflow.Roboto_Black
import io.composeflow.Roboto_BlackItalic
import io.composeflow.Roboto_Bold
import io.composeflow.Roboto_BoldItalic
import io.composeflow.Roboto_Italic
import io.composeflow.Roboto_Light
import io.composeflow.Roboto_LightItalic
import io.composeflow.Roboto_Medium
import io.composeflow.Roboto_MediumItalic
import io.composeflow.Roboto_Regular
import io.composeflow.Roboto_Thin
import io.composeflow.Roboto_ThinItalic
import io.composeflow.SUSE_Bold
import io.composeflow.SUSE_ExtraBold
import io.composeflow.SUSE_ExtraLight
import io.composeflow.SUSE_Light
import io.composeflow.SUSE_Medium
import io.composeflow.SUSE_Regular
import io.composeflow.SUSE_SemiBold
import io.composeflow.SUSE_Thin
import io.composeflow.Sevillana_Regular
import io.composeflow.SourceCodePro_Black
import io.composeflow.SourceCodePro_BlackItalic
import io.composeflow.SourceCodePro_Bold
import io.composeflow.SourceCodePro_BoldItalic
import io.composeflow.SourceCodePro_ExtraBold
import io.composeflow.SourceCodePro_ExtraBoldItalic
import io.composeflow.SourceCodePro_ExtraLight
import io.composeflow.SourceCodePro_ExtraLightItalic
import io.composeflow.SourceCodePro_Italic
import io.composeflow.SourceCodePro_Light
import io.composeflow.SourceCodePro_LightItalic
import io.composeflow.SourceCodePro_Medium
import io.composeflow.SourceCodePro_MediumItalic
import io.composeflow.SourceCodePro_Regular
import io.composeflow.SourceCodePro_SemiBold
import io.composeflow.SourceCodePro_SemiBoldItalic
import io.composeflow.Teko_Bold
import io.composeflow.Teko_Light
import io.composeflow.Teko_Medium
import io.composeflow.Teko_Regular
import io.composeflow.Teko_SemiBold
import io.composeflow.Ubuntu_Bold
import io.composeflow.Ubuntu_BoldItalic
import io.composeflow.Ubuntu_Italic
import io.composeflow.Ubuntu_Light
import io.composeflow.Ubuntu_LightItalic
import io.composeflow.Ubuntu_Medium
import io.composeflow.Ubuntu_MediumItalic
import io.composeflow.Ubuntu_Regular

val latoFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Lato-Italic.ttf",
        fontResource = Res.font.Lato_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Lato-LightItalic.ttf",
        fontResource = Res.font.Lato_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Lato-Thin.ttf",
        fontResource = Res.font.Lato_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Lato-Bold.ttf",
        fontResource = Res.font.Lato_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Lato-Black.ttf",
        fontResource = Res.font.Lato_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Lato-Regular.ttf",
        fontResource = Res.font.Lato_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Lato-BlackItalic.ttf",
        fontResource = Res.font.Lato_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Lato-BoldItalic.ttf",
        fontResource = Res.font.Lato_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Lato-Light.ttf",
        fontResource = Res.font.Lato_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Lato-ThinItalic.ttf",
        fontResource = Res.font.Lato_ThinItalic,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Italic
    )
)

val suseFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "SUSE-Light.ttf",
        fontResource = Res.font.SUSE_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-Thin.ttf",
        fontResource = Res.font.SUSE_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-SemiBold.ttf",
        fontResource = Res.font.SUSE_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-Bold.ttf",
        fontResource = Res.font.SUSE_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-ExtraBold.ttf",
        fontResource = Res.font.SUSE_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-Medium.ttf",
        fontResource = Res.font.SUSE_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-Regular.ttf",
        fontResource = Res.font.SUSE_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SUSE-ExtraLight.ttf",
        fontResource = Res.font.SUSE_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    )
)

val robotoFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Roboto-Medium.ttf",
        fontResource = Res.font.Roboto_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Light.ttf",
        fontResource = Res.font.Roboto_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Regular.ttf",
        fontResource = Res.font.Roboto_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Roboto-MediumItalic.ttf",
        fontResource = Res.font.Roboto_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-ThinItalic.ttf",
        fontResource = Res.font.Roboto_ThinItalic,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-BoldItalic.ttf",
        fontResource = Res.font.Roboto_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-LightItalic.ttf",
        fontResource = Res.font.Roboto_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Italic.ttf",
        fontResource = Res.font.Roboto_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-BlackItalic.ttf",
        fontResource = Res.font.Roboto_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Bold.ttf",
        fontResource = Res.font.Roboto_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Thin.ttf",
        fontResource = Res.font.Roboto_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Roboto-Black.ttf",
        fontResource = Res.font.Roboto_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    )
)

val ralewayFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Raleway-BoldItalic.ttf",
        fontResource = Res.font.Raleway_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-MediumItalic.ttf",
        fontResource = Res.font.Raleway_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-ThinItalic.ttf",
        fontResource = Res.font.Raleway_ThinItalic,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-ExtraLight.ttf",
        fontResource = Res.font.Raleway_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Medium.ttf",
        fontResource = Res.font.Raleway_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-SemiBold.ttf",
        fontResource = Res.font.Raleway_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-ExtraBold.ttf",
        fontResource = Res.font.Raleway_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-ExtraBoldItalic.ttf",
        fontResource = Res.font.Raleway_ExtraBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Regular.ttf",
        fontResource = Res.font.Raleway_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-ExtraLightItalic.ttf",
        fontResource = Res.font.Raleway_ExtraLightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-SemiBoldItalic.ttf",
        fontResource = Res.font.Raleway_SemiBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Light.ttf",
        fontResource = Res.font.Raleway_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Black.ttf",
        fontResource = Res.font.Raleway_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-BlackItalic.ttf",
        fontResource = Res.font.Raleway_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-LightItalic.ttf",
        fontResource = Res.font.Raleway_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Thin.ttf",
        fontResource = Res.font.Raleway_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Italic.ttf",
        fontResource = Res.font.Raleway_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Raleway-Bold.ttf",
        fontResource = Res.font.Raleway_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val bungeetintFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "BungeeTint-Regular.ttf",
        fontResource = Res.font.BungeeTint_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val montserratFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Montserrat-LightItalic.ttf",
        fontResource = Res.font.Montserrat_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Medium.ttf",
        fontResource = Res.font.Montserrat_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-BoldItalic.ttf",
        fontResource = Res.font.Montserrat_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Light.ttf",
        fontResource = Res.font.Montserrat_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-ThinItalic.ttf",
        fontResource = Res.font.Montserrat_ThinItalic,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-ExtraLight.ttf",
        fontResource = Res.font.Montserrat_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Thin.ttf",
        fontResource = Res.font.Montserrat_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Bold.ttf",
        fontResource = Res.font.Montserrat_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-MediumItalic.ttf",
        fontResource = Res.font.Montserrat_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-BlackItalic.ttf",
        fontResource = Res.font.Montserrat_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-SemiBold.ttf",
        fontResource = Res.font.Montserrat_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-ExtraLightItalic.ttf",
        fontResource = Res.font.Montserrat_ExtraLightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-ExtraBold.ttf",
        fontResource = Res.font.Montserrat_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Black.ttf",
        fontResource = Res.font.Montserrat_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Regular.ttf",
        fontResource = Res.font.Montserrat_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-Italic.ttf",
        fontResource = Res.font.Montserrat_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-SemiBoldItalic.ttf",
        fontResource = Res.font.Montserrat_SemiBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Montserrat-ExtraBoldItalic.ttf",
        fontResource = Res.font.Montserrat_ExtraBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    )
)

val sourcecodeproFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "SourceCodePro-LightItalic.ttf",
        fontResource = Res.font.SourceCodePro_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-SemiBold.ttf",
        fontResource = Res.font.SourceCodePro_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Medium.ttf",
        fontResource = Res.font.SourceCodePro_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-SemiBoldItalic.ttf",
        fontResource = Res.font.SourceCodePro_SemiBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-MediumItalic.ttf",
        fontResource = Res.font.SourceCodePro_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Light.ttf",
        fontResource = Res.font.SourceCodePro_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-BlackItalic.ttf",
        fontResource = Res.font.SourceCodePro_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-BoldItalic.ttf",
        fontResource = Res.font.SourceCodePro_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Black.ttf",
        fontResource = Res.font.SourceCodePro_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-ExtraLight.ttf",
        fontResource = Res.font.SourceCodePro_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Regular.ttf",
        fontResource = Res.font.SourceCodePro_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-ExtraLightItalic.ttf",
        fontResource = Res.font.SourceCodePro_ExtraLightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Italic.ttf",
        fontResource = Res.font.SourceCodePro_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-ExtraBoldItalic.ttf",
        fontResource = Res.font.SourceCodePro_ExtraBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-Bold.ttf",
        fontResource = Res.font.SourceCodePro_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "SourceCodePro-ExtraBold.ttf",
        fontResource = Res.font.SourceCodePro_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val ptserifFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "PTSerif-Italic.ttf",
        fontResource = Res.font.PTSerif_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PTSerif-BoldItalic.ttf",
        fontResource = Res.font.PTSerif_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PTSerif-Bold.ttf",
        fontResource = Res.font.PTSerif_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PTSerif-Regular.ttf",
        fontResource = Res.font.PTSerif_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val ubuntuFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Ubuntu-Medium.ttf",
        fontResource = Res.font.Ubuntu_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-LightItalic.ttf",
        fontResource = Res.font.Ubuntu_LightItalic,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-Regular.ttf",
        fontResource = Res.font.Ubuntu_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-Bold.ttf",
        fontResource = Res.font.Ubuntu_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-MediumItalic.ttf",
        fontResource = Res.font.Ubuntu_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-BoldItalic.ttf",
        fontResource = Res.font.Ubuntu_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-Italic.ttf",
        fontResource = Res.font.Ubuntu_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "Ubuntu-Light.ttf",
        fontResource = Res.font.Ubuntu_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    )
)

val crimsontextFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "CrimsonText-SemiBold.ttf",
        fontResource = Res.font.CrimsonText_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "CrimsonText-Bold.ttf",
        fontResource = Res.font.CrimsonText_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "CrimsonText-Regular.ttf",
        fontResource = Res.font.CrimsonText_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "CrimsonText-SemiBoldItalic.ttf",
        fontResource = Res.font.CrimsonText_SemiBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "CrimsonText-BoldItalic.ttf",
        fontResource = Res.font.CrimsonText_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "CrimsonText-Italic.ttf",
        fontResource = Res.font.CrimsonText_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    )
)

val oswaldFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Oswald-Bold.ttf",
        fontResource = Res.font.Oswald_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Oswald-SemiBold.ttf",
        fontResource = Res.font.Oswald_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Oswald-Medium.ttf",
        fontResource = Res.font.Oswald_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Oswald-Regular.ttf",
        fontResource = Res.font.Oswald_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Oswald-ExtraLight.ttf",
        fontResource = Res.font.Oswald_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Oswald-Light.ttf",
        fontResource = Res.font.Oswald_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    )
)

val ptsansFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "PTSans-Italic.ttf",
        fontResource = Res.font.PTSans_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PTSans-BoldItalic.ttf",
        fontResource = Res.font.PTSans_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PTSans-Regular.ttf",
        fontResource = Res.font.PTSans_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PTSans-Bold.ttf",
        fontResource = Res.font.PTSans_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val playfairdisplayFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-Bold.ttf",
        fontResource = Res.font.PlayfairDisplay_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-SemiBold.ttf",
        fontResource = Res.font.PlayfairDisplay_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-Medium.ttf",
        fontResource = Res.font.PlayfairDisplay_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-BoldItalic.ttf",
        fontResource = Res.font.PlayfairDisplay_BoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-ExtraBoldItalic.ttf",
        fontResource = Res.font.PlayfairDisplay_ExtraBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-Italic.ttf",
        fontResource = Res.font.PlayfairDisplay_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-Regular.ttf",
        fontResource = Res.font.PlayfairDisplay_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-Black.ttf",
        fontResource = Res.font.PlayfairDisplay_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-SemiBoldItalic.ttf",
        fontResource = Res.font.PlayfairDisplay_SemiBoldItalic,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-MediumItalic.ttf",
        fontResource = Res.font.PlayfairDisplay_MediumItalic,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-BlackItalic.ttf",
        fontResource = Res.font.PlayfairDisplay_BlackItalic,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "PlayfairDisplay-ExtraBold.ttf",
        fontResource = Res.font.PlayfairDisplay_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val caveatFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Caveat-Bold.ttf",
        fontResource = Res.font.Caveat_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Caveat-Regular.ttf",
        fontResource = Res.font.Caveat_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Caveat-Medium.ttf",
        fontResource = Res.font.Caveat_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Caveat-SemiBold.ttf",
        fontResource = Res.font.Caveat_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val handjetFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Handjet-Black.ttf",
        fontResource = Res.font.Handjet_Black,
        fontWeight = FontWeight.Black,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-ExtraBold.ttf",
        fontResource = Res.font.Handjet_ExtraBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-Regular.ttf",
        fontResource = Res.font.Handjet_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-Bold.ttf",
        fontResource = Res.font.Handjet_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-Thin.ttf",
        fontResource = Res.font.Handjet_Thin,
        fontWeight = FontWeight.Thin,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-SemiBold.ttf",
        fontResource = Res.font.Handjet_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-Medium.ttf",
        fontResource = Res.font.Handjet_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-Light.ttf",
        fontResource = Res.font.Handjet_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Handjet-ExtraLight.ttf",
        fontResource = Res.font.Handjet_ExtraLight,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    )
)

val tekoFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Teko-Bold.ttf",
        fontResource = Res.font.Teko_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Teko-Medium.ttf",
        fontResource = Res.font.Teko_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Teko-SemiBold.ttf",
        fontResource = Res.font.Teko_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Teko-Regular.ttf",
        fontResource = Res.font.Teko_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "Teko-Light.ttf",
        fontResource = Res.font.Teko_Light,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle.Normal
    )
)

val nerkooneFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "NerkoOne-Regular.ttf",
        fontResource = Res.font.NerkoOne_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val notoSansJpFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "NotoSansJP-Regular.ttf",
        fontResource = Res.font.NotoSansJP_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val dancingscriptFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "DancingScript-Bold.ttf",
        fontResource = Res.font.DancingScript_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "DancingScript-Medium.ttf",
        fontResource = Res.font.DancingScript_Medium,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "DancingScript-Regular.ttf",
        fontResource = Res.font.DancingScript_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "DancingScript-SemiBold.ttf",
        fontResource = Res.font.DancingScript_SemiBold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val librebaskervilleFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "LibreBaskerville-Italic.ttf",
        fontResource = Res.font.LibreBaskerville_Italic,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic
    ),
    FontFileWrapper(
        fontFileName = "LibreBaskerville-Regular.ttf",
        fontResource = Res.font.LibreBaskerville_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    ),
    FontFileWrapper(
        fontFileName = "LibreBaskerville-Bold.ttf",
        fontResource = Res.font.LibreBaskerville_Bold,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal
    )
)

val baskervvillescFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "BaskervvilleSC-Regular.ttf",
        fontResource = Res.font.BaskervvilleSC_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val sevillanaFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "Sevillana-Regular.ttf",
        fontResource = Res.font.Sevillana_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

val protestguerrillaFontFiles = listOf(
    FontFileWrapper(
        fontFileName = "ProtestGuerrilla-Regular.ttf",
        fontResource = Res.font.ProtestGuerrilla_Regular,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal
    )
)

