package io.composeflow.firebase.management

import io.composeflow.firebase.AndroidAppMetadata
import kotlinx.serialization.Serializable

@Serializable
data class AndroidAppWrapper(
    val metadata: AndroidAppMetadata,
    val config: String,
) {
    fun buildUrl(): String = metadata.buildUrl()
}
