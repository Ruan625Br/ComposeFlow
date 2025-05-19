package io.composeflow.model.palette

import io.composeflow.Res
import io.composeflow.palette_category_screen_elements_tooltip
import org.jetbrains.compose.resources.StringResource

enum class TraitCategory(
    val tooltipResource: StringResource? = null,
) {
    /**
     * Most frequently used Composables.
     */
    Common {
        override fun displayName(): String = "Commonly used"
    },

    /**
     * Basic set of Composables.
     */
    Basic,

    /**
     * Able to have children. Used for creating layouts such as Row or Column
     */
    Layout,

    /**
     * Able to have children. Doesn't have to be used for creating layouts.
     * For example, TabContent is able to have children, but it's not used for layouts.
     */
    Container,

    /**
     * Similar to [Container], but this represents the Composables are able to wrap
     * other Composables.
     * E.g. They are visible in the context menu in the UI builder to wrap the target
     * Composable
     */
    WrapContainer,

    /**
     * Specific to content to tab. Tab content has unique aspect in that it may be possible to be
     * hidden based on the state of the selected tab.
     */
    TabContent,

    /**
     * Specific to content to included in screen up to 1.
     * For example, FAB, TopAppBar, Drawer, BottomAppBar, etc.
     */
    ScreenOnly(
        tooltipResource = Res.string.palette_category_screen_elements_tooltip
    ) {
        override fun displayName() = "Screen elements"
    },

    /**
     * Authentication related Composables, such as "Sign in with Google" button.
     */
    Auth,

    NavigationItem,
    ;

    open fun displayName(): String = name
}
