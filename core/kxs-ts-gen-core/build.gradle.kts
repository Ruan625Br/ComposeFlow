plugins {
    id("io.compose.flow.kmp.library")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()

    sourceSets {
        configureEach {
            languageSettings {
                optIn("dev.adamko.kxstsgen.core.UnimplementedKxsTsGenApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }

        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("reflect"))
            }
        }
    }
}

