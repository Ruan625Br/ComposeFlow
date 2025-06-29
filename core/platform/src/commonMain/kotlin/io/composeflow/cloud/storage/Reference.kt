package io.composeflow.cloud.storage

import kotlinx.serialization.Serializable

class Reference(
    val location: Location,
    private val client: FirebaseStorageClient = FirebaseStorageClient(),
) {
    suspend fun listAll(): ListResult = client.listAll(location)

    suspend fun getMetadata(): Map<String, String> = client.getMetadata(location)

    suspend fun get(): BlobInfoWrapper = client.get(location)

    suspend fun upload(
        blobInfo: BlobInfoWrapper,
        metadata: Map<String, String>? = null,
        contentType: String? = null,
    ): BlobInfoWrapper = client.upload(location, blobInfo, metadata, contentType)

    suspend fun delete() = client.delete(location)

    companion object {
        fun of(
            bucket: String,
            path: String,
        ): Reference =
            Reference(
                Location(
                    bucket = bucket,
                    path = path,
                ),
            )
    }
}

@Serializable
data class UploadMetadata(
    val name: String,
    val contentType: String,
    val metadata: Map<String, String>? = null,
)
