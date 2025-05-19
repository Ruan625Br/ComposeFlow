package io.composeflow.model.property

/**
 * Represents where the property is available.
 *
 * For example:
 * - IntrinsicProperty, ValueFromState are available (can be read) at both of ComposeScreen and
 *   ViewModel
 * - ComposableParameterProperty, FunctionScopeParameterProperty are only available in ComposeScreen
 */
enum class PropertyAvailableAt {
    Both,
    ComposeScreen,
    ViewModel,
}