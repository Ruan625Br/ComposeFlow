package io.composeflow.serializer

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.composeflow.model.parameter.wrapper.shapeWrapperModule
import kotlinx.serialization.modules.SerializersModule

val composeflowModule =
    SerializersModule {
        include(shapeWrapperModule)
    }

val yamlSerializer =
    Yaml(
        configuration =
            YamlConfiguration(
                strictMode = false,
                breakScalarsAt = Int.MAX_VALUE,
                encodeDefaults = false,
            ),
        serializersModule = composeflowModule,
    )
