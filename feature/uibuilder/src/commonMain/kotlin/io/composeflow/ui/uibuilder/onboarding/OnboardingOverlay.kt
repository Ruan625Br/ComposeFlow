package io.composeflow.ui.uibuilder.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.composeflow.Res
import io.composeflow.onboarding_finish
import io.composeflow.onboarding_next
import io.composeflow.onboarding_previous
import io.composeflow.onboarding_skip
import org.jetbrains.compose.resources.stringResource

/**
 * Overlay that shows the onboarding tooltips and highlights
 */
@Composable
fun OnboardingOverlay(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val currentStep = state.currentStep

    Box(modifier = modifier.fillMaxSize()) {
        // Always render main content
        content()

        // Only render onboarding overlay when active
        if (state.isActive && currentStep != null) {
            // Backdrop with spotlight highlighting - rendered first
            OnboardingBackdrop(
                step = currentStep,
                targetBounds = state.targetBounds,
                modifier = Modifier.fillMaxSize(),
            )

            // Tooltip card - rendered on top of backdrop
            OnboardingTooltipCard(
                step = currentStep,
                stepNumber = state.currentStepIndex + 1,
                totalSteps = state.steps.size,
                onAction = onAction,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun OnboardingTooltipCard(
    step: OnboardingStep,
    stepNumber: Int,
    totalSteps: Int,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Card(
            modifier =
                Modifier
                    .width(400.dp)
                    .height(460.dp)
                    .padding(16.dp)
                    .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Step $stepNumber of $totalSteps",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                        )

                        if (step.showSkip) {
                            TextButton(
                                onClick = { onAction(OnboardingAction.Skip) },
                            ) {
                                Text(stringResource(Res.string.onboarding_skip))
                            }
                        }
                    }

                    Text(
                        text = stringResource(step.titleResource),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = stringResource(step.descriptionResource),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2,
                    )
                }

                // Bottom content section
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Progress indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        repeat(totalSteps) { index ->
                            Box(
                                modifier =
                                    Modifier
                                        .size(8.dp)
                                        .background(
                                            if (index <= stepNumber - 1) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            },
                                            shape = androidx.compose.foundation.shape.CircleShape,
                                        ),
                            )
                            if (index < totalSteps - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (stepNumber > 1) Arrangement.SpaceBetween else Arrangement.End,
                    ) {
                        if (stepNumber > 1) {
                            OutlinedButton(
                                onClick = { onAction(OnboardingAction.Previous) },
                            ) {
                                Text(stringResource(Res.string.onboarding_previous))
                            }
                        }

                        Button(
                            onClick = {
                                if (step.isLast) {
                                    onAction(OnboardingAction.Finish)
                                } else {
                                    onAction(OnboardingAction.Next)
                                }
                            },
                        ) {
                            Text(
                                if (step.isLast) {
                                    stringResource(Res.string.onboarding_finish)
                                } else {
                                    stringResource(
                                        Res.string.onboarding_next,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingBackdrop(
    step: OnboardingStep,
    targetBounds: Map<TargetArea, Rect>,
    modifier: Modifier = Modifier,
) {
    val overlayColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
    val layoutOffsets = LocalOnboardingLayoutOffsets.current
    val density = LocalDensity.current

    Canvas(
        modifier = modifier,
    ) {
        step.targetArea?.let { targetArea ->
            targetBounds[targetArea]?.let { bounds ->
                val padding = 8.dp.toPx()
                val titleBarHeight = with(density) { layoutOffsets.titleBarHeight.toPx() }
                val navigationRailWidth = with(density) { layoutOffsets.navigationRailWidth.toPx() }

                // Adjust bounds to account for title bar and navigation rail
                // Subtract offsets since bounds are in screen coordinates
                val adjustedBounds =
                    Rect(
                        bounds.left - navigationRailWidth,
                        bounds.top - titleBarHeight,
                        bounds.right - navigationRailWidth,
                        bounds.bottom - titleBarHeight,
                    )

                // Special adjustment for AI Assistant - move highlight area higher
                val finalBounds =
                    if (step.targetArea == TargetArea.AiAssistant) {
                        val offsetUp = 10.dp.toPx() // Move highlight 20dp higher
                        Rect(
                            adjustedBounds.left,
                            adjustedBounds.top - offsetUp,
                            adjustedBounds.right,
                            adjustedBounds.bottom - offsetUp,
                        )
                    } else {
                        adjustedBounds
                    }

                val highlightBounds =
                    Rect(
                        finalBounds.left - padding,
                        finalBounds.top - padding,
                        finalBounds.right + padding,
                        finalBounds.bottom + padding,
                    )

                // Draw overlay rectangles around the highlighted area (creating cutout effect)
                // Top rectangle
                if (highlightBounds.top > 0) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, highlightBounds.top),
                    )
                }

                // Bottom rectangle
                if (highlightBounds.bottom < size.height) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(0f, highlightBounds.bottom),
                        size = Size(size.width, size.height - highlightBounds.bottom),
                    )
                }

                // Left rectangle (middle section)
                if (highlightBounds.left > 0) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(0f, highlightBounds.top),
                        size = Size(highlightBounds.left, highlightBounds.height),
                    )
                }

                // Right rectangle (middle section)
                if (highlightBounds.right < size.width) {
                    drawRect(
                        color = overlayColor,
                        topLeft = Offset(highlightBounds.right, highlightBounds.top),
                        size = Size(size.width - highlightBounds.right, highlightBounds.height),
                    )
                }

                // Add a subtle border around the highlighted area
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.6f),
                    topLeft = Offset(highlightBounds.left, highlightBounds.top),
                    size = Size(highlightBounds.width, highlightBounds.height),
                    cornerRadius =
                        androidx.compose.ui.geometry
                            .CornerRadius(8.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx()),
                )
            } ?: run {
                // No target area - draw full overlay
                drawRect(color = overlayColor, size = size)
            }
        } ?: run {
            // No target area - draw full overlay
            drawRect(color = overlayColor, size = size)
        }
    }
}
