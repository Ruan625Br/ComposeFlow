package io.composeflow.model.palette

import androidx.compose.ui.graphics.vector.ImageVector
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import org.jetbrains.compose.resources.StringResource

interface PaletteDraggable {
    /**
     * Create a default ComposeNode used when this node is dragged on the canvas from the palette
     */
    fun defaultComposeNode(project: Project): ComposeNode?

    /**
     * Icon, which is displayed when the node is being dragged.
     */
    fun icon(): ImageVector

    /**
     * Text, which is displayed when the node is being dragged.
     */
    fun iconText(): String

    fun paletteCategories(): List<TraitCategory>

    /**
     * Tooltip resource for this palette item explaining its purpose and usage
     */
    fun tooltipResource(): StringResource
}
