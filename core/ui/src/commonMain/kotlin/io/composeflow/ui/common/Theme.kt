package io.composeflow.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val defaultLightScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

val defaultDarkScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

val ColorScheme.warning: Color
    @Composable
    get() = if (isSystemInDarkTheme()) warningDark else warningLight

/**
 * Indicates if the dark theme is used in the ComposeFlow setting.
 * May be different from the one provided by ProvideAppThemeTokens because the latter is used
 * for the edited app in the canvas.
 */
val LocalUseDarkTheme =
    staticCompositionLocalOf<Boolean> {
        throw IllegalStateException("No UseDarkTheme provided")
    }

@Composable
fun ComposeFlowTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors =
        if (!useDarkTheme) {
            defaultLightScheme
        } else {
            defaultDarkScheme
        }

    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 32.dp) {
        CompositionLocalProvider(LocalUseDarkTheme provides useDarkTheme) {
            MaterialTheme(
                colorScheme = colors,
                content = content,
            )
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LocalAppThemeTokens.current.colorScheme,
        typography = LocalAppThemeTokens.current.typography,
        shapes = LocalAppThemeTokens.current.shapes,
        content = content,
    )
}

private val LocalAppThemeTokens =
    staticCompositionLocalOf<AppThemeTokens> {
        throw IllegalStateException("No AppThemeTokens provided")
    }

@Composable
fun ProvideAppThemeTokens(
    isDarkTheme: Boolean,
    lightScheme: ColorScheme = defaultLightScheme,
    darkScheme: ColorScheme = defaultDarkScheme,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit,
) {
    val appThemeTokens =
        AppThemeTokens(
            colorScheme = if (isDarkTheme) darkScheme else lightScheme,
            typography = typography,
            shapes = MaterialTheme.shapes,
        )
    CompositionLocalProvider(LocalAppThemeTokens provides appThemeTokens) {
        content()
    }
}

private data class AppThemeTokens(
    val colorScheme: ColorScheme,
    val typography: Typography,
    val shapes: Shapes,
)
