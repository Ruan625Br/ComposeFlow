package io.composeflow.platform

import co.touchlab.kermit.Logger
import io.composeflow.cloud.storage.GoogleCloudStorageWrapper
import io.composeflow.di.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.time.Duration

object CloudProjectSaverRunner {
    var userId: String? = null
    var projectId: String? = null
    private val ioDispatcher =
        ServiceLocator.getOrPutWithKey(ServiceLocator.KeyIoDispatcher) {
            Dispatchers.IO
        }

    private val cloudProjectSaver = createCloudProjectSaver(
        cloudStorageWrapper = GoogleCloudStorageWrapper()
    )
    private val localProjectSaver = createLocalProjectSaver()

    suspend fun startSavingProjectPeriodically() {
        Logger.i("Started periodic sync for the project")
        withContext(ioDispatcher) {
            while (isActive) {
                syncProjectYaml()
                delay(Duration.ofMinutes(3))
            }
        }
    }

    suspend fun syncProjectYaml() {
        withContext(ioDispatcher) {
            val userId = userId ?: return@withContext
            val projectId = projectId ?: return@withContext
            val localYaml = localProjectSaver.loadProject(userId, projectId) ?: return@withContext

            Logger.i("Saving the project: $projectId to the cloud. ${Clock.System.now()}")
            cloudProjectSaver.saveProjectYaml(userId, projectId, localYaml.yaml)
        }
    }
}