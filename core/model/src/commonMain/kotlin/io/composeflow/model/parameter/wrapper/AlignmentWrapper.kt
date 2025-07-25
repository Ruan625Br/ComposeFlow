package io.composeflow.model.parameter.wrapper

import androidx.compose.ui.Alignment
import io.composeflow.serializer.LocationAwareFallbackEnumSerializer
import kotlinx.serialization.Serializable

object AlignmentWrapperSerializer :
    LocationAwareFallbackEnumSerializer<AlignmentWrapper>(AlignmentWrapper::class)

@Serializable(AlignmentWrapperSerializer::class)
enum class AlignmentWrapper(
    val alignment: Alignment,
) {
    TopStart(Alignment.TopStart),
    TopCenter(Alignment.TopCenter),
    TopEnd(Alignment.TopEnd),
    CenterStart(Alignment.CenterStart),
    Center(Alignment.Center),
    CenterEnd(Alignment.CenterEnd),
    BottomStart(Alignment.BottomStart),
    BottomCenter(Alignment.BottomCenter),
    BottomEnd(Alignment.BottomEnd),
    ;

    companion object {
        fun fromAlignment(alignment: Alignment?) = entries.firstOrNull { it.alignment == alignment } ?: TopStart
    }
}
