package io.composeflow.datastore

import io.composeflow.cloud.storage.GoogleCloudStorageWrapper
import io.composeflow.platform.createCloudProjectSaver
import io.composeflow.platform.createLocalProjectSaver

class LocalFirstProjectSaver(
    private val cloudProjectSaver: ProjectSaver = createCloudProjectSaver(
        GoogleCloudStorageWrapper()
    ),
    private val localProjectSaver: ProjectSaver = createLocalProjectSaver(),
) : ProjectSaver {

    override suspend fun saveProjectYaml(userId: String, projectId: String, yamlContent: String) {
        localProjectSaver.saveProjectYaml(userId, projectId, yamlContent)
        // Don't save to the cloud unless CloudProjectSaver is used explicitly
    }

    override suspend fun deleteProject(userId: String, projectId: String) {
        localProjectSaver.deleteProject(userId, projectId)
        cloudProjectSaver.deleteProject(userId, projectId)
    }

    override suspend fun loadProject(
        userId: String,
        projectId: String,
    ): ProjectYamlNameWithLastModified? {
        var result = localProjectSaver.loadProject(userId, projectId)
        if (result != null) {
            return result
        }
        result = cloudProjectSaver.loadProject(userId, projectId)
        result?.let {
            localProjectSaver.saveProjectYaml(userId, projectId, it.yaml)
        }
        return result
    }

    override suspend fun loadProjectIdList(userId: String): List<String> {
        val idsInCloud = cloudProjectSaver.loadProjectIdList(userId)
        val idsInLocal = localProjectSaver.loadProjectIdList(userId)
        return (idsInCloud + idsInLocal).toSet().toList()
    }

    /**
     * Called when the user logs out to delete the locally stored project files
     */
    override suspend fun deleteCacheProjects() {
        localProjectSaver.deleteCacheProjects()
    }
}