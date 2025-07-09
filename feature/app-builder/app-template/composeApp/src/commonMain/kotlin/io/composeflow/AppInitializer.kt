package io.composeflow

import com.mmk.kmpauth.google.GoogleAuthCredentials
import io.composeflow.auth.initializeGoogleAuthProvider
import io.composeflow.platform.onApplicationStartPlatformSpecific

object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        initializeGoogleAuthProvider(
            // e.g. client_id where client_type is 3 in google-services.json
            credentials = GoogleAuthCredentials(serverId = "OAUTH_CLIENT_ID"),
        )
    }
}
