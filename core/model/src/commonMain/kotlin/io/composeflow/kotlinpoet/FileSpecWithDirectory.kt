package io.composeflow.kotlinpoet

import com.squareup.kotlinpoet.FileSpec

data class FileSpecWithDirectory(
    val fileSpec: FileSpec,
    val baseDirectory: BaseDirectory = BaseDirectory.CommonMainKotlin,
)

enum class BaseDirectory(
    val directoryName: String,
) {
    AndroidMain("composeApp/src/androidMain/"),
    AndroidMainKotlin("composeApp/src/androidMain/kotlin/"),
    AndroidMainRes("composeApp/src/androidMain/res/"),
    CommonMainKotlin("composeApp/src/commonMain/kotlin/"),
}
