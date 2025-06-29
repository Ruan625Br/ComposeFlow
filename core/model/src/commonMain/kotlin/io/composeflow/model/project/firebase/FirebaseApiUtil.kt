package io.composeflow.model.project.firebase

import co.touchlab.kermit.Logger
import io.composeflow.auth.AuthRepository
import io.composeflow.auth.FirebaseIdToken
import io.composeflow.firebase.FirebaseAppIdentifier
import io.composeflow.firebase.management.firebaseScope
import io.composeflow.firebase.management.identityToolkitScope
import kotlinx.coroutines.flow.StateFlow

/**
 * Check if the required scope is satisfied in the FirebaseIdToken.
 * If not, let the user grants it by initiating the OAuth2 flow.
 */
suspend fun StateFlow<FirebaseIdToken?>.prepareFirebaseApiCall(
    firebaseProjectId: String,
    authRepository: AuthRepository,
    ignoreInsufficientScope: Boolean = false,
    executeApiCall: suspend (FirebaseAppIdentifier) -> Unit,
) {
    if (value?.googleTokenResponse?.hasSufficientScopes(
            firebaseScope,
            identityToolkitScope,
        ) != true
    ) {
        if (ignoreInsufficientScope) {
            Logger.i("Doesn't have sufficient scope, but ignoreInsufficientScope is true. Ignoring")
            return
        }

        // If the stored credential doesn't have the required scope, let the user grant it
        authRepository.startFirebaseManagementGrantInFlow()
        // After the user grants the scope, the firebaseIdToken will be updated
        collect { newFirebaseIdToken ->
            newFirebaseIdToken?.googleTokenResponse?.let {
                val identifier =
                    FirebaseAppIdentifier(
                        firebaseProjectId = firebaseProjectId,
                        googleTokenResponse = it,
                    )
                executeApiCall(identifier)
            }
        }
    } else {
        value?.googleTokenResponse?.let {
            val identifier =
                FirebaseAppIdentifier(
                    firebaseProjectId = firebaseProjectId,
                    googleTokenResponse = it,
                )
            executeApiCall(identifier)
        }
    }
}
