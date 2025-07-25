package io.composeflow.model.parameter.wrapper

import androidx.compose.foundation.gestures.snapping.SnapPosition
import com.squareup.kotlinpoet.MemberName
import io.composeflow.serializer.FallbackEnumSerializer
import kotlinx.serialization.Serializable

object SnapPositionWrapperSerializer :
    FallbackEnumSerializer<SnapPositionWrapper>(SnapPositionWrapper::class)

@Serializable(SnapPositionWrapperSerializer::class)
enum class SnapPositionWrapper {
    Start {
        override fun toSnapPosition(): SnapPosition = SnapPosition.Start

        override fun toMemberName(): MemberName = MemberName("androidx.compose.foundation.gestures.snapping.SnapPosition", "Start")
    },
    Center {
        override fun toSnapPosition(): SnapPosition = SnapPosition.Center

        override fun toMemberName(): MemberName = MemberName("androidx.compose.foundation.gestures.snapping.SnapPosition", "Center")
    },
    End {
        override fun toSnapPosition(): SnapPosition = SnapPosition.End

        override fun toMemberName(): MemberName = MemberName("androidx.compose.foundation.gestures.snapping.SnapPosition", "End")
    },
    ;

    abstract fun toSnapPosition(): SnapPosition

    abstract fun toMemberName(): MemberName
}
