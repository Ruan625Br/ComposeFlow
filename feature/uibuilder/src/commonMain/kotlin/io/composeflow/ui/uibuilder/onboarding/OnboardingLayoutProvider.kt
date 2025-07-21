package io.composeflow.ui.uibuilder.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Layout offset values for onboarding calculations
 */
data class OnboardingLayoutOffsets(
    val titleBarHeight: Dp = 30.dp, // Typical Jewel TitleBar height
    val navigationRailWidth: Dp = 40.dp, // From ProjectEditorView
    val statusBarHeight: Dp = 24.dp, // Estimated status bar height
)

/**
 * CompositionLocal for providing layout offsets to onboarding components
 */
val LocalOnboardingLayoutOffsets = staticCompositionLocalOf { OnboardingLayoutOffsets() }

/**
 * Provider composable to set onboarding layout offsets
 */
@Composable
fun ProvideOnboardingLayoutOffsets(
    offsets: OnboardingLayoutOffsets = OnboardingLayoutOffsets(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalOnboardingLayoutOffsets provides offsets) {
        content()
    }
}
