package io.composeflow.model.project.appscreen.screen.composenode

import androidx.compose.runtime.mutableStateOf
import io.composeflow.model.parameter.ComponentTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.component.ComponentId
import io.composeflow.model.project.findComponentOrNull

sealed interface ComponentHandler {

    var self: ComposeNode

    /**
     * Create reference ComposeNode for a component.
     * Component can be used in multiple locations with the same definition.
     * Thus, reference can't be edited, but reflects the latest definition of the
     * original component.
     */
    fun createComponentWrapperNode(componentId: ComponentId): ComposeNode

    fun getComponentRootNode(project: Project, componentId: ComponentId): ComposeNode?
}

class ComponentHandlerImpl : ComponentHandler {
    override lateinit var self: ComposeNode

    override fun createComponentWrapperNode(componentId: ComponentId): ComposeNode {
        return ComposeNode(
            componentId = componentId,
            trait = mutableStateOf(ComponentTrait()),
        )
    }

    override fun getComponentRootNode(project: Project, componentId: ComponentId): ComposeNode? {
        val component = project.findComponentOrNull(componentId)
        val root = component?.componentRoot?.value?.copyExceptId()
        root?.setIsPartOfComponentRecursively(value = true)
        root?.updateChildParentRelationships()
        return root
    }
}
