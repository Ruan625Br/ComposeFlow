package io.composeflow.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.composeflow.UiBuilderViewModel
import io.composeflow.model.parameter.ButtonTrait
import io.composeflow.model.parameter.ColumnTrait
import io.composeflow.model.parameter.RowTrait
import io.composeflow.model.parameter.TextFieldTrait
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.repository.fakeFirebaseIdToken
import io.composeflow.repository.fakeProjectRepository
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UiBuilderViewModelTest {

    private lateinit var viewModel: UiBuilderViewModel

    @Before
    fun setUp() {
        viewModel = UiBuilderViewModel(
            firebaseIdToken = fakeFirebaseIdToken,
            projectId = "testId",
            projectRepository = fakeProjectRepository,
        )
        viewModel.project.screenHolder.screens.forEach {
            it.rootNode.value.boundsInWindow.value = Rect(0f, 0f, 400f, 400f)
            it.contentRootNode().boundsInWindow.value = Rect(0f, 0f, 400f, 400f)
        }
    }

    @Test
    fun testDropTextField_assertChild_isAdded_textField_dependsOn_state() {
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(50f, 50f),
            TextFieldTrait().defaultComposeNode(viewModel.project),
        )
        val screen = viewModel.project.screenHolder.currentEditable()
        val rootNode = screen.getRootNode()
        assert(rootNode.allChildren().any { it.trait.value is TextFieldTrait })
        val states = screen.getStates(viewModel.project)
        val textFieldNode =
            rootNode.allChildren().firstOrNull { it.trait.value is TextFieldTrait }
        assertTrue(states.any { textFieldNode?.isDependent(it.id) == true })
    }

    @Test
    fun testDeleteTextField_assert_orphan_state_is_deleted() {
        val textField = TextFieldTrait().defaultComposeNode(viewModel.project)
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(50f, 50f),
            textField,
        )
        val textField1 = TextFieldTrait().defaultComposeNode(viewModel.project)
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(50f, 50f),
            textField1,
        )
        val editable = viewModel.project.screenHolder.currentEditable()
        val rootNode = editable.getRootNode()
        textField1.isFocused.value = true

        assert(rootNode.allChildren().count { it.trait.value is TextFieldTrait } == 2)
        assert(editable.getStates(viewModel.project).size == 2)

        viewModel.onDeleteKey()

        assert(rootNode.allChildren().count { it.trait.value is TextFieldTrait } == 1)
        assertEquals(1, editable.getStates(viewModel.project).size)
    }

    @Test
    fun testConvertToComponent() {
        val row = RowTrait().defaultComposeNode(viewModel.project).apply {
            boundsInWindow.value = Rect(0f, 0f, 400f, 400f)
        }
        val textField = TextFieldTrait().defaultComposeNode(viewModel.project)
        val button = ButtonTrait().defaultComposeNode(viewModel.project)
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(50f, 50f),
            row,
        )
        // When the TextField is dropped, matching state should be created
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(70f, 70f),
            textField,
        )
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(70f, 70f),
            button,
        )

        val screen = viewModel.project.screenHolder.currentEditable()
        assert(screen is Screen)

        // Before the conversion, screen has 1 states for TextField
        assertEquals(1, screen.getStates(viewModel.project).size)

        viewModel.onConvertToComponent("testComponent", row)

        // After the conversion, screen has no states
        assertEquals(0, screen.getStates(viewModel.project).size)

        assertEquals(1, viewModel.project.componentHolder.components.size)
        val component = viewModel.project.componentHolder.components[0]

        // After the conversion, the component has 1 state for TextField
        assertEquals(1, component.getStates(viewModel.project).size)
        assertTrue(
            viewModel.project.getAllComposeNodes().any { it.componentId == component.id },
        )
    }

    @Test
    fun testWrapWithComposable() {
        val row = RowTrait().defaultComposeNode(viewModel.project).apply {
            boundsInWindow.value = Rect(0f, 0f, 400f, 400f)
        }
        val textField = TextFieldTrait().defaultComposeNode(viewModel.project)
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(50f, 50f),
            row,
        )
        // When the TextField is dropped, matching state should be created
        viewModel.onComposableDroppedToTarget(
            dropPosition = Offset(70f, 70f),
            textField,
        )

        val screen = viewModel.project.screenHolder.currentEditable()
        assert(screen is Screen)
        assertEquals(row.id, textField.parentNode?.id)

        viewModel.onWrapWithContainerComposable(textField, ColumnTrait())

        val afterTextField =
            screen.getRootNode().allChildren().firstOrNull { it.id == textField.id }
        assertNotEquals(row.id, afterTextField?.parentNode?.id)
        assertEquals(ColumnTrait(), afterTextField?.parentNode?.trait?.value)
    }
}
