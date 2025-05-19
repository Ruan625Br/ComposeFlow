package io.composeflow.cloud.storage

import kotlinx.serialization.Serializable

class Reference(
    val location: Location,
    private val client: FirebaseStorageClient = FirebaseStorageClient()
) {
    suspend fun listAll(): ListResult {
        return client.listAll(location)
    }

    suspend fun getMetadata(): Map<String, String> {
        return client.getMetadata(location)
    }

    suspend fun get(): BlobInfoWrapper {
        return client.get(location)
    }

    suspend fun upload(
        blobInfo: BlobInfoWrapper,
        metadata: Map<String, String>? = null,
        contentType: String? = null
    ): BlobInfoWrapper {
        return client.upload(location, blobInfo, metadata, contentType)
    }

    suspend fun delete() {
        return client.delete(location)
    }

    companion object {
        fun of(bucket: String, path: String): Reference {
            return Reference(
                Location(
                    bucket = bucket,
                    path = path
                )
            )
        }
    }
}

@Serializable
data class UploadMetadata(
    val name: String,
    val contentType: String,
    val metadata: Map<String, String>? = null
)