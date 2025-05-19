package io.composeflow.model.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.composeflow.di.ServiceLocator
import io.composeflow.di.ServiceLocator.KeyDeafultDispatcher
import io.composeflow.json.jsonSerializer
import io.composeflow.platform.getEnvVar
import io.composeflow.platform.getOrCreateDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

class SettingsRepository(
    private val dataStore: DataStore<Preferences> = ServiceLocator.getOrPut { getOrCreateDataStore() },
    private val javaHomePathFromEnvVar: PathSetting.FromEnvVar? = getEnvVar("JAVA_HOME")?.let {
        PathSetting.FromEnvVar("JAVA_HOME", it)
    },
    dispatchers: CoroutineDispatcher = ServiceLocator.getOrPutWithKey(KeyDeafultDispatcher) { Dispatchers.Default },
) {
    private val scope: CoroutineScope = CoroutineScope(dispatchers)

    private val darkThemeKey = intPreferencesKey("dark_theme")
    private val appDarkThemeKey = intPreferencesKey("app_dark_theme")
    private val javaHomeKey = stringPreferencesKey("java_home")
    private val showBordersKey = booleanPreferencesKey("show_borders")
    private val versionAskedToUpdateKey = stringPreferencesKey("version_asked_to_update")

    val settings: Flow<ComposeBuilderSettings> = dataStore.data.map { preference ->
        ComposeBuilderSettings(
            composeBuilderDarkThemeSetting = DarkThemeSetting.fromOrdinal(
                preference[darkThemeKey] ?: DarkThemeSetting.Dark.ordinal,
            ),
            appDarkThemeSetting = DarkThemeSetting.fromOrdinal(
                preference[appDarkThemeKey] ?: DarkThemeSetting.System.ordinal,
            ),
            showBordersInCanvas = preference[showBordersKey] == true,
            javaHome = preference[javaHomeKey]?.let {
                jsonSerializer.decodeFromString<PathSetting>(it)
            } ?: javaHomePathFromEnvVar,
            versionAskedToUpdate = preference[versionAskedToUpdateKey]
        )
    }

    fun saveComposeBuilderDarkTheme(
        darkThemeSetting: DarkThemeSetting,
    ) {
        scope.launch {
            dataStore.edit {
                it[darkThemeKey] = darkThemeSetting.ordinal
            }
        }
    }

    val appDarkThemeSetting: Flow<Boolean> = settings.map {
        when (it.appDarkThemeSetting) {
            DarkThemeSetting.System -> false
            DarkThemeSetting.Light -> false
            DarkThemeSetting.Dark -> true
        }
    }

    fun saveAppDarkTheme(
        darkThemeSetting: DarkThemeSetting,
    ) {
        scope.launch {
            dataStore.edit {
                it[appDarkThemeKey] = darkThemeSetting.ordinal
            }
        }
    }

    fun saveShowBorders(
        showBorders: Boolean,
    ) {
        scope.launch {
            dataStore.edit {
                it[showBordersKey] = showBorders
            }
        }
    }

    fun saveJavaHomePath(javaHomePath: PathSetting) {
        scope.launch {
            dataStore.edit {
                it[javaHomeKey] = jsonSerializer.encodeToString(javaHomePath)
            }
        }
    }

    fun saveVersionAskedToUpdate(versionAskedToUpdate: String) {
        scope.launch {
            dataStore.edit {
                it[versionAskedToUpdateKey] = versionAskedToUpdate
            }
        }
    }
}
