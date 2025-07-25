package io.composeflow.model.parameter.wrapper

import androidx.compose.ui.Alignment
import io.composeflow.serializer.LocationAwareFallbackEnumSerializer
import kotlinx.serialization.Serializable

object AlignmentVerticalWrapperSerializer :
    LocationAwareFallbackEnumSerializer<AlignmentVerticalWrapper>(AlignmentVerticalWrapper::class)

@Serializable(AlignmentVerticalWrapperSerializer::class)
enum class AlignmentVerticalWrapper(
    val alignment: Alignment.Vertical,
) {
    Top(Alignment.Top),
    CenterVertically(Alignment.CenterVertically),
    Bottom(Alignment.Bottom),
}
