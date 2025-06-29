package io.composeflow.platform

import io.composeflow.cloud.storage.GoogleCloudStorageWrapper
import io.composeflow.datastore.ProjectSaver

expect fun createCloudProjectSaver(cloudStorageWrapper: GoogleCloudStorageWrapper): ProjectSaver
