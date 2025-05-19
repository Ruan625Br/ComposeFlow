package io.composeflow.ui

import androidx.compose.ui.unit.IntSize

sealed interface FormFactor {
    val deviceSize: IntSize // Device size in device pixels

    data class Phone(
        override val deviceSize: IntSize = IntSize(416, 886),
    ) : FormFactor

    data class Tablet(
        override val deviceSize: IntSize = IntSize(810, 1080), // iPad
    ) : FormFactor

    data class Desktop(
        override val deviceSize: IntSize = IntSize(1440, 900), // Desktop
    ) : FormFactor
}