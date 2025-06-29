package io.composeflow.platform

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.IntSize
import androidx.window.core.layout.WindowSizeClass

// Holds the device size in dp.
// When running the JS target, the device size should be the app's device size in the canvas
// instead of using the size of the entire screen.
// Thus only in the JS target, set the device size in the canvas in this CompositionLocal context.
val LocalDeviceSizeDp = compositionLocalOf<IntSize> { error("No Device size is provided") }

@Composable
fun ProvideDeviceSizeDp(
    deviceSizeDp: IntSize,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDeviceSizeDp providesDefault deviceSizeDp) {
        content()
    }
}

@Composable
actual fun computeAdaptiveWindowInfo(): WindowAdaptiveInfo {
    val localDeviceSize = LocalDeviceSizeDp.current
    val adaptiveInfo = currentWindowAdaptiveInfo()
    // Compute the WindowSizeClass not from the entire window, but from the size of the device area.
    val computedWindowInfo =
        WindowSizeClass.compute(
            dpWidth = localDeviceSize.width.toFloat(),
            dpHeight = localDeviceSize.height.toFloat(),
        )
    return WindowAdaptiveInfo(computedWindowInfo, adaptiveInfo.windowPosture)
}
