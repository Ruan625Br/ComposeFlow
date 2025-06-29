package io.composeflow.model.project.appscreen.screen.composenode

import androidx.compose.runtime.mutableStateOf
import io.composeflow.model.parameter.IconTrait

/**
 * Interface that defines specific functionalities for TopAppBar
 */
interface BottomAppBarNode {
    var self: ComposeNode

    fun getBottomAppBarFab(): ComposeNode

    fun addBottomAppbarActionIcon()

    fun removeBottomAppBarActionIcon(i: Int)

    fun getBottomAppBarActionIcons(): List<ComposeNode>
}

/**
 * Implementation of BottomAppBar functionalities.
 *
 * It's built on the condition that:
 * - The first child is used for Fab
 * - From the second children are used for action icons
 */
class BottomAppBarNodeImpl : BottomAppBarNode {
    override lateinit var self: ComposeNode

    override fun getBottomAppBarFab(): ComposeNode = self.children[0]

    override fun addBottomAppbarActionIcon() {
        self.addChild(
            ComposeNode(
                trait = mutableStateOf(IconTrait.defaultTrait()),
                label = mutableStateOf("Action Icon ${self.children.size - 1}"),
            ),
        )
    }

    override fun removeBottomAppBarActionIcon(i: Int) {
        if (self.children.size > i) {
            self.children.removeAt(i + 1)
        }
    }

    override fun getBottomAppBarActionIcons(): List<ComposeNode> = self.children.drop(1)
}
