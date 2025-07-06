package io.composeflow

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.composeflow.kotlinpoet.MemberHolder
import kotlinx.serialization.json.Json

const val onScreenInitiallyLoaded = "onScreenInitiallyLoaded"
const val screenInitiallyLoadedFlag = "screenInitiallyLoadedFlag"

@Suppress("EnumEntryName")
enum class ComposeScreenConstant {
    arguments,
    onNavigateToRoute,
    onNavigateBack,
    coroutineScope,
    navDrawerState,
}

@Suppress("EnumEntryName")
enum class ViewModelConstant {
    flowSettings {
        @OptIn(ExperimentalSettingsApi::class)
        override fun generateProperty(): PropertySpec =
            PropertySpec
                .builder(flowSettings.name, FlowSettings::class)
                .addModifiers(KModifier.PRIVATE)
                .delegate(
                    CodeBlock
                        .builder()
                        .beginControlFlow("lazy")
                        .add("%M()", MemberHolder.Koin.get)
                        .endControlFlow()
                        .build(),
                ).build()
    },
    jsonSerializer {
        override fun generateProperty(): PropertySpec =
            PropertySpec
                .builder(jsonSerializer.name, Json::class)
                .addModifiers(KModifier.PRIVATE)
                .delegate(
                    CodeBlock
                        .builder()
                        .add("%M()", MemberHolder.Koin.inject)
                        .build(),
                ).build()
    },
    firestore {
        override fun generateProperty(): PropertySpec =
            PropertySpec
                .builder(firestore.name, FirebaseFirestore::class)
                .addModifiers(KModifier.PRIVATE)
                .delegate(
                    CodeBlock
                        .builder()
                        .add("%M()", MemberHolder.Koin.inject)
                        .build(),
                ).build()
    },
    ;

    abstract fun generateProperty(): PropertySpec
}
