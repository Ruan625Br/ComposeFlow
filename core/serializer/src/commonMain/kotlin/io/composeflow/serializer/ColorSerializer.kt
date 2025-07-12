package io.composeflow.serializer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Color,
    ) {
        encoder.encodeString(value.asString())
    }

    override fun deserialize(decoder: Decoder): Color {
        val colorString = decoder.decodeString().removePrefix("0x").removePrefix("#")
        val colorLong = colorString.toULong(16)
        val colorInt =
            if (colorString.length <= 6) {
                // If the string is 6 characters or fewer, we assume it's RGB and prefix it with FF for the alpha
                (colorLong or 0xFF000000u).toInt()
            } else {
                // If the string is 8 characters, we assume it includes an alpha value
                colorLong.toInt()
            }
        return Color(colorInt)
    }
}

/**
 * Represent a Color as String in hexadecimal format.
 * Always represent the alpha in sRGB space.
 *
 * For example following values are represented as:
 * Color(0xFF8A123F) -> "0xFF8A123F"
 * Color(0x238A123F) -> "0x238A123F"
 * Color(0x8A123F) -> "0xFF0x8A123F"
 */
fun Color.asString(): String {
    val colorInt = toArgb()
    val colorString =
        if (colorInt ushr 24 == 0) {
            "0xFF${colorInt.toString(16).uppercase().padStart(6, '0')}"
        } else {
            "0x${colorInt.toUInt().toString(16).uppercase().padStart(8, '0')}"
        }
    return colorString
}

/**
 * Represent a Color as String in a format that conforms with Android xml.
 * Always represent the alpha in sRGB space.
 *
 * For example following values are represented as:
 * Color(0xFF8A123F) -> "#FF8A123F"
 * Color(0x238A123F) -> "#238A123F"
 * Color(0x8A123F) -> "#FF0x8A123F"
 */
fun Color.asAndroidXmlString(): String {
    val colorInt = toArgb()
    val colorString =
        if (colorInt ushr 24 == 0) {
            "#FF${colorInt.toString(16).uppercase().padStart(6, '0')}"
        } else {
            "#${colorInt.toUInt().toString(16).uppercase().padStart(8, '0')}"
        }
    return colorString
}
