plugins {
    id("io.compose.flow.kmp.library")
    id("io.compose.flow.compose.multiplatform")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:config"))
            implementation(project(":core:serializer"))

            api(project.dependencies.platform(libs.google.cloud.bom))
            api(libs.okhttp)

            api(libs.compose.adaptive)
            api(libs.compose.adaptive.layout)
            api(libs.compose.adaptive.navigation)
            implementation(libs.kotlin.result)
        }
        commonTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}

