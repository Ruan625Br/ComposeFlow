package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.MemberName
import io.composeflow.model.project.COMPOSEFLOW_PACKAGE

/**
 * Utility objects that hold MemberName for KotlinPoet to avoid same MemberName are repeatedly
 * defined.
 */
object MemberHolder {

    object AndroidX {
        object Foundation {
            val Image = MemberName("androidx.compose.foundation", "Image")
            val background =
                MemberName("androidx.compose.foundation", "background", isExtension = true)
        }

        object Lazy {
            val items = MemberName("androidx.compose.foundation.lazy", "items")
            val itemsIndexed = MemberName("androidx.compose.foundation.lazy", "itemsIndexed")
            val gridItemsIndexed =
                MemberName("androidx.compose.foundation.lazy.grid", "itemsIndexed")
        }

        object Ui {
            val dp = MemberName("androidx.compose.ui.unit", "dp", isExtension = true)
            val sp = MemberName("androidx.compose.ui.unit", "sp", isExtension = true)
            val Alignment = MemberName("androidx.compose.ui", "Alignment")
            val Color = MemberName("androidx.compose.ui.graphics", "Color")
            val Dialog = MemberName("androidx.compose.ui.window", "Dialog")
            val FontWeight = MemberName("androidx.compose.ui.text.font", "FontWeight")
            val Modifier = MemberName("androidx.compose.ui", "Modifier")
        }

        object Layout {
            val Arrangement = MemberName("androidx.compose.foundation.layout", "Arrangement")
            val Column = MemberName("androidx.compose.foundation.layout", "Column")
            val Row = MemberName("androidx.compose.foundation.layout", "Row")
            val Box = MemberName("androidx.compose.foundation.layout", "Box")
            val Spacer = MemberName("androidx.compose.foundation.layout", "Spacer")
            val align =
                MemberName("androidx.compose.foundation.layout", "align", isExtension = true)
            val padding = MemberName("androidx.compose.foundation.layout", "padding")
            val size = MemberName("androidx.compose.foundation.layout", "size", isExtension = true)
            val width =
                MemberName("androidx.compose.foundation.layout", "width", isExtension = true)
        }

        object Platform {
            val LocalUriHandler = MemberName("androidx.compose.ui.platform", "LocalUriHandler")
        }

        object Runtime {
            val collectAsState = MemberName("androidx.compose.runtime", "collectAsState")
            val LaunchedEffect = MemberName("androidx.compose.runtime", "LaunchedEffect")
            val mutableStateOf = MemberName("androidx.compose.runtime", "mutableStateOf")
            val remember = MemberName("androidx.compose.runtime", "remember")
            val rememberSaveable =
                MemberName("androidx.compose.runtime.saveable", "rememberSaveable")
            val rememberCoroutineScope =
                MemberName("androidx.compose.runtime", "rememberCoroutineScope")
        }
    }

    object AppCash {
        val collectAsLazyPagingItems =
            MemberName("app.cash.paging.compose", "collectAsLazyPagingItems")
    }

    object ComposeFlow {
        val asErrorMessage =
            MemberName("${COMPOSEFLOW_PACKAGE}.validator", "asErrorMessage", isExtension = true)
        val isSuccess =
            MemberName("${COMPOSEFLOW_PACKAGE}.validator", "isSuccess", isExtension = true)
        val ValidateResult = MemberName("${COMPOSEFLOW_PACKAGE}.validator", "ValidateResult")
        val LocalAuthenticatedUser =
            MemberName("${COMPOSEFLOW_PACKAGE}.auth", "LocalAuthenticatedUser")
        val LocalOnShowsnackbar = MemberName("${COMPOSEFLOW_PACKAGE}.ui", "LocalOnShowSnackbar")
        val Res = MemberName(COMPOSEFLOW_PACKAGE, "Res")

        object String {
            val cancel = MemberName(COMPOSEFLOW_PACKAGE, "cancel", isExtension = true)
            val confirm = MemberName(COMPOSEFLOW_PACKAGE, "confirm", isExtension = true)
        }
    }

    object Coroutines {
        val launch = MemberName("kotlinx.coroutines", "launch")

        object Flow {
            val collectLatest = MemberName("kotlinx.coroutines.flow", "collectLatest")
            val first = MemberName("kotlinx.coroutines.flow", "first")
            val map = MemberName("kotlinx.coroutines.flow", "map")
            val mapLatest = MemberName("kotlinx.coroutines.flow", "mapLatest")
            val stateIn = MemberName("kotlinx.coroutines.flow", "stateIn")
            val SharingStarted = MemberName("kotlinx.coroutines.flow", "SharingStarted")
        }
    }

    object DateTime {
        val format = MemberName("kotlinx.datetime", "format", isExtension = true)
        val plus = MemberName("kotlinx.datetime", "plus", isExtension = true)
        val toLocalDateTime = MemberName("kotlinx.datetime", "toLocalDateTime", isExtension = true)
        val DateTimeUnit = MemberName("kotlinx.datetime", "DateTimeUnit")
        val Instant = MemberName("kotlinx.datetime", "Instant")
        val LocalDateTime = MemberName("kotlinx.datetime", "LocalDateTime")
        val MonthNames = MemberName("kotlinx.datetime.format", "MonthNames")
        val Padding = MemberName("kotlinx.datetime.format", "Padding")
        val TimeZone = MemberName("kotlinx.datetime", "TimeZone")
    }

    object JetBrains {
        val imageResource = MemberName("org.jetbrains.compose.resources", "imageResource")
        val stringResource = MemberName("org.jetbrains.compose.resources", "stringResource")
        val vectorResource = MemberName("org.jetbrains.compose.resources", "vectorResource")
    }

    object Koin {
        val inject = MemberName("org.koin.core.component", "inject")
        val get = MemberName("org.koin.core.component", "get")
    }

    object Kotlin {
        object Collection {
            val sorted = MemberName("kotlin.collections", "sorted", isExtension = true)
            val sortedBy = MemberName("kotlin.collections", "sortedBy", isExtension = true)
            val isNotEmpty = MemberName("kotlin.collections", "isNotEmpty", isExtension = true)
            val toMutableList =
                MemberName("kotlin.collections", "toMutableList", isExtension = true)
        }
    }

    object Firebase {
        val Firebase = MemberName("dev.gitlive.firebase", "Firebase")
        val auth = MemberName("dev.gitlive.firebase.auth", "auth")
    }

    object Material3 {
        val Card = MemberName("androidx.compose.material3", "Card")
        val CircularProgressIndicator =
            MemberName("androidx.compose.material3", "CircularProgressIndicator")
        val DatePicker = MemberName("androidx.compose.material3", "DatePicker")
        val DatePickerDialog = MemberName("androidx.compose.material3", "DatePickerDialog")
        val MaterialTheme = MemberName("androidx.compose.material3", "MaterialTheme")
        val OutlinedButton = MemberName("androidx.compose.material3", "OutlinedButton")
        val rememberDatePickerState =
            MemberName("androidx.compose.material3", "rememberDatePickerState")
        val rememberTimePickerState =
            MemberName("androidx.compose.material3", "rememberTimePickerState")
        val Icon = MemberName("androidx.compose.material3", "Icon")
        val SelectableDates = MemberName("androidx.compose.material3", "SelectableDates")
        val Text = MemberName("androidx.compose.material3", "Text")
        val TextButton = MemberName("androidx.compose.material3", "TextButton")
        val TimePicker = MemberName("androidx.compose.material3", "TimePicker")
        val Typography = MemberName("androidx.compose.material3", "Typography")
    }

    object PreCompose {
        // Using collectAsState doesn't make the js app work probably because the expected lifecycle events aren't sent as in mobile
        // Use AndroidX.Runtime.collectAsState instead
//        val collectAsStateWithLifeCycle = MemberName("moe.tlaster.precompose.flow", "collectAsStateWithLifecycle")
        val viewModelScope = MemberName("moe.tlaster.precompose.viewmodel", "viewModelScope")
        val koinViewModel = MemberName("moe.tlaster.precompose.koin", "koinViewModel")
    }

    object Serialization {
        val encodeToString = MemberName("kotlinx.serialization", "encodeToString")
    }

    object Settings {
        val getBooleanFlow = MemberName("com.russhwolf.settings.coroutines", "getBooleanFlow")
        val getFloatFlow = MemberName("com.russhwolf.settings.coroutines", "getFloatFlow")
        val getIntFlow = MemberName("com.russhwolf.settings.coroutines", "getIntFlow")
        val getLongFlow = MemberName("com.russhwolf.settings.coroutines", "getLongFlow")
        val getStringFlow = MemberName("com.russhwolf.settings.coroutines", "getStringFlow")
    }
}
