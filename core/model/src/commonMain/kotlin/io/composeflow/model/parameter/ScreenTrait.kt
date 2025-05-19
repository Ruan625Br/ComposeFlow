package io.composeflow.model.parameter

import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import io.composeflow.custom.ComposeFlowIcons
import io.composeflow.custom.composeflowicons.Smartphone
import io.composeflow.kotlinpoet.GenerationContext
import io.composeflow.model.action.ActionType
import io.composeflow.model.palette.TraitCategory
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ScreenTrait")
data object ScreenTrait : ComposeTrait {

    override fun icon(): ImageVector = ComposeFlowIcons.Smartphone
    override fun iconText(): String = "Screen"
    override fun paletteCategories(): List<TraitCategory> = listOf(TraitCategory.Container)
    override fun visibleInPalette(): Boolean = false
    override fun isDroppable(): Boolean = false
    override fun isVisibilityConditional(): Boolean = false
    override fun actionTypes(): List<ActionType> = listOf(ActionType.OnInitialLoad)
    override fun generateCode(
        project: Project,
        node: ComposeNode,
        context: GenerationContext,
        dryRun: Boolean,
    ): CodeBlock = CodeBlock.of("")
}