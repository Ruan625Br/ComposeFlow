package io.composeflow.auth

import dev.gitlive.firebase.auth.FirebaseUser

data class FirebaseUserWrapper(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoURL: String?,
    val isAnonymous: Boolean,
    val isEmailVerified: Boolean,
    val providerId: String,
)

fun FirebaseUser.toWrapper(): FirebaseUserWrapper =
    FirebaseUserWrapper(
        uid = this.uid,
        displayName = this.displayName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        photoURL = this.photoURL,
        isAnonymous = this.isAnonymous,
        isEmailVerified = this.isEmailVerified,
        providerId = this.providerId,
    )
