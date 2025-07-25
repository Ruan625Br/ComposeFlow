package io.composeflow.model.parameter.wrapper

import androidx.compose.foundation.layout.Arrangement
import io.composeflow.serializer.LocationAwareFallbackEnumSerializer
import kotlinx.serialization.Serializable

object ArrangementVerticalWrapperSerializer :
    LocationAwareFallbackEnumSerializer<ArrangementVerticalWrapper>(ArrangementVerticalWrapper::class)

@Serializable(ArrangementVerticalWrapperSerializer::class)
enum class ArrangementVerticalWrapper(
    val arrangement: Arrangement.Vertical,
) {
    Top(Arrangement.Top),
    Center(Arrangement.Center),
    Bottom(Arrangement.Bottom),
    SpaceEvenly(Arrangement.SpaceEvenly),
    SpaceBetween(Arrangement.SpaceBetween),
    SpaceAround(Arrangement.SpaceAround),
}
