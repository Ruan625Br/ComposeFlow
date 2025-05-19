plugins {
    id("io.compose.flow.kmp.library")
    id("io.compose.flow.compose.multiplatform")
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.code.editor)
            implementation(libs.kotlinpoet)
            implementation(libs.ktlint.core)
            implementation(libs.ktlint.ruleset.standard)
        }
    }
}

