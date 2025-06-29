package io.composeflow.model.project.firebase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("FirebaseAppInfoHolder")
data class FirebaseAppInfoHolder(
    var firebaseAppInfo: FirebaseAppInfo = FirebaseAppInfo(),
)
