package io.composeflow.model.parameter.wrapper

import androidx.compose.ui.graphics.TileMode
import io.composeflow.serializer.LocationAwareFallbackEnumSerializer
import kotlinx.serialization.Serializable

object TileModeWrapperSerializer :
    LocationAwareFallbackEnumSerializer<TileModeWrapper>(TileModeWrapper::class)

@Serializable(TileModeWrapperSerializer::class)
enum class TileModeWrapper {
    Clamp,
    Repeated,
    Mirror,
    Decal,
}

fun TileModeWrapper.toTileMode(): TileMode =
    when (this) {
        TileModeWrapper.Clamp -> TileMode.Clamp
        TileModeWrapper.Repeated -> TileMode.Repeated
        TileModeWrapper.Mirror -> TileMode.Mirror
        TileModeWrapper.Decal -> TileMode.Decal
    }

fun TileMode.toTileModeWrapper(): TileModeWrapper =
    when (this) {
        TileMode.Clamp -> TileModeWrapper.Clamp
        TileMode.Repeated -> TileModeWrapper.Repeated
        TileMode.Mirror -> TileModeWrapper.Mirror
        TileMode.Decal -> TileModeWrapper.Decal
        else -> TileModeWrapper.Clamp // fallback for unknown modes
    }
