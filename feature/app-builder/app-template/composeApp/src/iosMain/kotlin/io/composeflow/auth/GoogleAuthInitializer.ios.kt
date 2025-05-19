package io.composeflow.auth

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

actual fun initializeGoogleAuthProvider(credentials: GoogleAuthCredentials) {
    GoogleAuthProvider.create(credentials = credentials)
}