package io.composeflow.model.action

enum class ActionType(
    /**
     * The sort priority
     */
    val priority: Int,
) {
    OnClick(0),
    OnDoubleClick(1),
    OnLongClick(2),
    OnSubmit(0),
    OnChange(1),
    OnFocused(2),
    OnUnfocused(3),
    OnInitialLoad(0),
}
