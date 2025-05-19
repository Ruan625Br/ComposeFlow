package io.composeflow.model.state

sealed interface StateHolderType {

    data object Global : StateHolderType
    data class Screen(val screenId: String) : StateHolderType
    data class Component(val componentId: String) : StateHolderType
}
