package io.composeflow.platform

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun getOrCreateDataStore(): DataStore<Preferences>
