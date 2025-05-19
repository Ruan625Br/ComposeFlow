package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.ClassName
import io.composeflow.model.project.COMPOSEFLOW_PACKAGE

object ClassHolder {
    object AndroidX {
        object Lazy {
            val GridCells = ClassName("androidx.compose.foundation.lazy.grid", "GridCells")
        }

        object Ui {
            val Modifier = ClassName("androidx.compose.ui", "Modifier")
            val Color = ClassName("androidx.compose.ui.graphics", "Color")
        }
    }

    object Kotlin {
        val Boolean = ClassName("kotlin", "Boolean")
        val Int = ClassName("kotlin", "Int")
        val Float = ClassName("kotlin", "Float")
        val Long = ClassName("kotlin", "Long")
        val String = ClassName("kotlin", "String")
    }

    object Kotlinx {
        object DateTime {
            val Clock = ClassName("kotlinx.datetime", "Clock")
        }

        object Serialization {
            val JsonElement = ClassName("kotlinx.serialization.json", "JsonElement")
            val JsonArray = ClassName("kotlinx.serialization.json", "JsonArray")
            val JsonObject = ClassName("kotlinx.serialization.json", "JsonObject")
            val JsonPrimitive = ClassName("kotlinx.serialization.json", "JsonPrimitive")
            val Serializable = ClassName("kotlinx.serialization", "Serializable")
        }
    }

    object ComposeFlow {
        val DataResult = ClassName("$COMPOSEFLOW_PACKAGE.model", "DataResult")
        val EventResultState = ClassName("$COMPOSEFLOW_PACKAGE.model", "EventResultState")
    }

    object Collections {
        val List = ClassName("kotlin.collections", "List")
    }

    object Coroutines {
        object Flow {
            val StateFlow = ClassName("kotlinx.coroutines.flow", "StateFlow")
            val MutableStateFlow = ClassName("kotlinx.coroutines.flow", "MutableStateFlow")
        }
    }
}
