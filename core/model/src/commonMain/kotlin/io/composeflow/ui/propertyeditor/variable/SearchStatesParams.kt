package io.composeflow.ui.propertyeditor.variable

import io.composeflow.model.project.ParameterWrapper
import io.composeflow.model.project.Project
import io.composeflow.model.project.findDataTypeOrNull
import io.composeflow.model.property.AssignableProperty
import io.composeflow.model.type.ComposeFlowType

data class SearchStatesParams(
    val searchText: String = "",
    val searchOnlyAcceptableType: Boolean = false,
    val acceptableType: ComposeFlowType,
) {
    val isFilterEnabled: Boolean = searchText.isNotBlank() || searchOnlyAcceptableType

    fun matchCriteria(project: Project, assignableProperty: AssignableProperty): Boolean {
        return matchCriteria(
            project,
            displayText = assignableProperty.displayText(project),
            type = assignableProperty.valueType(project)
        )
    }

    fun matchCriteria(project: Project, displayText: String, type: ComposeFlowType): Boolean {
        val typeMatch = if (searchOnlyAcceptableType) {
            val ableToAssign = acceptableType.isAbleToAssign(type, exactMatch = true)
            val hasFieldsAbleToAssign =
                type.hasTypeThatIsAbleToAssign(project, acceptableType, exactMatch = true)
            ableToAssign || hasFieldsAbleToAssign
        } else {
            true
        }
        val textMatch = matchText(displayText)
        return typeMatch && textMatch
    }

    fun matchCriteria(project: Project, parameter: ParameterWrapper<*>): Boolean {
        val typeMatch = matchType(project, parameter.parameterType)
        val textMatch = matchText(parameter.variableName)
        return typeMatch && textMatch
    }

    private fun matchText(text: String): Boolean {
        return if (searchText.isNotBlank()) {
            text.contains(searchText, ignoreCase = true)
        } else {
            true
        }
    }

    private fun matchType(project: Project, type: ComposeFlowType): Boolean {
        val result = if (type is ComposeFlowType.CustomDataType && !type.isList) {
            val dataType = (type as? ComposeFlowType.CustomDataType)?.dataTypeId?.let {
                project.findDataTypeOrNull(it)
            }
            dataType?.fields?.any {
                matchType(project, it.fieldType.type())
            }
        } else {
            acceptableType.isAbleToAssign(type)
        }
        return result == true
    }
}