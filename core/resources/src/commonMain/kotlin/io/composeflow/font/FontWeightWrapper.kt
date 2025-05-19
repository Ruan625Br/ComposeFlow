package io.composeflow.font

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W100
import androidx.compose.ui.text.font.FontWeight.Companion.W200
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.font.FontWeight.Companion.W900

enum class FontWeightWrapper {
    Thin {
        override fun asFontWeight(): FontWeight = FontWeight.Thin
    },
    ExtraLight {
        override fun asFontWeight(): FontWeight = FontWeight.ExtraLight
    },
    Light {
        override fun asFontWeight(): FontWeight = FontWeight.Light
    },
    Normal {
        override fun asFontWeight(): FontWeight = FontWeight.Normal
    },
    Medium {
        override fun asFontWeight(): FontWeight = FontWeight.Medium
    },
    SemiBold {
        override fun asFontWeight(): FontWeight = FontWeight.SemiBold
    },
    Bold {
        override fun asFontWeight(): FontWeight = FontWeight.Bold
    },
    ExtraBold {
        override fun asFontWeight(): FontWeight = FontWeight.ExtraBold
    },
    Black {
        override fun asFontWeight(): FontWeight = FontWeight.Black
    },
    ;

    abstract fun asFontWeight(): FontWeight

    companion object {
        fun fromFontWeight(fontWeight: FontWeight?): FontWeightWrapper {
            return when (fontWeight) {
                W100 -> Thin
                W200 -> ExtraLight
                W300 -> Light
                W400 -> Normal
                W500 -> Medium
                W600 -> SemiBold
                W700 -> Bold
                W800 -> ExtraBold
                W900 -> Black
                else -> Normal
            }
        }
    }
}