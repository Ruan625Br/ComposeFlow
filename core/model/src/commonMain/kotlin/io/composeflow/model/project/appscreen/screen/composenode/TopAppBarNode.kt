package io.composeflow.model.project.appscreen.screen.composenode

import androidx.compose.runtime.mutableStateOf
import io.composeflow.model.parameter.IconTrait

/**
 * Interface that defines specific functionalities for TopAppBar
 */
interface TopAppBarNode {

    var self: ComposeNode
    fun getTopAppBarNavigationIcon(): ComposeNode?
    fun addTopAppBarActionIcon()
    fun removeTopAppBarActionIcon(i: Int)
    fun getTopAppBarActionIcons(): List<ComposeNode>
}

/**
 * Implementation of TopAppBar specific functionalities.
 *
 * It's built on the condition that:
 * - The first child is used for navigation icon
 * - From the second children are used for action icons
 */
class TopAppBarNodeImpl : TopAppBarNode {
    override lateinit var self: ComposeNode

    override fun getTopAppBarNavigationIcon(): ComposeNode? = if (self.children.size > 0) {
        self.children[0]
    } else {
        null
    }

    override fun addTopAppBarActionIcon() {
        self.addChild(
            ComposeNode(
                trait = mutableStateOf(IconTrait.defaultTrait()),
                label = mutableStateOf("Action Icon ${self.children.size - 1}"),
            ),
        )
    }

    override fun removeTopAppBarActionIcon(i: Int) {
        if (self.children.size > i) {
            self.children.removeAt(i + 1)
        }
    }

    override fun getTopAppBarActionIcons(): List<ComposeNode> = self.children.drop(1)
}
