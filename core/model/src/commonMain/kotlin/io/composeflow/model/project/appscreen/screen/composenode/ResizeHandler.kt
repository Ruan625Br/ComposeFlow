package io.composeflow.model.project.appscreen.screen.composenode

import androidx.compose.ui.unit.Density
import io.composeflow.model.modifier.ModifierWrapper

/**
 * Interface that defines methods to handle the resize functionalities for a dropped Composable
 */
interface ResizeHandler {
    var self: ComposeNode

    /**
     * Resize the height and apply the new Modifier as pending referred as
     * [ComposeNode.pendingModifier].
     *
     * A pending modifier is only visible in the canvas but it doesn't affect the generated code.
     */
    fun resizeHeightAsPending(
        deltaY: Float,
        currentHeight: Float,
        paddingHeight: Float,
        density: Density,
        parentHeight: Float? = null,
    )

    /**
     *
     * Resize the height and apply the new Modifier as pending referred as
     * [ComposeNode.pendingModifier].
     */
    fun resizeWidthAsPending(
        deltaX: Float,
        currentWidth: Float,
        paddingWidth: Float,
        density: Density,
        parentWidth: Float? = null,
    )

    /**
     * Commit the pending height modifier as part of the [ComposeNode.modifierList] so that it's reflected
     * in the generated code.
     */
    fun commitPendingHeightModifier()

    /**
     * Commit the pending width modifier as part of the [ComposeNode.modifierList] so that it's reflected
     * in the generated code.
     */
    fun commitPendingWidthModifier()

    /**
     * String representation of a modifier that decides the height of a Composable
     */
    fun heightDecider(): String

    /**
     * String representation of a modifier that decides the width of a Composable
     */
    fun widthDecider(): String
}

class ResizeHandlerImpl : ResizeHandler {
    override lateinit var self: ComposeNode

    override fun resizeHeightAsPending(
        deltaY: Float,
        currentHeight: Float,
        paddingHeight: Float,
        density: Density,
        parentHeight: Float?,
    ) {
        val heightDeciderModifier = self.modifiersIncludingPending()
            .filter { it.visible.value }
            .firstOrNull { it.heightDecider(self.parentNode?.trait?.value) != null }

        with(density) {
            when (heightDeciderModifier) {
                is ModifierWrapper.FillMaxHeight -> {
                    val pendingModifier = if (
                        parentHeight != null &&
                        deltaY + currentHeight + paddingHeight >= parentHeight
                    ) {
                        ModifierWrapper.FillMaxHeight()
                    } else {
                        ModifierWrapper.Height(
                            height = currentHeight.toDp() + deltaY.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.FillMaxSize -> {
                    val pendingModifier = if (
                        parentHeight != null &&
                        deltaY + currentHeight + paddingHeight >= parentHeight
                    ) {
                        ModifierWrapper.FillMaxHeight()
                    } else {
                        ModifierWrapper.Height(
                            height = currentHeight.toDp() + deltaY.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.Height -> {
                    val pendingModifier = if (
                        parentHeight != null &&
                        deltaY + currentHeight + paddingHeight >= parentHeight
                    ) {
                        ModifierWrapper.FillMaxHeight()
                    } else {
                        heightDeciderModifier.copy(
                            height = heightDeciderModifier.height + deltaY.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.Size -> {
                    val pendingModifier = if (
                        parentHeight != null &&
                        deltaY + currentHeight >= parentHeight
                    ) {
                        ModifierWrapper.FillMaxHeight()
                    } else {
                        heightDeciderModifier.copy(
                            height = heightDeciderModifier.height + deltaY.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                else -> {
                    self.pendingModifier.value = ModifierWrapper.Height(
                        currentHeight.toDp(),
                    )
                }
            }
        }
    }

    override fun resizeWidthAsPending(
        deltaX: Float,
        currentWidth: Float,
        paddingWidth: Float,
        density: Density,
        parentWidth: Float?,
    ) {
        val widthDeciderModifier = self.modifiersIncludingPending()
            .filter { it.visible.value }
            .firstOrNull { it.widthDecider(self.parentNode?.trait?.value) != null }
        with(density) {
            when (widthDeciderModifier) {
                is ModifierWrapper.FillMaxWidth -> {
                    val pendingModifier = if (
                        parentWidth != null &&
                        deltaX + currentWidth + paddingWidth >= parentWidth
                    ) {
                        ModifierWrapper.FillMaxWidth()
                    } else {
                        ModifierWrapper.Width(
                            width = currentWidth.toDp() + deltaX.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.FillMaxSize -> {
                    val pendingModifier = if (
                        parentWidth != null &&
                        deltaX + currentWidth + paddingWidth >= parentWidth
                    ) {
                        ModifierWrapper.FillMaxWidth()
                    } else {
                        ModifierWrapper.Width(
                            width = currentWidth.toDp() + deltaX.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.Width -> {
                    val pendingModifier = if (
                        parentWidth != null &&
                        deltaX + currentWidth + paddingWidth >= parentWidth
                    ) {
                        ModifierWrapper.FillMaxWidth()
                    } else {
                        widthDeciderModifier.copy(
                            width = widthDeciderModifier.width + deltaX.toDp(),
                        )
                    }
                    self.pendingModifier.value = pendingModifier
                }

                is ModifierWrapper.Size -> {
                    self.pendingModifier.value = widthDeciderModifier.copy(
                        width = widthDeciderModifier.width + deltaX.toDp(),
                    )
                }

                else -> {
                    self.pendingModifier.value = ModifierWrapper.Width(
                        currentWidth.toDp(),
                    )
                }
            }
        }
    }

    override fun commitPendingHeightModifier() {
        val heightDeciderIndex = self.modifierList
            .filter { it.visible.value }
            .indexOfFirst { it.heightDecider(self.parentNode?.trait?.value) != null }
        val heightDeciderModifier = self.modifierList
            .filter { it.visible.value }
            .firstOrNull { it.heightDecider(self.parentNode?.trait?.value) != null }

        self.pendingModifier.value?.let { pending ->
            when (heightDeciderIndex) {
                -1 -> {
                    self.modifierList.add(0, pending)
                }

                else -> {
                    when (heightDeciderModifier) {
                        is ModifierWrapper.FillMaxSize -> {
                            self.modifierList.add(0, pending)
                            self.pendingModifierCommittedIndex.value = 0
                        }

                        else -> {
                            self.modifierList[heightDeciderIndex] = pending
                            self.pendingModifierCommittedIndex.value = heightDeciderIndex
                        }
                    }
                }
            }
        }
        self.pendingModifier.value = null
    }

    override fun commitPendingWidthModifier() {
        val widthDeciderIndex = self.modifierList
            .filter { it.visible.value }
            .indexOfFirst { it.widthDecider(self.parentNode?.trait?.value) != null }
        val widthDeciderModifier = self.modifierList
            .filter { it.visible.value }
            .firstOrNull { it.widthDecider(self.parentNode?.trait?.value) != null }
        self.pendingModifier.value?.let { pending ->
            when (widthDeciderIndex) {
                -1 -> {
                    self.modifierList.add(0, pending)
                }

                else -> {
                    when (widthDeciderModifier) {
                        is ModifierWrapper.FillMaxSize -> {
                            self.modifierList.add(0, pending)
                            self.pendingModifierCommittedIndex.value = 0
                        }

                        else -> {
                            self.modifierList[widthDeciderIndex] = pending
                            self.pendingModifierCommittedIndex.value = widthDeciderIndex
                        }
                    }
                }
            }
        }
        self.pendingModifier.value = null
    }

    override fun heightDecider(): String {
        val modifiers = self.modifiersIncludingPending()
        return modifiers
            .firstOrNull {
                // First check the Weight since weights is prioritized regardless of the order of modifiers
                it is ModifierWrapper.Weight
            }?.heightDecider(self.parentNode?.trait?.value) ?: modifiers
            .filter { it.visible.value }
            .firstOrNull {
                it.heightDecider(self.parentNode?.trait?.value) != null
            }
            ?.heightDecider(self.parentNode?.trait?.value)
        ?: "Unspecified"
    }

    override fun widthDecider(): String {
        val modifiers = self.modifiersIncludingPending()
        return modifiers.firstOrNull {
            // First check the Weight since weights is prioritized regardless of the order of modifiers
            it is ModifierWrapper.Weight
        }?.widthDecider(self.parentNode?.trait?.value) ?: modifiers
            .filter { it.visible.value }
            .firstOrNull {
                it.widthDecider(self.parentNode?.trait?.value) != null
            }
            ?.widthDecider(self.parentNode?.trait?.value)
        ?: "Unspecified"
    }
}
