package io.composeflow.ui.uibuilder

import androidx.compose.ui.unit.dp
import io.composeflow.model.modifier.ModifierWrapper
import io.composeflow.model.parameter.ButtonTrait
import io.composeflow.model.parameter.ColumnTrait
import io.composeflow.model.parameter.FabTrait
import io.composeflow.model.parameter.TextTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.Screen
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import io.composeflow.serializer.yamlSerializer
import kotlinx.serialization.encodeToString
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for UiBuilderOperator focusing on core functionality that can be reliably tested
 * without making assumptions about internal implementation details.
 */
class UiBuilderOperatorTest {
    private lateinit var uiBuilderOperator: UiBuilderOperator
    private lateinit var project: Project
    private lateinit var screen: Screen
    private lateinit var rootNode: ComposeNode

    @Before
    fun setUp() {
        uiBuilderOperator = UiBuilderOperator()
        project = Project()
        screen = project.screenHolder.screens.first()
        rootNode = screen.getRootNode()
    }

    // ========== Helper Methods ==========

    private fun createColumnNode(): ComposeNode = ColumnTrait().defaultComposeNode(project)

    private fun createButtonNode(): ComposeNode = ButtonTrait().defaultComposeNode(project)

    private fun createTextNode(): ComposeNode = TextTrait().defaultComposeNode(project)

    private fun createFabNode(): ComposeNode = FabTrait().defaultComposeNode(project)

    private fun createPaddingModifier(): ModifierWrapper = ModifierWrapper.Padding(16.dp)

    // ========== Basic Structure Tests ==========

    @Test
    fun testProjectStructure() {
        // Test that the project has expected structure
        assertNotNull(project.screenHolder)
        assertNotNull(project.screenHolder.screens)
        assertTrue(project.screenHolder.screens.isNotEmpty())
        assertNotNull(project.screenHolder.currentEditable())
    }

    @Test
    fun testBasicNodeCreation() {
        val buttonNode = createButtonNode()
        assertTrue(buttonNode.trait.value is ButtonTrait)

        val columnNode = createColumnNode()
        assertTrue(columnNode.trait.value is ColumnTrait)

        val textNode = createTextNode()
        assertTrue(textNode.trait.value is TextTrait)
    }

    // ========== Node Addition Tests ==========

    @Test
    fun testOnPreAddComposeNodeToContainerNode_ContainerNotFound() {
        val nodeToAdd = createButtonNode()

        val result =
            uiBuilderOperator.onPreAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = "non-existent-id",
                composeNode = nodeToAdd,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("Container node with ID non-existent-id not found"))
    }

    @Test
    fun testOnPreAddComposeNodeToContainerNode_Success() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)
        val nodeToAdd = createButtonNode()

        val result =
            uiBuilderOperator.onPreAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNode = nodeToAdd,
            )

        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun testOnAddComposeNodeToContainerNode_LlmMethod() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)
        val buttonNode = createButtonNode()
        val buttonYaml = yamlSerializer.encodeToString(buttonNode)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = buttonYaml,
                indexToDrop = 0,
            )

        // The method returns an empty EventResult on success
        assertTrue(result.errorMessages.isEmpty())
    }

    // ========== Node Removal Tests ==========

    @Test
    fun testOnPreRemoveComposeNode_NullNode() {
        val result = uiBuilderOperator.onPreRemoveComposeNode(null)
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun testOnPreRemoveComposeNode_ValidNode() {
        val nodeToRemove = createButtonNode()
        val result = uiBuilderOperator.onPreRemoveComposeNode(nodeToRemove)
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun testOnRemoveComposeNode_NodeNotFound() {
        val result =
            uiBuilderOperator.onRemoveComposeNode(
                project = project,
                composeNodeId = "non-existent-id",
            )

        // The implementation doesn't check for existence in the error path
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun testOnRemoveComposeNode_ScreenOnlyNode_Fab() {
        val fabNode = createFabNode()
        screen.fabNode.value = fabNode

        val result =
            uiBuilderOperator.onRemoveComposeNode(
                project = project,
                composeNodeId = fabNode.id,
            )

        assertTrue(result.errorMessages.isEmpty())
        assertNull(screen.fabNode.value)
    }

    // ========== Modifier Tests - LLM Methods ==========

    @Test
    fun testOnAddModifier_NodeNotFound() {
        val modifier = createPaddingModifier()
        val modifierYaml = yamlSerializer.encodeToString(modifier)

        val result =
            uiBuilderOperator.onAddModifier(
                project = project,
                composeNodeId = "non-existent-id",
                modifierYaml = modifierYaml,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    @Test
    fun testOnUpdateModifier_NodeNotFound() {
        val modifier = createPaddingModifier()
        val modifierYaml = yamlSerializer.encodeToString(modifier)

        val result =
            uiBuilderOperator.onUpdateModifier(
                project = project,
                composeNodeId = "non-existent-id",
                index = 0,
                modifierYaml = modifierYaml,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    @Test
    fun testOnUpdateModifier_InvalidIndex() {
        val node = createButtonNode()
        rootNode.addChild(node)
        val modifier = createPaddingModifier()
        val modifierYaml = yamlSerializer.encodeToString(modifier)

        val result =
            uiBuilderOperator.onUpdateModifier(
                project = project,
                composeNodeId = node.id,
                index = 5, // Invalid index for empty modifier list
                modifierYaml = modifierYaml,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("Invalid modifier index"))
    }

    @Test
    fun testOnRemoveModifier_NodeNotFound() {
        val result =
            uiBuilderOperator.onRemoveModifier(
                project = project,
                composeNodeId = "non-existent-id",
                index = 0,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    @Test
    fun testOnRemoveModifier_InvalidIndex() {
        val node = createButtonNode()
        rootNode.addChild(node)

        // Clear any default modifiers to ensure we're testing with no modifiers
        node.modifierList.clear()

        val result =
            uiBuilderOperator.onRemoveModifier(
                project = project,
                composeNodeId = node.id,
                index = 0, // No modifiers exist
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("Invalid modifier index"))
    }

    @Test
    fun testOnSwapModifiers_NodeNotFound() {
        val result =
            uiBuilderOperator.onSwapModifiers(
                project = project,
                composeNodeId = "non-existent-id",
                fromIndex = 0,
                toIndex = 1,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    @Test
    fun testOnSwapModifiers_InvalidIndices() {
        val node = createButtonNode()
        rootNode.addChild(node)
        val modifier = createPaddingModifier()
        node.modifierList.add(modifier)

        val result =
            uiBuilderOperator.onSwapModifiers(
                project = project,
                composeNodeId = node.id,
                fromIndex = 0,
                toIndex = 5, // Invalid index
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("Invalid modifier indices"))
    }

    // ========== Node Movement Tests ==========

    @Test
    fun testOnPreMoveComposeNodeToPosition_NodeNotFound() {
        val container = createColumnNode()
        rootNode.addChild(container)

        val result =
            uiBuilderOperator.onPreMoveComposeNodeToPosition(
                project = project,
                composeNodeId = "non-existent-id",
                containerNodeId = container.id,
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    @Test
    fun testOnPreMoveComposeNodeToPosition_ContainerNotFound() {
        val nodeToMove = createButtonNode()
        rootNode.addChild(nodeToMove)

        val result =
            uiBuilderOperator.onPreMoveComposeNodeToPosition(
                project = project,
                composeNodeId = nodeToMove.id,
                containerNodeId = "non-existent-id",
            )

        assertFalse(result.errorMessages.isEmpty())
        assertTrue(result.errorMessages.first().contains("not found"))
    }

    // ========== Error Handling Tests ==========

    @Test
    fun testInvalidYamlHandling() {
        val result =
            uiBuilderOperator.onAddModifier(
                project = project,
                composeNodeId = "some-id",
                modifierYaml = "invalid-yaml-content",
            )

        // Should have error messages due to invalid YAML or missing node
        assertFalse(result.errorMessages.isEmpty())
    }

    // ========== Serialization Tests ==========

    @Test
    fun testModifierSerialization() {
        val modifier = createPaddingModifier()
        val yaml = yamlSerializer.encodeToString(modifier)

        // Basic check that serialization produces non-empty result
        assertTrue(yaml.isNotEmpty())
        assertTrue(yaml.contains("Padding"))
    }

    @Test
    fun testNodeSerialization() {
        val buttonNode = createButtonNode()
        val yaml = yamlSerializer.encodeToString(buttonNode)

        // Basic check that serialization produces non-empty result
        assertTrue(yaml.isNotEmpty())
        assertTrue(yaml.contains("Button"))
    }
}
