package io.composeflow.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.datastore.LocalFirstProjectSaver
import io.composeflow.datastore.ProjectSaver
import io.composeflow.di.ServiceLocator
import io.composeflow.model.project.Project
import io.composeflow.model.project.serialize
import io.composeflow.platform.getOrCreateDataStore
import io.composeflow.serializer.yamlDefaultSerializer
import io.composeflow.util.toKotlinFileName
import io.composeflow.util.toPackageName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class ProjectRepository(
    private val firebaseIdToken: FirebaseIdToken,
    private val projectSaver: ProjectSaver = LocalFirstProjectSaver(),
    private val dataStore: DataStore<Preferences> = ServiceLocator.getOrPut { getOrCreateDataStore() },
) {
    private val editingProjectKey = stringPreferencesKey("editing_project")

    val editingProject: Flow<Project> =
        dataStore.data.map { preference ->
            preference[editingProjectKey]?.let { yamlDefaultSerializer.decodeFromString<Project>(it) }
                ?: Project()
        }

    suspend fun createProject(
        projectName: String,
        packageName: String,
    ): Project {
        val project =
            Project(
                name = projectName.toKotlinFileName(),
                packageName = packageName.toPackageName(),
            )
        projectSaver.saveProjectYaml(
            userId = firebaseIdToken.user_id,
            projectId = project.id,
            yamlContent = project.serialize(),
        )
        return project
    }

    suspend fun deleteProject(projectId: String) {
        projectSaver.deleteProject(
            userId = firebaseIdToken.user_id,
            projectId = projectId,
        )
    }

    suspend fun loadProject(projectId: String): Result<Project?, Throwable> =
        runCatching {
            val loaded =
                projectSaver.loadProject(
                    userId = firebaseIdToken.user_id,
                    projectId = projectId.removeSuffix(".yaml"),
                )
            loaded?.let {
                val project = Project.deserializeFromString(it.yaml)
                project.lastModified = it.lastModified
                project
            }
        }

    suspend fun updateProject(project: Project) {
        projectSaver.saveProjectYaml(
            userId = firebaseIdToken.user_id,
            projectId = project.id,
            yamlContent = project.serialize(),
        )

        // Save the project to DataStore, too so that Flow
        dataStore.edit {
            it[editingProjectKey] = yamlDefaultSerializer.encodeToString(project)
        }
    }

    suspend fun loadProjectIdList(): List<String> = projectSaver.loadProjectIdList(userId = firebaseIdToken.user_id)
}
