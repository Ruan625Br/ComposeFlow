package io.composeflow.model.palette

import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.parameter.TabsTrait
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode

sealed class Constraint {
    /**
     * Constraint that prevents duplicate infinite scrolling layouts in the same direction.
     */
    data class InfiniteScroll(
        val orientation: Orientation,
    ) : Constraint() {
        override fun getErrorIfInvalid(parentNode: ComposeNode): String? {
            parentNode.findNodesUntilRoot(includeSelf = true).forEach { node ->
                val hasVerticalScrollModifier =
                    node.modifierList.any { it is ModifierWrapper.VerticalScroll }
                if (orientation == Orientation.Vertical &&
                    (
                        InfiniteScroll(Orientation.Vertical) in node.trait.value.defaultConstraints() ||
                            hasVerticalScrollModifier
                    )
                ) {
                    return DUPLICATE_VERTICAL_INFINITE_SCROLL
                }
                val hasHorizontalScrollModifier =
                    node.modifierList.any { it is ModifierWrapper.HorizontalScroll }
                if (orientation == Orientation.Horizontal &&
                    (
                        InfiniteScroll(Orientation.Horizontal) in node.trait.value.defaultConstraints() ||
                            hasHorizontalScrollModifier
                    )
                ) {
                    return DUPLICATE_HORIZONTAL_INFINITE_SCROLL
                }
            }
            return null
        }
    }

    data object NestedTabs : Constraint() {
        override fun getErrorIfInvalid(parentNode: ComposeNode): String? {
            parentNode.findNodesUntilRoot(includeSelf = true).forEach {
                if (it.trait.value is TabsTrait) {
                    return NESTED_TABS
                }
            }
            return null
        }
    }

    open fun getErrorIfInvalid(parentNode: ComposeNode): String? = null

    // TODO: Add these as string resources
    companion object {
        const val DUPLICATE_VERTICAL_INFINITE_SCROLL =
            "Nesting vertically infinite scrolling lists is not allowed"
        const val DUPLICATE_HORIZONTAL_INFINITE_SCROLL =
            "Nesting horizontally infinite scrolling lists is not allowed"
        const val CANT_DROP_NODE = "You can't drop a node to %s"
        const val NESTED_TABS = "Nesting tabs is not allowed"
        const val ONLY_SCREEN_IS_ALLOWED = "You can drop this only in a screen"
        const val SAME_NODE_EXISTS_IN_SCREEN = "%s is already included in the screen"
    }
}

enum class Orientation {
    Vertical,
    Horizontal,
}
