plugins {
    id("io.compose.flow.kmp.library")
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.logback.classic)
            implementation(libs.logback.core)
            implementation(libs.slf4j.api)
        }
    }
}
