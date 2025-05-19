package io.composeflow.firebase.management

import io.composeflow.firebase.IosAppMetadata
import kotlinx.serialization.Serializable

@Serializable
data class IosAppWrapper(
    val metadata: IosAppMetadata,
    val config: String,
) {
    fun buildUrl(): String = metadata.buildUrl()
}
