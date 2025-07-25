package io.composeflow.model.project.firebase

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import io.composeflow.firebase.WebAppWrapper
import io.composeflow.firebase.management.AndroidAppWrapper
import io.composeflow.firebase.management.IosAppWrapper
import io.composeflow.serializer.LocationAwareFallbackMutableStateListSerializer
import io.composeflow.serializer.LocationAwareMutableStateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FirebaseAppInfo")
data class FirebaseAppInfo(
    val firebaseProjectId: String? = null,
    val androidApp: AndroidAppWrapper? = null,
    val iOSApp: IosAppWrapper? = null,
    val webApp: WebAppWrapper? = null,
    @Serializable(LocationAwareFallbackMutableStateListSerializer::class)
    val firestoreCollections: MutableList<FirestoreCollection> = mutableStateListOf(),
    @Serializable(LocationAwareMutableStateSerializer::class)
    val authenticationEnabled: MutableState<Boolean> = mutableStateOf(false),
) {
    fun getConnectedStatus(): FirebaseConnectedStatus =
        if (androidApp != null && iOSApp != null && webApp != null) {
            FirebaseConnectedStatus.Connected
        } else if (
            androidApp != null ||
            iOSApp != null ||
            webApp != null
        ) {
            FirebaseConnectedStatus.PartiallyConnected
        } else {
            FirebaseConnectedStatus.NotConnected
        }

    companion object {
        fun defaultAppDisplayName(projectName: String) = "ComposeFlow: $projectName"

        fun defaultWebAppDisplayName(projectName: String) = "ComposeFlow preview: $projectName"
    }
}

enum class FirebaseConnectedStatus {
    Connected,
    PartiallyConnected,
    NotConnected,
}
