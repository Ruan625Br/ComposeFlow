package io.composeflow.model.parameter.wrapper

import androidx.compose.foundation.layout.Arrangement
import io.composeflow.serializer.FallbackEnumSerializer
import kotlinx.serialization.Serializable

object ArrangementHorizontalWrapperSerializer : FallbackEnumSerializer<ArrangementHorizontalWrapper>(ArrangementHorizontalWrapper::class)

@Serializable(ArrangementHorizontalWrapperSerializer::class)
enum class ArrangementHorizontalWrapper(val arrangement: Arrangement.Horizontal) {
    Start(Arrangement.Start),
    Center(Arrangement.Center),
    End(Arrangement.End),
    SpaceEvenly(Arrangement.SpaceEvenly),
    SpaceBetween(Arrangement.SpaceBetween),
    SpaceAround(Arrangement.SpaceAround),
    ;

    companion object {
        fun fromArrangement(arrangement: Arrangement.Horizontal?) =
            entries.firstOrNull { it.arrangement == arrangement } ?: Start
    }
}
