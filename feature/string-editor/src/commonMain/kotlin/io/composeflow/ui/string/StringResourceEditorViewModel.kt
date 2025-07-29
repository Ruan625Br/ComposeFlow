package io.composeflow.ui.string

import io.composeflow.auth.FirebaseIdToken
import io.composeflow.model.project.Project
import io.composeflow.model.project.string.ResourceLocale
import io.composeflow.model.project.string.StringResource
import io.composeflow.override.toMutableStateMapEqualsOverride
import io.composeflow.repository.ProjectRepository
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class StringResourceEditorViewModel(
    firebaseIdToken: FirebaseIdToken,
    private val project: Project,
    private val projectRepository: ProjectRepository = ProjectRepository(firebaseIdToken),
    private val stringResourceEditorOperator: StringResourceEditorOperator = StringResourceEditorOperator(),
) : ViewModel() {
    fun onAddStringResource(
        key: String,
        description: String,
        defaultValue: String,
    ) {
        if (key.isNotBlank() && defaultValue.isNotBlank()) {
            val newResource =
                StringResource(
                    key = key,
                    description = description.ifBlank { null },
                    localizedValues = mutableMapOf(project.stringResourceHolder.defaultLocale.value to defaultValue),
                )
            val result = stringResourceEditorOperator.addStringResource(project, newResource)
            if (result.errorMessages.isEmpty()) {
                saveProject()
            }
        }
    }

    fun onUpdateStringResourceKey(
        resource: StringResource,
        newKey: String,
    ) {
        val updatedResource = resource.copy(key = newKey)
        val result = stringResourceEditorOperator.updateStringResource(project, updatedResource)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    fun onUpdateStringResourceDescription(
        resource: StringResource,
        newDescription: String,
    ) {
        val updatedResource = resource.copy(description = newDescription.ifBlank { null })
        val result = stringResourceEditorOperator.updateStringResource(project, updatedResource)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    fun onUpdateStringResourceValue(
        resource: StringResource,
        locale: ResourceLocale,
        value: String,
    ) {
        val updatedResource =
            resource.copy(
                localizedValues =
                    resource.localizedValues.toMutableStateMapEqualsOverride().apply {
                        this[locale] = value
                    },
            )
        val result = stringResourceEditorOperator.updateStringResource(project, updatedResource)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    fun onDeleteStringResource(resource: StringResource) {
        val result = stringResourceEditorOperator.deleteStringResource(project, resource.id)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    fun onUpdateSupportedLocales(newLocales: List<ResourceLocale>) {
        val result = stringResourceEditorOperator.updateSupportedLocales(project, newLocales)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    fun onUpdateDefaultLocale(locale: ResourceLocale) {
        val result = stringResourceEditorOperator.setDefaultLocale(project, locale)
        if (result.errorMessages.isEmpty()) {
            saveProject()
        }
    }

    private fun saveProject() {
        viewModelScope.launch {
            projectRepository.updateProject(project)
        }
    }
}
