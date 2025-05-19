package io.composeflow.model.parameter.lazylist

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.squareup.kotlinpoet.CodeBlock
import io.composeflow.kotlinpoet.ClassHolder
import io.composeflow.kotlinpoet.MemberHolder
import io.composeflow.serializer.DpSerializer
import io.composeflow.ui.propertyeditor.DropdownItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LazyGridCells")
sealed interface LazyGridCells : DropdownItem {

    fun asCodeBlock(): CodeBlock
    fun asComposeGridCells(): GridCells

    @Serializable
    @SerialName("Adaptive")
    data class Adaptive(
        @Serializable(DpSerializer::class)
        val minSize: Dp = 100.dp,
    ) : LazyGridCells {
        @Composable
        override fun asDropdownText(): String = "Adaptive"
        override fun isSameItem(item: Any): Boolean = item is Adaptive
        override fun asCodeBlock(): CodeBlock {
            return CodeBlock.of(
                "%T.Adaptive(${minSize.value.toInt()}.%M)",
                ClassHolder.AndroidX.Lazy.GridCells,
                MemberHolder.AndroidX.Ui.dp
            )
        }

        override fun asComposeGridCells(): GridCells = GridCells.Adaptive(minSize)
    }

    @Serializable
    @SerialName("Fixed")
    data class Fixed(val count: Int = 2) : LazyGridCells {
        @Composable
        override fun asDropdownText(): String = "Fixed"
        override fun isSameItem(item: Any): Boolean = item is Fixed
        override fun asCodeBlock(): CodeBlock {
            return CodeBlock.of(
                "%T.Fixed(${count})",
                ClassHolder.AndroidX.Lazy.GridCells,
            )
        }

        override fun asComposeGridCells(): GridCells = GridCells.Fixed(count)
    }

    @Serializable
    @SerialName("FixedSize")
    data class FixedSize(
        @Serializable(DpSerializer::class)
        val size: Dp = 100.dp,
    ) : LazyGridCells {
        @Composable
        override fun asDropdownText(): String = "FixedSize"
        override fun isSameItem(item: Any): Boolean = item is FixedSize
        override fun asCodeBlock(): CodeBlock {
            return CodeBlock.of(
                "%T.FixedSize(${size.value.toInt()}.%M)",
                ClassHolder.AndroidX.Lazy.GridCells,
                MemberHolder.AndroidX.Ui.dp
            )
        }

        override fun asComposeGridCells(): GridCells = GridCells.FixedSize(size)
    }

    companion object {
        fun entries(): List<LazyGridCells> = listOf(
            Adaptive(),
            Fixed(),
            FixedSize(),
        )
    }
}