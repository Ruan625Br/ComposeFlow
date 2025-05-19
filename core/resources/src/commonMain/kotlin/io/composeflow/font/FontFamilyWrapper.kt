package io.composeflow.font

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.FontResource

enum class FontFamilyWrapper(
    val displayName: String,
    val fontFileWrappers: List<FontFileWrapper>,
) {
    Roboto("Roboto", robotoFontFiles),
    NotoSansJP("Noto Sans JP", notoSansJpFontFiles),
    BaskervvilleSC("Baskervville SC", baskervvillescFontFiles),
    BungeeTint("Bungee Tint", bungeetintFontFiles),
    Caveat("Caveat", caveatFontFiles),
    CrimsonText("Crimson Text", crimsontextFontFiles),
    DancingScript("Dancing Script", dancingscriptFontFiles),
    Handjet("Handjet", handjetFontFiles),
    Lato("Lato", latoFontFiles),
    LibreBaskerville("Libre Baskerville", librebaskervilleFontFiles),
    Montserrat("Montserrat", montserratFontFiles),
    NerkoOne("Nerko One", nerkooneFontFiles),
    Oswald("Oswald", oswaldFontFiles),
    PlayfairDisplay("Playfair Display", playfairdisplayFontFiles),
    ProtestGuerilla("Protest Guerilla", protestguerrillaFontFiles),
    PTSans("PT Sans", ptsansFontFiles),
    PTSerif("PT Serif", ptserifFontFiles),
    Raleway("Raleway", ralewayFontFiles),
    Sevillana("Sevillana", sevillanaFontFiles),
    SourceCodePro("Source Code Pro", sourcecodeproFontFiles),
    SUSE("SUSE", suseFontFiles),
    Teko("Teko", tekoFontFiles),
    Ubuntu("Ubuntu", ubuntuFontFiles),
    ;

    @Composable
    fun asFontFamily(): FontFamily = FontFamily(fontFileWrappers.map { it.asFont() })

    fun fontFamilyName(): String = "${name}FontFamily"

    fun generateFontFamilyFunSpec(): FunSpec {
        val funSpecBuilder = FunSpec.builder(fontFamilyName())
            .addAnnotation(Composable::class)
            .returns(FontFamily::class)
            .addCode(
                CodeBlock.of(
                    "return %M(",
                    MemberName("androidx.compose.ui.text.font", "FontFamily")
                )
            )

        fontFileWrappers.forEach { fontFile ->
            val weightWrapper = FontWeightWrapper.fromFontWeight(fontFile.fontWeight)
            val styleAsString = when (fontFile.fontStyle) {
                FontStyle.Normal -> "Normal"
                FontStyle.Italic -> "Italic"
                else -> "Normal"
            }
            funSpecBuilder.addCode(
                CodeBlock.of(
                    "%M(%M.font.%M, weight = %M.${weightWrapper.name}, style = %M.${styleAsString}),",
                    MemberName("org.jetbrains.compose.resources", "Font"),
                    MemberName("io.composeflow", "Res"),
                    MemberName("io.composeflow", fontFile.fontResourceName),
                    MemberName("androidx.compose.ui.text.font", "FontWeight"),
                    MemberName("androidx.compose.ui.text.font", "FontStyle"),
                )
            )
        }
        funSpecBuilder.addCode(")")
        return funSpecBuilder.build()
    }
}

data class FontFileWrapper(
    val fontFileName: String,
    val fontResource: FontResource,
    val fontWeight: FontWeight = FontWeight.Normal,
    val fontStyle: FontStyle = FontStyle.Normal,
) {
    val fontResourceName = fontFileName
        .replace("-", "_")
        .replace(".ttf", "")

    @Composable
    fun asFont(): Font = Font(
        resource = fontResource,
        weight = fontWeight,
        style = fontStyle,
    )
}