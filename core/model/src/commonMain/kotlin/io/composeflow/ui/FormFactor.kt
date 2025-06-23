package io.composeflow.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import kotlin.math.min

sealed interface FormFactor {
    val deviceSize: IntSize // Device size in device pixels
    val vesselSize: Int // Device frame width in device pixels

    data class Phone(
        override val deviceSize: IntSize = IntSize(416, 886),
        override val vesselSize: Int = 24,
    ) : FormFactor

    data class Tablet(
        override val deviceSize: IntSize = IntSize(810, 1080), // iPad
        override val vesselSize: Int = 44,
    ) : FormFactor

    data class Desktop(
        override val deviceSize: IntSize = IntSize(1440, 900), // Desktop
        override val vesselSize: Int = 0,
    ) : FormFactor
}

/**
 * Calculate the best suited scale that fits the given screen size
 */
fun calculateScale(
    formFactor: FormFactor,
    screenSize: Size,
    topAreaMargin: Int = 80,
    bottomAreaMargin: Int = 40
): Float {
    var widthScale = 1f
    val minimumScale = 0.3f
    while (formFactor.deviceSize.width * widthScale > screenSize.width) {
        widthScale -= 0.1f
        if (widthScale <= minimumScale) {
            break
        }
    }
    var heightScale = 1f
    while (formFactor.deviceSize.height * heightScale > screenSize.height - topAreaMargin - bottomAreaMargin) {
        heightScale -= 0.1f
        if (heightScale <= minimumScale) {
            break
        }
    }
    return min(widthScale, heightScale)
}