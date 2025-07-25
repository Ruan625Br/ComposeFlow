package io.composeflow.model.parameter.wrapper

import androidx.compose.ui.Alignment
import io.composeflow.serializer.LocationAwareFallbackEnumSerializer
import kotlinx.serialization.Serializable

object AlignmentHorizontalWrapperSerializer :
    LocationAwareFallbackEnumSerializer<AlignmentHorizontalWrapper>(AlignmentHorizontalWrapper::class)

@Serializable(AlignmentHorizontalWrapperSerializer::class)
enum class AlignmentHorizontalWrapper(
    val alignment: Alignment.Horizontal,
) {
    Start(Alignment.Start),
    CenterHorizontally(Alignment.CenterHorizontally),
    End(Alignment.End),
}
