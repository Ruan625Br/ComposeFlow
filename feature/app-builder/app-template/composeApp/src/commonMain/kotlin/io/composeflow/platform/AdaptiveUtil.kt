package io.composeflow.platform

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowWidthSizeClass

@Composable
expect fun computeAdaptiveWindowInfo(): WindowAdaptiveInfo

@Composable
fun isCurrentWindowWidthSizeClass(windowWidthSizeClass: WindowWidthSizeClass): Boolean {
    val windowAdaptiveInfo = computeAdaptiveWindowInfo()
    return windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == windowWidthSizeClass
}
