plugins {
    id("io.compose.flow.kmp.library")
    id("io.compose.flow.compose.multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:platform"))
            api(libs.compose.adaptive)
            api(libs.compose.adaptive.layout)
            api(libs.compose.adaptive.navigation)
            implementation(libs.compose.image.loader)
            implementation(libs.compose.shimmer)
            implementation(libs.kotlin.result)
            implementation(libs.jewel.int.ui.standalone)
            implementation(libs.jewel.int.ui.decorated.window)
            api(libs.reorderable)
        }
        commonTest.dependencies {
            implementation(kotlin("test-junit"))
        }
    }
}
