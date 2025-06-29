package io.composeflow.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.composeflow.components.componentViewModelModule
import io.composeflow.screens.screenViewModelModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    val koinApplication =
        startKoin {
            appDeclaration()
            modules(
                commonModule(),
                platformModule(),
                screenViewModelModule(),
                componentViewModelModule(),
            )
        }
}

fun initKoin() = initKoin {}

fun commonModule() =
    module {
        single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
        single {
            Firebase.firestore
        }
    }
