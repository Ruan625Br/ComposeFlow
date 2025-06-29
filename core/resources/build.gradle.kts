plugins {
    id("io.compose.flow.kmp.library")
    id("org.jetbrains.compose")
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvm("desktop")
    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(libs.kotlinpoet)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.composeflow"
    generateResClass = always
}
