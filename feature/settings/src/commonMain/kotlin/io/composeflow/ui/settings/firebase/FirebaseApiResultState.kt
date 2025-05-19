package io.composeflow.ui.settings.firebase

import io.composeflow.firebase.WebAppWrapper
import io.composeflow.firebase.management.AndroidAppWrapper
import io.composeflow.firebase.management.IosAppWrapper

sealed interface FirebaseApiResultState {

    data object Initial : FirebaseApiResultState
    data object Loading : FirebaseApiResultState
    data class Success(val message: String = "Connected") : FirebaseApiResultState
    data class PartialSuccess(val message: String = "Partially connected") : FirebaseApiResultState
    data class Failure(val message: String = "Connection failed") : FirebaseApiResultState
}

data class FirebaseApiAppResultState(
    val androidApp: FirebaseAndroidAppApiResultState,
    val iOSApp: FirebaseIosAppApiResultState,
    val webApp: FirebaseWebAppApiResultState,
)

interface FirebaseApiAppResult {

    val isLoading: Boolean
    val failureMessage: String?
    val loadingMessage: String?
    fun isSuccess(): Boolean
    fun appDisplayName(): String?
    fun appBundleName(): String?
    fun firebaseConsoleUrl(): String?
}

data class FirebaseAndroidAppApiResultState(
    override val isLoading: Boolean = false,
    override val failureMessage: String? = null,
    override val loadingMessage: String? = null,
    private val androidApp: AndroidAppWrapper? = null,
) : FirebaseApiAppResult {

    override fun isSuccess(): Boolean = androidApp != null
    override fun appDisplayName(): String? = androidApp?.metadata?.displayName
    override fun appBundleName(): String? = androidApp?.metadata?.packageName
    override fun firebaseConsoleUrl(): String? = androidApp?.buildUrl()
}

data class FirebaseIosAppApiResultState(
    override val isLoading: Boolean = false,
    override val failureMessage: String? = null,
    override val loadingMessage: String? = null,
    private val iOSApp: IosAppWrapper? = null,
) : FirebaseApiAppResult {
    override fun isSuccess(): Boolean = iOSApp != null
    override fun appDisplayName(): String? = iOSApp?.metadata?.displayName
    override fun appBundleName(): String? = iOSApp?.metadata?.bundleId
    override fun firebaseConsoleUrl(): String? = iOSApp?.buildUrl()
}

data class FirebaseWebAppApiResultState(
    override val isLoading: Boolean = false,
    override val failureMessage: String? = null,
    override val loadingMessage: String? = null,
    private val webApp: WebAppWrapper? = null,
) : FirebaseApiAppResult {
    override fun isSuccess(): Boolean = webApp != null
    override fun appDisplayName(): String? = webApp?.metadata?.displayName
    override fun appBundleName(): String? = null
    override fun firebaseConsoleUrl(): String? = webApp?.metadata?.buildUrl()
}
