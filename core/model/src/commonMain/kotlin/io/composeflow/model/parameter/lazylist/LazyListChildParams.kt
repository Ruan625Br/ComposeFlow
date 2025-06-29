package io.composeflow.model.parameter.lazylist

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.composeflow.model.parameter.ComposeTrait
import io.composeflow.model.project.Project
import io.composeflow.model.project.appscreen.screen.composenode.ComposeNode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val DefaultNumOfItems = 8

@Serializable
@SerialName("LazyListChildParams")
sealed interface LazyListChildParams {
    fun getSourceId(): String?

    @Serializable
    @SerialName("FixedNumber")
    data class FixedNumber(
        val numOfItems: Int = ComposeTrait.NumOfItemsInLazyList,
    ) : LazyListChildParams {
        override fun getNumOfItems(
            project: Project,
            lazyList: ComposeNode,
        ): Int = numOfItems

        override fun getSourceId(): String? = null
    }

    @Serializable
    @SerialName("DynamicItemsSource")
    data class DynamicItemsSource(
        val composeNodeId: String,
    ) : LazyListChildParams {
        override fun getSourceId(): String = composeNodeId

        override fun getNumOfItems(
            project: Project,
            lazyList: ComposeNode,
        ): Int = DefaultNumOfItems
    }

    fun boundsIncludingChildren(
        project: Project,
        lazyList: ComposeNode,
        self: ComposeNode,
    ): Rect {
        val childBottomRight = self.boundsInWindow.value.bottomRight
        return Rect(
            topLeft = self.boundsInWindow.value.topLeft,
            bottomRight =
                childBottomRight +
                    Offset(
                        x = 0f,
                        y =
                            (
                                getNumOfItems(
                                    project,
                                    lazyList,
                                ) - 1
                            ) * self.boundsInWindow.value.height,
                    ),
        )
    }

    fun getNumOfItems(
        project: Project,
        lazyList: ComposeNode,
    ): Int
}
