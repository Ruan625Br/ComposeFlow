package io.composeflow.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import io.ktor.client.engine.js.Js
import org.koin.dsl.module
import io.composeflow.settings.asObservableSettings

@OptIn(ExperimentalSettingsApi::class)
actual fun platformModule() = module {
    single { Js.create() }
    single { StorageSettings().asObservableSettings().toFlowSettings() }
}
