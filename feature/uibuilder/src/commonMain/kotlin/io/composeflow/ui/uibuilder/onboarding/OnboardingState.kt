package io.composeflow.ui.uibuilder.onboarding

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Rect

/**
 * State holder for the onboarding flow
 */
@Stable
data class OnboardingState(
    val isActive: Boolean = false,
    val currentStepIndex: Int = 0,
    val steps: List<OnboardingStep> = OnboardingSteps.allSteps,
    val targetBounds: Map<TargetArea, Rect> = emptyMap(),
    val shouldShowOnFirstLaunch: Boolean = true,
) {
    val currentStep: OnboardingStep?
        get() = if (currentStepIndex < steps.size) steps[currentStepIndex] else null
}

/**
 * Actions for controlling the onboarding flow
 */
sealed class OnboardingAction {
    data object Start : OnboardingAction()

    data object Next : OnboardingAction()

    data object Previous : OnboardingAction()

    data object Skip : OnboardingAction()

    data object Finish : OnboardingAction()

    data class UpdateTargetBounds(
        val targetArea: TargetArea,
        val bounds: Rect,
    ) : OnboardingAction()

    data class JumpToStep(
        val stepIndex: Int,
    ) : OnboardingAction()
}
