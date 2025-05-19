import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.YamlTaggedNode
import kotlin.uuid.Uuid

/**
 * Recursively traverses a kaml YamlNode structure
 * and prefixes specific ID fields if they don't already start with the prefix.
 *
 * @param node The current YamlNode being processed.
 * @param screenId The ID of the screen to use as the prefix base.
 * @param idPrefix The actual prefix string (e.g., "screenId:").
 * @return The potentially modified YamlNode.
 */
fun prefixIdInYamlNode(node: YamlNode, screenId: String, idPrefix: String): YamlNode {
    return when (node) {
        is YamlMap -> {
            // Process maps (YAML objects)
            // Create new entries, potentially modifying values
            val newEntries = node.entries.mapValues { (keyNode, valueNode) ->
                // Key must be a scalar for map keys
                val key = (keyNode as? YamlScalar)?.content ?: keyNode.toString() // Get key string
                // Recursively process the value node
                val processedValueNode = prefixIdInYamlNode(valueNode, screenId, idPrefix)

                // screenId is used to navigate to other screens. Thus, excluding it from prefixed
                if (key == "id" || key.endsWith("Id") && key != "screenId") {
                    // Check if the key indicates an ID field
                    if (processedValueNode is YamlScalar && !processedValueNode.content.startsWith(
                            idPrefix
                        )
                    ) {
                        // If the value is a scalar string and not prefixed, create a new scalar with the prefix
                        // Use the location from the original value node
                        YamlScalar(
                            content = "$idPrefix${processedValueNode.content}",
                            path = processedValueNode.path
                        )
                    } else {
                        // Otherwise, keep the processed value node as is
                        processedValueNode
                    }
                } else {
                    // Not an ID field, just use the processed value node
                    processedValueNode
                }
            }
            // Return a new YamlMap with original keys but potentially modified values
            // Use the newEntries map directly, keys are already YamlNodes
            YamlMap(newEntries, node.path) // Use location from original map
        }

        is YamlList -> {
            // Process lists (YAML sequences)
            // Create a new list by processing each element
            val newItems =
                node.items.map { element -> prefixIdInYamlNode(element, screenId, idPrefix) }
            // Return a new YamlList with processed items
            YamlList(newItems, node.path) // Use location from original list
        }

        is YamlTaggedNode -> {
            // NEW: Handle tagged nodes explicitly
            // Recursively process the inner node wrapped by the tag
            val processedInnerNode = prefixIdInYamlNode(node.innerNode, screenId, idPrefix)
            // Return a new tagged node with the original tag and the processed inner node
            YamlTaggedNode(node.tag, processedInnerNode)
        }

        is YamlScalar -> node // Base case: Return scalar nodes as is
        is YamlNull -> node   // Base case: Return null nodes as is
        else -> node // Should not happen for typical YAML
    }
}


/**
 * Recursively traverses a kaml YamlNode structure
 * and replace ID fields with new UUIDs while keeping the relationships with the original IDs.
 *
 * @param node The current YamlNode being processed.
 * @param idMap The Map to store the original ID to new UUID mappings.
 * @return The potentially modified YamlNode.
 */
fun replaceIdInYamlNode(
    node: YamlNode,
    idMap: MutableMap<String, String> = mutableMapOf()
): YamlNode {
    return when (node) {
        is YamlMap -> {
            val newEntries = node.entries.mapValues { (keyNode, valueNode) ->
                val key = (keyNode as? YamlScalar)?.content ?: keyNode.toString()
                val processedValueNode = replaceIdInYamlNode(valueNode, idMap)

                if (key == "id" || (key.endsWith("Id"))) {
                    if (processedValueNode is YamlScalar) {
                        val originalId = processedValueNode.content
                        val newId = idMap.getOrPut(originalId) { Uuid.random().toString() }
                        YamlScalar(content = newId, path = processedValueNode.path)
                    } else {
                        processedValueNode
                    }
                } else {
                    processedValueNode
                }
            }
            YamlMap(newEntries, node.path)
        }

        is YamlList -> {
            val newItems =
                node.items.map { element -> replaceIdInYamlNode(element, idMap) }
            YamlList(newItems, node.path)
        }

        is YamlTaggedNode -> {
            val processedInnerNode = replaceIdInYamlNode(node.innerNode, idMap)
            YamlTaggedNode(node.tag, processedInnerNode)
        }

        is YamlScalar -> node
        is YamlNull -> node
        else -> node
    }
}
