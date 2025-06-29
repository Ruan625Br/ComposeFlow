package io.composeflow.ui.adaptive

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.IntSize
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass

val LocalDeviceSizeDp = compositionLocalOf { IntSize(420, 800) }

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
fun computeWindowAdaptiveInfo(): WindowAdaptiveInfo {
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

fun computeNavigationSuiteType(
    widthDp: Float,
    heightDp: Float,
    showOnNavigation: Boolean = true,
): NavigationSuiteType {
    if (!showOnNavigation) return NavigationSuiteType.None

    val windowSizeClass = WindowSizeClass.compute(widthDp, heightDp)
    return when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            NavigationSuiteType.NavigationBar
        }

        WindowWidthSizeClass.MEDIUM -> {
            NavigationSuiteType.NavigationRail
        }

        WindowWidthSizeClass.EXPANDED -> {
            NavigationSuiteType.NavigationRail
        }

        else -> {
            NavigationSuiteType.NavigationBar
        }
    }
}
