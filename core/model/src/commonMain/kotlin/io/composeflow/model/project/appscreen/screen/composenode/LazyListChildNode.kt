package io.composeflow.model.project.appscreen.screen.composenode

import io.composeflow.model.parameter.lazylist.LazyListChildParams

/**
 * Interface that defines specific functionalities for a child (or its dependant) of a LazyList
 */
interface LazyListChildNode {

    var self: ComposeNode

    /**
     * Find a LazyList which depends on the data source whose ID is specified as [sourceId] or
     * return null if it's not found.
     */
    fun findDependentDynamicItemsHolderOrNull(target: ComposeNode, sourceId: String): ComposeNode?

    /**
     * Checks if any siblings of LazyList is already dependent of the data source specified as
     * [sourceId]
     */
    fun isAnySiblingDependentSource(sourceId: String): Boolean

    /**
     * Set the data source for the direct child of the LazyList.
     */
    fun setSourceForLazyListChild(lazyListChildParams: LazyListChildParams)
}

class LazyListChildNodeImpl : LazyListChildNode {

    override lateinit var self: ComposeNode

    override fun findDependentDynamicItemsHolderOrNull(
        target: ComposeNode,
        sourceId: String
    ): ComposeNode? {
        val dynamicItems = target.dynamicItems.value
        return if (dynamicItems != null &&
            target.trait.value.hasDynamicItems() &&
            target.id == sourceId
        ) {
            target
        } else {
            target.parentNode?.let { parent ->
                findDependentDynamicItemsHolderOrNull(parent, sourceId)
            }
        }
    }

    override fun isAnySiblingDependentSource(sourceId: String): Boolean {
        val lazyList = findDependentDynamicItemsHolderOrNull(self, sourceId)
        val directChildSameTree = lazyList?.children?.firstOrNull { it.hasChild(self) }
        // Check if any child already uses the same source
        return lazyList?.children
            ?.filter {
                it.id != directChildSameTree?.id
            }
            ?.any {
                val childParams = it.lazyListChildParams
                childParams.value is LazyListChildParams.DynamicItemsSource &&
                        childParams.value.getSourceId() == sourceId
            } == true
    }

    override fun setSourceForLazyListChild(lazyListChildParams: LazyListChildParams) {
        val lazyList =
            lazyListChildParams.getSourceId()
                ?.let { findDependentDynamicItemsHolderOrNull(self, it) }

        val self = this
        lazyList?.children?.forEach { child ->
            if (child.hasChild(self.self)) {
                child.lazyListChildParams.value = lazyListChildParams
            }
        }
    }

    private fun ComposeNode.hasChild(child: ComposeNode, includeSelf: Boolean = true): Boolean {
        var result = false
        if (includeSelf) {
            if (this == child) result = true
        }
        if (allChildren().any { it == child }) result = true
        return result
    }
}
