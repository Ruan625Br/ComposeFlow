package io.composeflow.model.parameter.wrapper

import androidx.compose.ui.Alignment
import io.composeflow.serializer.FallbackEnumSerializer
import kotlinx.serialization.Serializable

object AlignmentVerticalWrapperSerializer : FallbackEnumSerializer<AlignmentVerticalWrapper>(AlignmentVerticalWrapper::class)

@Serializable(AlignmentVerticalWrapperSerializer::class)
enum class AlignmentVerticalWrapper(
    val alignment: Alignment.Vertical,
) {
    Top(Alignment.Top),
    CenterVertically(Alignment.CenterVertically),
    Bottom(Alignment.Bottom),
    ;

    companion object {
        fun fromAlignment(alignment: Alignment.Vertical?) = entries.firstOrNull { it.alignment == alignment } ?: Top
    }
}
