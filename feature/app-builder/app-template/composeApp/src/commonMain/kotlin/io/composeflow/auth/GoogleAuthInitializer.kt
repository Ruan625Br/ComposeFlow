package io.composeflow.auth

import com.mmk.kmpauth.google.GoogleAuthCredentials

// Js target isn't supported by the kmpauth at the moment, thus, defining the JS implementation
// separately in the jsMain
expect fun initializeGoogleAuthProvider(credentials: GoogleAuthCredentials)