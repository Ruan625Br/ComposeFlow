package io.composeflow.materialicons

import androidx.compose.ui.graphics.vector.ImageVector
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("MaterialIcon")
enum class MaterialIcon {
    Outlined,
    Filled,
    Rounded,
    Sharp,
    TwoTone,
}

@Serializable
@SerialName("ImageVectorHolder")
sealed interface ImageVectorHolder {
    val imageVector: ImageVector
    val name: String
    val packageDescriptor: String
    val memberDescriptor: String
}

fun ImageVectorHolder.asCodeBlock(): CodeBlock =
    CodeBlock.of(
        """%M.$memberDescriptor.%M""",
        MemberName("androidx.compose.material.icons", "Icons"),
        MemberName("androidx.compose.material.icons.$packageDescriptor", name),
    )
