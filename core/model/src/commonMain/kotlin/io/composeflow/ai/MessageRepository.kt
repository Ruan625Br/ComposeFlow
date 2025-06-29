package io.composeflow.ai

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.composeflow.di.ServiceLocator
import io.composeflow.platform.getOrCreateDataStore

class MessageRepository(
    private val dataStore: DataStore<Preferences> = ServiceLocator.getOrPut { getOrCreateDataStore() },
)
