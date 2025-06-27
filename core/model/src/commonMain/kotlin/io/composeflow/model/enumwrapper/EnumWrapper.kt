package io.composeflow.model.enumwrapper

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import io.composeflow.kotlinpoet.CodeConvertible
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.serializer.FallbackEnumSerializer
import io.composeflow.ui.propertyeditor.DropdownTextDisplayable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.enums.EnumEntries

/**
 * Sealed interface to wrap enum classes to make it possible to list the known enum classes
 * so that Kotlinx serialization is able to
 * serialize/deserialize the enum classes without creating custom serializer.
 */
@Serializable
sealed interface EnumWrapper : CodeConvertible {

    fun enumValue(): Enum<*>

    fun <E : Enum<E>> entries(): EnumEntries<E>
}

@SerialName("TextDecorationWrapper")
@Serializable(TextDecorationWrapper.TextDecorationWrapperSerializer::class)
enum class TextDecorationWrapper(val textDecoration: TextDecoration) : EnumWrapper {
    None(TextDecoration.None),
    Underline(TextDecoration.Underline),
    LineThrough(TextDecoration.LineThrough),
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.$name", MemberName("androidx.compose.ui.text.style", "TextDecoration"))

    object TextDecorationWrapperSerializer :
        FallbackEnumSerializer<TextDecorationWrapper>(TextDecorationWrapper::class)
}

@SerialName("TextStyleWrapper")
@Serializable(TextStyleWrapper.TextStyleWrapperSerializer::class)
enum class TextStyleWrapper(val styleName: String, val displayName: String) : EnumWrapper,
    DropdownTextDisplayable {
    DisplayLarge("displayLarge", "Display Large"),
    DisplayMedium("displayMedium", "Display Medium"),
    DisplaySmall("displaySmall", "Display Small"),
    HeadlineLarge("headlineLarge", "Headline Large"),
    HeadlineMedium("headlineMedium", "Headline Medium"),
    HeadlineSmall("headlineSmall", "Headline Small"),
    TitleLarge("titleLarge", "Title Large"),
    TitleMedium("titleMedium", "Title Medium"),
    TitleSmall("titleSmall", "Title Small"),
    BodyLarge("bodyLarge", "Body Large"),
    BodyMedium("bodyMedium", "Body Medium"),
    BodySmall("bodySmall", "Body Small"),
    LabelLarge("labelLarge", "Label Large"),
    LabelMedium("labelMedium", "Label Medium"),
    LabelSmall("labelSmall", "Label Small"),
    ;

    @Composable
    override fun asDropdownText(): AnnotatedString {
        // TODO: Technically, if font styles are overridden, the size and weight may not be correct
        val secondaryColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
        return when (this) {
            DisplayLarge -> buildAnnotatedString {
                append("Display Large ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.displayLarge.fontSize}, weight:${MaterialTheme.typography.displayLarge.fontWeight?.weight}")
                }
            }

            DisplayMedium -> buildAnnotatedString {
                append("Display Medium ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.displayMedium.fontSize}, weight:${MaterialTheme.typography.displayMedium.fontWeight?.weight}")
                }
            }

            DisplaySmall -> buildAnnotatedString {
                append("Display Small ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.displaySmall.fontSize}, weight:${MaterialTheme.typography.displaySmall.fontWeight?.weight}")
                }
            }

            HeadlineLarge -> buildAnnotatedString {
                append("Headline Large ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.headlineLarge.fontSize}, weight:${MaterialTheme.typography.headlineLarge.fontWeight?.weight}")
                }
            }

            HeadlineMedium -> buildAnnotatedString {
                append("Headline Medium ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.headlineMedium.fontSize}, weight:${MaterialTheme.typography.headlineMedium.fontWeight?.weight}")
                }
            }

            HeadlineSmall -> buildAnnotatedString {
                append("Headline Small ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.headlineSmall.fontSize}, weight:${MaterialTheme.typography.headlineSmall.fontWeight?.weight}")
                }
            }

            TitleLarge -> buildAnnotatedString {
                append("Title Large ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.titleLarge.fontSize}, weight:${MaterialTheme.typography.titleLarge.fontWeight?.weight}")
                }
            }

            TitleMedium -> buildAnnotatedString {
                append("Title Medium ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.titleMedium.fontSize}, weight:${MaterialTheme.typography.titleMedium.fontWeight?.weight}")
                }
            }

            TitleSmall -> buildAnnotatedString {
                append("Title Small ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.titleSmall.fontSize}, weight:${MaterialTheme.typography.titleSmall.fontWeight?.weight}")
                }
            }

            BodyLarge -> buildAnnotatedString {
                append("Body Large ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.bodyLarge.fontSize}, weight:${MaterialTheme.typography.bodyLarge.fontWeight?.weight}")
                }
            }

            BodyMedium -> buildAnnotatedString {
                append("Body Medium ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.bodyMedium.fontSize}, weight:${MaterialTheme.typography.bodyMedium.fontWeight?.weight}")
                }
            }

            BodySmall -> buildAnnotatedString {
                append("Body Small ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.bodySmall.fontSize}, weight:${MaterialTheme.typography.bodySmall.fontWeight?.weight}")
                }
            }

            LabelLarge -> buildAnnotatedString {
                append("Label Large ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.labelLarge.fontSize}, weight:${MaterialTheme.typography.labelLarge.fontWeight?.weight}")
                }
            }

            LabelMedium -> buildAnnotatedString {
                append("Label Medium ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.labelMedium.fontSize}, weight:${MaterialTheme.typography.labelMedium.fontWeight?.weight}")
                }
            }

            LabelSmall -> buildAnnotatedString {
                append("Label Small ")
                withStyle(SpanStyle(color = secondaryColor)) {
                    append("${MaterialTheme.typography.labelSmall.fontSize}, weight:${MaterialTheme.typography.labelSmall.fontWeight?.weight}")
                }
            }
        }
    }

    @Composable
    fun getStyle(): TextStyle =
        when (this) {
            DisplayLarge -> MaterialTheme.typography.displayLarge
            DisplayMedium -> MaterialTheme.typography.displayMedium
            DisplaySmall -> MaterialTheme.typography.displaySmall
            HeadlineLarge -> MaterialTheme.typography.headlineLarge
            HeadlineMedium -> MaterialTheme.typography.headlineMedium
            HeadlineSmall -> MaterialTheme.typography.headlineSmall
            TitleLarge -> MaterialTheme.typography.titleLarge
            TitleMedium -> MaterialTheme.typography.titleMedium
            TitleSmall -> MaterialTheme.typography.titleSmall
            BodyLarge -> MaterialTheme.typography.bodyLarge
            BodyMedium -> MaterialTheme.typography.bodyMedium
            BodySmall -> MaterialTheme.typography.bodySmall
            LabelLarge -> MaterialTheme.typography.labelLarge
            LabelMedium -> MaterialTheme.typography.labelMedium
            LabelSmall -> MaterialTheme.typography.labelSmall
        }

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.typography.$styleName", MemberHolder.Material3.MaterialTheme)

    object TextStyleWrapperSerializer :
        FallbackEnumSerializer<TextStyleWrapper>(TextStyleWrapper::class)
}

@SerialName("FontStyleWrapper")
@Serializable(FontStyleWrapper.FontStyleWrapperSerializer::class)
enum class FontStyleWrapper(val fontStyle: FontStyle) : EnumWrapper {
    Normal(FontStyle.Normal),
    Italic(FontStyle.Italic),
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.$name", MemberName("androidx.compose.ui.text.font", "FontStyle"))

    object FontStyleWrapperSerializer :
        FallbackEnumSerializer<FontStyleWrapper>(FontStyleWrapper::class)
}

@SerialName("TextAlignWrapper")
@Serializable(TextAlignWrapper.TextAlignWrapperSerializer::class)
enum class TextAlignWrapper(val textAlign: TextAlign) : EnumWrapper {
    Left(TextAlign.Left),
    Right(TextAlign.Right),
    Center(TextAlign.Center),
    Justify(TextAlign.Justify),
    Start(TextAlign.Start),
    End(TextAlign.End),
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.$name", MemberName("androidx.compose.ui.text.style", "TextAlign"))

    object TextAlignWrapperSerializer :
        FallbackEnumSerializer<TextAlignWrapper>(TextAlignWrapper::class)
}

@SerialName("TextOverflowWrapper")
@Serializable(TextOverflowWrapper.TextOverflowWrapperSerializer::class)
enum class TextOverflowWrapper(val textOverflow: TextOverflow) : EnumWrapper {
    Clip(TextOverflow.Clip),
    Ellipsis(TextOverflow.Ellipsis),
    Visible(TextOverflow.Visible),
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.$name", MemberName("androidx.compose.ui.text.style", "TextOverflow"))

    object TextOverflowWrapperSerializer :
        FallbackEnumSerializer<TextOverflowWrapper>(TextOverflowWrapper::class)
}

@SerialName("ContentScaleWrapper")
@Serializable(ContentScaleWrapper.ContentScaleWrapperSerializer::class)
enum class ContentScaleWrapper(val contentScale: ContentScale) : EnumWrapper {
    Crop(ContentScale.Crop),
    Fit(ContentScale.Fit),
    FillHeight(ContentScale.FillHeight),
    FillWidth(ContentScale.FillWidth),
    Inside(ContentScale.Inside),
    FillBounds(ContentScale.FillBounds),
    None(ContentScale.None),
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.$name", MemberName("androidx.compose.ui.layout", "ContentScale"))

    object ContentScaleWrapperSerializer :
        FallbackEnumSerializer<ContentScaleWrapper>(ContentScaleWrapper::class)
}

@SerialName("TextFieldColorsWrapper")
@Serializable(TextFieldColorsWrapper.TextFieldColorsWrapperSerializer::class)
enum class TextFieldColorsWrapper : EnumWrapper {
    Default,
    Outlined,
    ;

    override fun enumValue(): Enum<*> = this

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return entries as EnumEntries<E>
    }

    override fun asCodeBlock(): CodeBlock =
        CodeBlock.of("%M.colors()", toMemberName())

    fun toMemberName(): MemberName {
        return when (this) {
            Default -> MemberName("androidx.compose.material3", "TextFieldDefaults")
            Outlined -> MemberName("androidx.compose.material3", "OutlinedTextFieldDefaults")
        }
    }

    object TextFieldColorsWrapperSerializer :
        FallbackEnumSerializer<TextFieldColorsWrapper>(TextFieldColorsWrapper::class)
}

@SerialName("NodeVisibility")
@Serializable(NodeVisibility.NodeVisibilitySerializer::class)
enum class NodeVisibility : EnumWrapper {
    AlwaysVisible,
    Conditional,
    ;

    override fun <E : Enum<E>> entries(): EnumEntries<E> {
        @Suppress("UNCHECKED_CAST")
        return NodeVisibility.entries as EnumEntries<E>
    }

    override fun enumValue(): Enum<*> = this
    override fun asCodeBlock(): CodeBlock = CodeBlock.of("")

    object NodeVisibilitySerializer : FallbackEnumSerializer<NodeVisibility>(NodeVisibility::class)
}
