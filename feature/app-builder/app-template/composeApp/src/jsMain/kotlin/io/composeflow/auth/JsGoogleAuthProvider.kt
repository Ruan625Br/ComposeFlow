package io.composeflow.auth

import androidx.compose.runtime.Composable
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.google.GoogleAuthUiProvider
import com.mmk.kmpauth.google.GoogleUser
import dev.gitlive.firebase.auth.externals.getAuth
import dev.gitlive.firebase.auth.externals.signInWithPopup
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class JsGoogleAuthProvider : GoogleAuthProvider {

    @Composable
    override fun getUiProvider(): GoogleAuthUiProvider = JsGoogleAuthUiProvider()

    override suspend fun signOut() {
        getAuth().signOut()
    }
}

class JsGoogleAuthUiProvider : GoogleAuthUiProvider {

    override suspend fun signIn(): GoogleUser? = suspendCoroutine { continuation ->
        val auth = getAuth()
        val googleAuthProvider = dev.gitlive.firebase.auth.externals.GoogleAuthProvider()
        signInWithPopup(auth, googleAuthProvider).then { userCredential ->
            val oauthCredential =
                dev.gitlive.firebase.auth.externals.GoogleAuthProvider.credentialFromResult(
                    userCredential
                )
            val idToken = oauthCredential?.idToken
            val accessToken = oauthCredential?.accessToken
            if (idToken != null && accessToken != null) {
                val googleUser = GoogleUser(
                    idToken = idToken,
                    accessToken = accessToken,
                    displayName = userCredential.user.displayName ?: "",
                    profilePicUrl = userCredential.user.photoURL
                )
                continuation.resume(googleUser)
            } else {
                continuation.resume(null)
            }
        }
    }
}

fun composeFlowGoogleAuthModule(credentials: GoogleAuthCredentials) = module {
    factory { credentials }
    includes(googleAuthPlatformModule)
}

private val googleAuthPlatformModule: Module = module {
    factoryOf(::JsGoogleAuthProvider) bind GoogleAuthProvider::class
}