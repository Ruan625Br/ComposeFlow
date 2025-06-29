package io.composeflow.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.composeflow.di.ServiceLocator
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Path.Companion.toPath

object Factory {
    private lateinit var dataStore: DataStore<Preferences>

    private val lock = SynchronizedObject()

    /**
     * Gets the singleton DataStore instance, creating it if necessary.
     */
    fun getOrCreateDataStore(producePath: () -> String): DataStore<Preferences> =
        synchronized(lock) {
            if (Factory::dataStore.isInitialized) {
                dataStore
            } else {
                PreferenceDataStoreFactory
                    .createWithPath(
                        produceFile = { producePath().toPath() },
                        scope =
                            ServiceLocator.getOrPutWithKey(ServiceLocator.KeyIoDispatcherCoroutineScope) {
                                CoroutineScope(Dispatchers.IO + SupervisorJob())
                            },
                    ).also { dataStore = it }
            }
        }

    internal const val DATA_STORE_FILENAME = "compose.builder.preferences_pb"
}
