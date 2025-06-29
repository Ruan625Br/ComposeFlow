package io.composeflow.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule() =
    module {
        single<FlowSettings> { NSUserDefaultsSettings(NSUserDefaults()).toFlowSettings() }
    }
