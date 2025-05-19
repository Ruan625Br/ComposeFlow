plugins {
    id("io.compose.flow.kmp.library")
    alias(libs.plugins.kotlin.serialization)
    id("io.compose.flow.compose.multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:serializer"))
            implementation(compose.materialIconsExtended)
            implementation(libs.kaml)
            implementation(libs.kotlinpoet)
            implementation(libs.kotlinx.serialization.jsonpath)
        }
    }
}

