package io.composeflow.platform

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.composeflow.datastore.Factory
import io.composeflow.datastore.Factory.DATA_STORE_FILENAME

actual fun getOrCreateDataStore(): DataStore<Preferences> {
    return Factory.getOrCreateDataStore {
        getCacheDir().resolve(DATA_STORE_FILENAME).toPath().toString()
    }
}
