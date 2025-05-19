package io.composeflow.auth

import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.core.di.LibDependencyInitializer
import com.mmk.kmpauth.google.GoogleAuthCredentials

@OptIn(KMPAuthInternalApi::class)
actual fun initializeGoogleAuthProvider(credentials: GoogleAuthCredentials) {
    LibDependencyInitializer.initialize(composeFlowGoogleAuthModule(credentials))
}