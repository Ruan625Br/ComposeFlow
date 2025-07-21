package io.composeflow.ui.uibuilder.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import io.composeflow.model.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Manages onboarding state and provides utilities for tracking UI elements
 */
class OnboardingManager(
    private val settingsRepository: SettingsRepository,
) {
    private var _state by mutableStateOf(OnboardingState())
    val state: OnboardingState get() = _state

    val hasShownOnboarding: Flow<Boolean> = settingsRepository.hasShownOnboarding

    fun initializeOnboarding(hasShownOnboarding: Boolean) {
        // Auto-start onboarding if it hasn't been shown before
        if (!hasShownOnboarding) {
            startOnboarding()
        }
    }

    fun handleAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.Start -> {
                _state =
                    _state.copy(
                        isActive = true,
                        currentStepIndex = 0,
                    )
            }

            is OnboardingAction.Next -> {
                val nextIndex = _state.currentStepIndex + 1
                if (nextIndex < _state.steps.size) {
                    _state = _state.copy(currentStepIndex = nextIndex)
                } else {
                    finishOnboarding()
                }
            }

            is OnboardingAction.Previous -> {
                val prevIndex = (_state.currentStepIndex - 1).coerceAtLeast(0)
                _state = _state.copy(currentStepIndex = prevIndex)
            }

            is OnboardingAction.Skip, is OnboardingAction.Finish -> {
                finishOnboarding()
            }

            is OnboardingAction.UpdateTargetBounds -> {
                _state =
                    _state.copy(
                        targetBounds = _state.targetBounds + (action.targetArea to action.bounds),
                    )
            }

            is OnboardingAction.JumpToStep -> {
                if (action.stepIndex in 0 until _state.steps.size) {
                    _state = _state.copy(currentStepIndex = action.stepIndex)
                }
            }
        }
    }

    private fun finishOnboarding() {
        _state = _state.copy(isActive = false)
        settingsRepository.saveOnboardingCompleted(true)
    }

    private fun startOnboarding() {
        handleAction(OnboardingAction.Start)
    }
}

/**
 * Modifier that tracks the bounds of a UI element for onboarding highlighting
 */
@Composable
fun Modifier.onboardingTarget(
    targetArea: TargetArea,
    onboardingManager: OnboardingManager,
): Modifier =
    this.onGloballyPositioned { layoutCoordinates ->
        val bounds = layoutCoordinates.boundsInRoot()
        onboardingManager.handleAction(
            OnboardingAction.UpdateTargetBounds(targetArea, bounds),
        )
    }

/**
 * Hook for managing onboarding state in composables
 */
@Composable
fun rememberOnboardingManager(settingsRepository: SettingsRepository): OnboardingManager {
    val manager = remember { OnboardingManager(settingsRepository) }
    val hasShownOnboarding by manager.hasShownOnboarding.collectAsState(initial = true) // Default to true to prevent auto-start

    LaunchedEffect(hasShownOnboarding) {
        // Only initialize when we have the actual value from settings
        manager.initializeOnboarding(hasShownOnboarding)
    }

    return manager
}
