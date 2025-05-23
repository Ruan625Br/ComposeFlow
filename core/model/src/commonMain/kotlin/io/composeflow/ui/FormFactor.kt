package io.composeflow.ui

import androidx.compose.ui.unit.IntSize

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