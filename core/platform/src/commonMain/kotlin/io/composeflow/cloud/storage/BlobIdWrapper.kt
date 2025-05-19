package io.composeflow.cloud.storage

import com.google.cloud.storage.BlobId
import kotlinx.serialization.Serializable

@Serializable
data class BlobIdWrapper(
    val bucket: String,
    val name: String,
    val generation: Long?,
)

fun BlobId.toKotlinWrapper(): BlobIdWrapper = BlobIdWrapper(
    bucket = bucket,
    name = name,
    generation = generation,
)