package io.composeflow.platform

import io.composeflow.datastore.ProjectSaver
import io.composeflow.datastore.ProjectYamlNameWithLastModified
import io.composeflow.datastore.projectYamlFileName
import io.composeflow.di.ServiceLocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.io.File

actual fun createLocalProjectSaver(): ProjectSaver = LocalProjectSaverImpl()

class LocalProjectSaverImpl(
    private val ioDispatcher: CoroutineDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KeyIoDispatcher) {
            Dispatchers.IO
        },
) : ProjectSaver {
    override suspend fun saveProjectYaml(
        userId: String,
        projectId: String,
        yamlContent: String,
    ) {
        withContext(ioDispatcher) {
            val projectsDir = prepareProjectDir(userId = userId, projectId = projectId)
            val file = projectsDir.resolve(projectYamlFileName)
            file.writeText(yamlContent)
        }
    }

    override suspend fun deleteProject(
        userId: String,
        projectId: String,
    ) {
        withContext(ioDispatcher) {
            val projectDir = getCacheDir().resolve("projects").resolve(userId).resolve(projectId)
            if (projectDir.exists()) {
                projectDir.deleteRecursively()
            }
        }
    }

    override suspend fun loadProject(
        userId: String,
        projectId: String,
    ): ProjectYamlNameWithLastModified? {
        val projectsDir = prepareProjectDir(userId = userId, projectId = projectId)
        val file = projectsDir.resolve(projectYamlFileName)
        if (!file.exists()) {
            return null
        }
        return ProjectYamlNameWithLastModified(
            file.readText(),
            Instant.fromEpochMilliseconds(file.lastModified()),
        )
    }

    override suspend fun loadProjectIdList(userId: String): List<String> {
        val userProjectDir = getCacheDir().resolve("projects").resolve(userId)
        return buildList {
            userProjectDir.listFiles()?.toList()?.forEach { projectDir ->
                if (projectDir.resolve(projectYamlFileName).exists()) {
                    add(projectDir.name)
                }
            }
        }
    }

    override suspend fun deleteCacheProjects() {
        val projectDir = getCacheDir().resolve("projects")
        if (projectDir.exists()) {
            projectDir.deleteRecursively()
        }
    }
}

private fun prepareProjectDir(
    userId: String,
    projectId: String,
): File {
    val projectsDir = getCacheDir().resolve("projects").resolve(userId).resolve(projectId)
    projectsDir.mkdirs()
    return projectsDir
}
