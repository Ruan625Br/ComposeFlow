package packageName

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import io.composeflow.AppInitializer
import io.composeflow.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

private const val USER_PREFERENCES_NAME = "user_preferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
)

class MainApplication : Application() {
    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    override fun onCreate() {
        super.onCreate()

        AppInitializer.onApplicationStart()
        initKoin {
            androidContext(this@MainApplication)
        }
        GlobalContext.get().declare<FlowSettings>(DataStoreSettings(applicationContext.dataStore))
    }
}
