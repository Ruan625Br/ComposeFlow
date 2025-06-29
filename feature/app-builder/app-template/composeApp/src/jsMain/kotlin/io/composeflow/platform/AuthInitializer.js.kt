package io.composeflow.platform

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun onApplicationStartPlatformSpecific() {
    val options =
        FirebaseOptions(
            applicationId = "WEB_APPLICATION_ID", // e.g. 1:695411909814:web:2837b2925ba1fxxxxxxxx
            apiKey = "API_KEY", // e.g. AIzaSyXXXXXXXXXXXXXXXXX
            authDomain = "AUTH_DOMAIN", // kmp-auth-test.firebaseapp.com
            projectId = "PROJECT_ID", // kmp-auth-test
            storageBucket = "STORAGE_BUCKET", // kmp-auth-test.appspot.com
        )
    Firebase.initialize(options = options)
}
