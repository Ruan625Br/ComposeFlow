package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.FileSpec

data class FileSpecWithDirectory(
    val fileSpec: FileSpec,
    val baseDirectory: BaseDirectory = BaseDirectory.CommonMainKotlin,
)

enum class BaseDirectory(
    val directoryName: String,
) {
    AndroidMainKotlin("composeApp/src/androidMain/kotlin/"),
    CommonMainKotlin("composeApp/src/commonMain/kotlin/"),
}
