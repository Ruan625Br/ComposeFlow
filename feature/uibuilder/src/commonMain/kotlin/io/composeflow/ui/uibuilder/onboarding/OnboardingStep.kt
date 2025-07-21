package io.composeflow.ui.uibuilder.onboarding

import io.composeflow.Res
import io.composeflow.onboarding_ai_assistant_description
import io.composeflow.onboarding_ai_assistant_title
import io.composeflow.onboarding_canvas_description
import io.composeflow.onboarding_canvas_title
import io.composeflow.onboarding_getting_started_description
import io.composeflow.onboarding_getting_started_title
import io.composeflow.onboarding_inspector_description
import io.composeflow.onboarding_inspector_title
import io.composeflow.onboarding_palette_description
import io.composeflow.onboarding_palette_title
import io.composeflow.onboarding_welcome_description
import io.composeflow.onboarding_welcome_title
import org.jetbrains.compose.resources.StringResource

/**
 * Represents a single step in the onboarding flow
 */
data class OnboardingStep(
    val id: String,
    val titleResource: StringResource,
    val descriptionResource: StringResource,
    val targetArea: TargetArea? = null,
    val highlightArea: HighlightArea = HighlightArea.None,
    val placement: TooltipPlacement = TooltipPlacement.Center,
    val showSkip: Boolean = true,
    val isLast: Boolean = false,
)

/**
 * Defines different areas that can be targeted for highlighting
 */
sealed class TargetArea {
    data object Palette : TargetArea()

    data object Canvas : TargetArea()

    data object Inspector : TargetArea()

    data object AiAssistant : TargetArea()

    data object DevicePreview : TargetArea()

    data object ProjectStructure : TargetArea()

    data object Toolbar : TargetArea()
}

/**
 * Defines different highlight styles
 */
sealed class HighlightArea {
    data object None : HighlightArea()
}

/**
 * Defines where the tooltip should be placed relative to the highlight area
 */
enum class TooltipPlacement {
    Center,
    Top,
    Bottom,
    Left,
    Right,
}

/**
 * Predefined onboarding steps for the UI Builder
 */
object OnboardingSteps {
    val allSteps =
        listOf(
            OnboardingStep(
                id = "welcome",
                titleResource = Res.string.onboarding_welcome_title,
                descriptionResource = Res.string.onboarding_welcome_description,
                placement = TooltipPlacement.Center,
            ),
            OnboardingStep(
                id = "palette",
                titleResource = Res.string.onboarding_palette_title,
                descriptionResource = Res.string.onboarding_palette_description,
                targetArea = TargetArea.Palette,
                placement = TooltipPlacement.Right,
            ),
            OnboardingStep(
                id = "canvas",
                titleResource = Res.string.onboarding_canvas_title,
                descriptionResource = Res.string.onboarding_canvas_description,
                targetArea = TargetArea.Canvas,
                placement = TooltipPlacement.Top,
            ),
            OnboardingStep(
                id = "inspector",
                titleResource = Res.string.onboarding_inspector_title,
                descriptionResource = Res.string.onboarding_inspector_description,
                targetArea = TargetArea.Inspector,
                placement = TooltipPlacement.Left,
            ),
            OnboardingStep(
                id = "ai_assistant",
                titleResource = Res.string.onboarding_ai_assistant_title,
                descriptionResource = Res.string.onboarding_ai_assistant_description,
                targetArea = TargetArea.AiAssistant,
                placement = TooltipPlacement.Bottom,
            ),
            OnboardingStep(
                id = "getting_started",
                titleResource = Res.string.onboarding_getting_started_title,
                descriptionResource = Res.string.onboarding_getting_started_description,
                placement = TooltipPlacement.Center,
                showSkip = false,
                isLast = true,
            ),
        )
}
