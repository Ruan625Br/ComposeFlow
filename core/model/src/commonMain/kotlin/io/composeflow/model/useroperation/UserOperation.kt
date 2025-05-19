package io.composeflow.model.useroperation

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import io.composeflow.model.action.ActionNode
import io.composeflow.model.action.ActionType
import io.composeflow.model.apieditor.ApiDefinition
import io.composeflow.model.color.ColorSchemeWrapper
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.parameter.ComposeTrait
import io.composeflow.model.parameter.lazylist.LazyListChildParams
import io.composeflow.model.project.CanvasEditable
import io.composeflow.model.project.ParameterWrapper
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.model.project.appscreen.screen.composenode.VisibilityParams
import io.composeflow.model.project.component.Component
import io.composeflow.model.property.AssignableProperty
import io.composeflow.template.ScreenTemplatePair

sealed interface UserOperation {

    data class ComposableDropped(val node: ComposeNode) : UserOperation
    data class ComposableMoved(val node: ComposeNode) : UserOperation
    data class ComposableDeleted(val node: ComposeNode) : UserOperation
    data class ComposablePasted(val node: ComposeNode) : UserOperation
    data class ComposableLabelUpdated(val node: ComposeNode, val label: String) : UserOperation
    data class ParameterUpdated(val node: ComposeNode, val params: ComposeTrait) : UserOperation
    data class ModifierAdded(val node: ComposeNode, val modifier: ModifierWrapper) : UserOperation
    data class ModifierUpdated(val node: ComposeNode, val modifier: ModifierWrapper) : UserOperation
    data class ModifierDeleted(val node: ComposeNode, val modifier: ModifierWrapper) : UserOperation
    data class ModifierSwapped(val node: ComposeNode, val from: Int, val to: Int) : UserOperation
    data class PendingModifierCommitted(val node: ComposeNode, val pending: ModifierWrapper?) :
        UserOperation

    data class DynamicItemsUpdated(val assignableProperty: AssignableProperty?) : UserOperation
    data class LazyListChildParamsUpdated(val lazyListChildParams: LazyListChildParams) :
        UserOperation

    data class VisibilityParamsUpdated(val visibilityParams: VisibilityParams) : UserOperation
    data class WrapComposable(val node: ComposeNode, val wrapContainer: ComposeTrait) :
        UserOperation

    data class ScreenAddedFromTemplate(
        val name: String,
        val screenTemplatePair: ScreenTemplatePair
    ) :
        UserOperation

    data class ScreenAdded(val screen: Screen) : UserOperation

    data class ScreenDeleted(val screen: Screen) : UserOperation
    data class ScreenUpdated(val screen: Screen) : UserOperation
    data class BringToFront(val node: ComposeNode) : UserOperation
    data class SendToBack(val node: ComposeNode) : UserOperation
    data class ActionsMapUpdated(
        val node: ComposeNode,
        val actionsMap: Map<ActionType, List<ActionNode>>,
    ) : UserOperation

    data class ConvertToComponent(val componentName: String, val node: ComposeNode) : UserOperation
    data class CreateComponent(val componentName: String) : UserOperation
    data class RemoveComponent(val component: Component) : UserOperation
    data class AddParameterToCanvasEditable(
        val canvasEditable: CanvasEditable,
        val parameter: ParameterWrapper<*>,
    ) : UserOperation

    data class UpdateParameterInCanvasEditable(
        val canvasEditable: CanvasEditable,
        val parameter: ParameterWrapper<*>,
    ) : UserOperation

    data class RemoveParameterFromCanvasEditable(
        val canvasEditable: CanvasEditable,
        val parameter: ParameterWrapper<*>,
    ) : UserOperation

    data class UpdateColorSchemes(
        val sourceColor: Color,
        val paletteStyle: PaletteStyle,
        val lightScheme: ColorSchemeWrapper,
        val darkScheme: ColorSchemeWrapper,
    ) : UserOperation

    data class UpdateApi(val updatedApi: ApiDefinition) : UserOperation
    data object ResetColorSchemes : UserOperation
    data object Undo : UserOperation
    data object Redo : UserOperation
}
