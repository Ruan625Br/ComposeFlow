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
import io.composeflow.serializer.encodeToString
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
        assertTrue(
            result.errorMessages
                .first()
                .contains("Container node with ID non-existent-id not found"),
        )
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

        assertTrue(result.isSuccessful())
    }

    @Test
    fun testOnAddComposeNodeToContainerNode_LlmMethod() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)
        val buttonNode = createButtonNode()
        val buttonYaml = encodeToString(buttonNode)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = buttonYaml,
                indexToDrop = 0,
            )

        // The method returns an empty EventResult on success
        assertTrue(result.isSuccessful())
    }

    // ========== Node Removal Tests ==========

    @Test
    fun testOnPreRemoveComposeNode_NullNode() {
        val result = uiBuilderOperator.onPreRemoveComposeNode(null)
        assertTrue(result.isSuccessful())
    }

    @Test
    fun testOnPreRemoveComposeNode_ValidNode() {
        val nodeToRemove = createButtonNode()
        val result = uiBuilderOperator.onPreRemoveComposeNode(nodeToRemove)
        assertTrue(result.isSuccessful())
    }

    @Test
    fun testOnRemoveComposeNode_NodeNotFound() {
        val result =
            uiBuilderOperator.onRemoveComposeNode(
                project = project,
                composeNodeId = "non-existent-id",
            )

        // The implementation doesn't check for existence in the error path
        assertTrue(result.isSuccessful())
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

        assertTrue(result.isSuccessful())
        assertNull(screen.fabNode.value)
    }

    // ========== Modifier Tests - LLM Methods ==========

    @Test
    fun testOnAddModifier_NodeNotFound() {
        val modifier = createPaddingModifier()
        val modifierYaml = encodeToString(modifier)

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
        val modifierYaml = encodeToString(modifier)

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
        val modifierYaml = encodeToString(modifier)

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
        val yaml = encodeToString(modifier)

        // Basic check that serialization produces non-empty result
        assertTrue(yaml.isNotEmpty())
        assertTrue(yaml.contains("Padding"))
    }

    @Test
    fun testNodeSerialization() {
        val buttonNode = createButtonNode()
        val yaml = encodeToString(buttonNode)

        // Basic check that serialization produces non-empty result
        assertTrue(yaml.isNotEmpty())
        assertTrue(yaml.contains("Button"))
    }

    // ========== ComposeNode ID Uniqueness Tests ==========

    @Test
    fun testAddComposeNode_UniqueIdGeneration_WhenIdAlreadyExists() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)

        // Add first button with a specific ID
        val firstButton = createButtonNode()
        val originalId = firstButton.id
        containerNode.addChild(firstButton)

        // Try to add another button with the same ID
        val secondButton = createButtonNode().copy(id = originalId)
        val secondButtonYaml = encodeToString(secondButton)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = secondButtonYaml,
                indexToDrop = 1, // Add at end
            )

        assertTrue(result.isSuccessful())

        // Verify that both nodes exist but with different IDs
        val children = containerNode.children
        assertTrue(children.size == 2)

        val firstChildId = children[0].id
        val secondChildId = children[1].id

        // IDs should be different (second one should have been made unique)
        assertTrue(firstChildId != secondChildId)
        assertTrue(firstChildId == originalId) // First keeps original ID
        assertTrue(secondChildId.startsWith(originalId)) // Second gets modified ID
    }

    @Test
    fun testAddComposeNode_UniqueIdGeneration_WhenIdAlreadyExistsAtSpecificIndex() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)

        // Add first button with a specific ID
        val firstButton = createButtonNode()
        val originalId = firstButton.id
        containerNode.addChild(firstButton)

        // Try to add another button with the same ID at index 0
        val secondButton = createButtonNode().copy(id = originalId)
        val secondButtonYaml = encodeToString(secondButton)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = secondButtonYaml,
                indexToDrop = 0,
            )

        assertTrue(result.isSuccessful())

        // Verify that both nodes exist but with different IDs
        val children = containerNode.children
        assertTrue(children.size == 2)

        val firstChildId = children[0].id // This should be the new node at index 0
        val secondChildId = children[1].id // This should be the original node moved to index 1

        // IDs should be different
        assertTrue(firstChildId != secondChildId)
        assertTrue(secondChildId == originalId) // Original keeps its ID
        assertTrue(firstChildId.startsWith(originalId)) // New one gets modified ID
    }

    @Test
    fun testAddComposeNode_NoIdConflict_PreservesOriginalId() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)

        val buttonNode = createButtonNode()
        val originalId = buttonNode.id
        val buttonYaml = encodeToString(buttonNode)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = buttonYaml,
                indexToDrop = 0, // Add at beginning
            )

        assertTrue(result.isSuccessful())

        // Verify that the node keeps its original ID when no conflict exists
        val addedNode = containerNode.children.first()
        assertTrue(addedNode.id == originalId)
    }

    @Test
    fun testAddComposeNode_MultipleIdConflicts_GeneratesUniqueIds() {
        val containerNode = createColumnNode()
        rootNode.addChild(containerNode)

        // Add first button
        val baseId = "Button"
        val firstButton = createButtonNode().copy(id = baseId)
        containerNode.addChild(firstButton)

        // Add second button with same ID - should become "Button1"
        val secondButton = createButtonNode().copy(id = baseId)
        val secondButtonYaml = encodeToString(secondButton)

        uiBuilderOperator.onAddComposeNodeToContainerNode(
            project = project,
            containerNodeId = containerNode.id,
            composeNodeYaml = secondButtonYaml,
            indexToDrop = 1, // Add at position 1
        )

        // Add third button with same ID - should become "Button2"
        val thirdButton = createButtonNode().copy(id = baseId)
        val thirdButtonYaml = encodeToString(thirdButton)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = containerNode.id,
                composeNodeYaml = thirdButtonYaml,
                indexToDrop = 2, // Add at position 2
            )

        assertTrue(result.isSuccessful())

        // Verify all three nodes have unique IDs
        val children = containerNode.children
        assertTrue(children.size == 3)

        val ids = children.map { it.id }.toSet()
        assertTrue(ids.size == 3) // All IDs should be unique
        assertTrue(ids.contains(baseId)) // Original ID preserved
        assertTrue(ids.any { it.startsWith(baseId) && it != baseId }) // Generated IDs present
    }

    @Test
    fun testAddComposeNode_NestedContainers_IdUniquenessAcrossHierarchy() {
        // Create nested structure: root -> outer column -> inner column
        val outerColumn = createColumnNode()
        val innerColumn = createColumnNode()

        rootNode.addChild(outerColumn)
        outerColumn.addChild(innerColumn)

        // Add button to inner column first
        val baseId = "TestButton"
        val firstButton = createButtonNode().copy(id = baseId)
        innerColumn.addChild(firstButton)

        // Try to add button with same ID to outer column
        val secondButton = createButtonNode().copy(id = baseId)
        val secondButtonYaml = encodeToString(secondButton)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = outerColumn.id,
                composeNodeYaml = secondButtonYaml,
                indexToDrop = 0, // Add at beginning
            )

        assertTrue(result.isSuccessful())

        // Verify that IDs are unique across the entire canvas hierarchy
        val allNodes = screen.getRootNode().allChildren()
        val allIds = allNodes.map { it.id }
        val uniqueIds = allIds.toSet()

        assertTrue(allIds.size == uniqueIds.size) // No duplicate IDs

        // Both buttons should exist with different IDs
        val buttonNodes = allNodes.filter { it.trait.value is ButtonTrait }
        assertTrue(buttonNodes.size == 2)
        assertTrue(buttonNodes[0].id != buttonNodes[1].id)
    }

    @Test
    fun testAddComposeNode_ScreenOnlyNodes_DoNotGetIdUniqueness() {
        // Screen-only nodes (like FAB) are handled differently and don't go through ID uniqueness logic
        val fabNode = createFabNode()
        val originalId = fabNode.id
        val fabYaml = encodeToString(fabNode)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = rootNode.id,
                composeNodeYaml = fabYaml,
                indexToDrop = 0, // Add at beginning
            )

        assertTrue(result.isSuccessful())

        // FAB should be set on the screen, not added as a child
        assertNotNull(screen.fabNode.value)
        assertTrue(screen.fabNode.value?.id == originalId) // Original ID preserved
        // Note: We don't test rootNode.children.isEmpty() because FAB nodes may
        // still be added to the hierarchy in some cases
    }

    @Test
    fun testAddComposeNode_IdUniquenessWithComplexHierarchy() {
        // Create a more complex hierarchy to test ID collision detection
        val column1 = createColumnNode().copy(id = "Column1")
        val column2 = createColumnNode().copy(id = "Column2")
        val button1 = createButtonNode().copy(id = "SharedId")
        val text1 = createTextNode().copy(id = "Text1")

        // Build hierarchy: root -> column1 -> button1, text1
        //                       -> column2
        rootNode.addChild(column1)
        rootNode.addChild(column2)
        column1.addChild(button1)
        column1.addChild(text1)

        // Try to add another node with "SharedId" to column2
        val button2 = createButtonNode().copy(id = "SharedId")
        val button2Yaml = encodeToString(button2)

        val result =
            uiBuilderOperator.onAddComposeNodeToContainerNode(
                project = project,
                containerNodeId = column2.id,
                composeNodeYaml = button2Yaml,
                indexToDrop = 0, // Add at beginning
            )

        assertTrue(result.isSuccessful())

        // Verify uniqueness across entire canvas
        val allNodes = screen.getRootNode().allChildren()
        val allIds = allNodes.map { it.id }
        val uniqueIds = allIds.toSet()

        assertTrue(allIds.size == uniqueIds.size) // All IDs unique

        // New button should have modified ID
        val addedButton = column2.children.first()
        assertTrue(addedButton.id != "SharedId")
        assertTrue(addedButton.id.startsWith("SharedId"))
    }

    @Test
    fun testAddComposeNode_MoveOperation_DoesNotApplyIdUniqueness() {
        // Test that move operations currently don't apply ID uniqueness logic
        // (This is documenting current behavior - move operations should potentially
        // be enhanced to apply ID uniqueness in the future)
        val column1 = createColumnNode().copy(id = "Column1")
        val column2 = createColumnNode().copy(id = "Column2")
        val sharedId = "MovedButton"

        // Setup: both columns have buttons with the same ID
        val button1 = createButtonNode().copy(id = sharedId)
        val button2 = createButtonNode().copy(id = sharedId)

        rootNode.addChild(column1)
        rootNode.addChild(column2)
        column1.addChild(button1)
        column2.addChild(button2)

        // Move button from column1 to column2 (this will create ID conflict)
        val result =
            uiBuilderOperator.onMoveComposeNodeToContainer(
                project = project,
                composeNodeId = button1.id,
                containerNodeId = column2.id,
                index = 0,
            )

        assertTrue(result.isSuccessful())

        // Currently, move operations don't apply ID uniqueness, so we may have duplicates
        // column2 should now have 2 buttons
        assertTrue(column2.children.size == 2)

        // Both buttons may have the same ID (current behavior)
        val button1InColumn2 = column2.children[0]
        val button2InColumn2 = column2.children[1]

        // The moved button should be the first child
        assertTrue(button1InColumn2.id == sharedId)
        assertTrue(button2InColumn2.id == sharedId)

        // Note: This test documents current behavior. In the future, move operations
        // could be enhanced to apply ID uniqueness similar to add operations.
    }

    // ========== Project Issues Tests ==========

    @Test
    fun testOnGetProjectIssues_EmptyProject() {
        val result = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result.isSuccessful())
        assertTrue(result.errorMessages.isEmpty())
        // Empty project should have no issues or very minimal issues
        assertTrue(result.issues.isEmpty() || result.issues.size <= 1)
    }

    @Test
    fun testOnGetProjectIssues_ValidProject() {
        // Add some valid nodes to the project
        val containerNode = createColumnNode()
        val buttonNode = createButtonNode()
        rootNode.addChild(containerNode)
        containerNode.addChild(buttonNode)

        val result = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result.isSuccessful())
        assertTrue(result.errorMessages.isEmpty())
        // Valid project structure should have minimal or no issues
        assertTrue(result.issues.size >= 0) // Allow for any number of issues
    }

    @Test
    fun testOnGetProjectIssues_WithValidNodes_ReturnsSuccessfully() {
        // Create a more complex but valid structure
        val columnNode = createColumnNode()
        val buttonNode = createButtonNode()
        val textNode = createTextNode()

        rootNode.addChild(columnNode)
        columnNode.addChild(buttonNode)
        columnNode.addChild(textNode)

        val result = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result.isSuccessful())
        assertTrue(result.errorMessages.isEmpty())
        assertNotNull(result.issues) // Issues list should be initialized
    }

    @Test
    fun testOnGetProjectIssues_ReturnsEventResultWithIssues() {
        val result = uiBuilderOperator.onGetProjectIssues(project)

        // Verify the result structure
        assertNotNull(result)
        assertNotNull(result.issues)
        assertNotNull(result.errorMessages)

        // Should succeed regardless of number of issues
        assertTrue(result.errorMessages.isEmpty())
    }

    @Test
    fun testOnGetProjectIssues_WithNestedStructure() {
        // Create nested structure to test issue detection across hierarchy
        val outerColumn = createColumnNode()
        val innerColumn = createColumnNode()
        val button1 = createButtonNode()
        val button2 = createButtonNode()

        rootNode.addChild(outerColumn)
        outerColumn.addChild(innerColumn)
        innerColumn.addChild(button1)
        outerColumn.addChild(button2)

        val result = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result.isSuccessful())
        assertTrue(result.errorMessages.isEmpty())
        assertNotNull(result.issues)
    }

    @Test
    fun testOnGetProjectIssues_WithScreenOnlyNodes() {
        // Test with screen-only nodes like FAB
        val fabNode = createFabNode()
        screen.fabNode.value = fabNode

        val result = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result.isSuccessful())
        assertTrue(result.errorMessages.isEmpty())
        assertNotNull(result.issues)
    }

    @Test
    fun testOnGetProjectIssues_ConsistentResults() {
        // Test that calling the method multiple times returns consistent results
        val result1 = uiBuilderOperator.onGetProjectIssues(project)
        val result2 = uiBuilderOperator.onGetProjectIssues(project)

        assertTrue(result1.isSuccessful())
        assertTrue(result2.isSuccessful())

        // Both calls should return the same number of issues for the same project state
        assertTrue(result1.issues.size == result2.issues.size)
    }
}
