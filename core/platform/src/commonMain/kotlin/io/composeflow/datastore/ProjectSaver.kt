package io.composeflow.datastore

const val projectYamlFileName = "project.yaml"

interface ProjectSaver {

    suspend fun saveProjectYaml(userId: String, projectId: String, yamlContent: String)
    suspend fun deleteProject(userId: String, projectId: String)
    suspend fun loadProject(userId: String, projectId: String): ProjectYamlNameWithLastModified?
    suspend fun loadProjectIdList(userId: String): List<String>
    suspend fun deleteCacheProjects()
}