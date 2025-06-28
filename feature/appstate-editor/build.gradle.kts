plugins {
    id("io.compose.flow.kmp.library")
    id("io.compose.flow.compose.multiplatform")
    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

version = "1.0-SNAPSHOT"


kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":ksp-llm-tools"))
            implementation(project(":core:di"))
            implementation(project(":core:model"))
            implementation(project(":core:platform"))
            implementation(project(":core:serializer"))
            implementation(project(":core:ui"))
            implementation(libs.datastore.core.okio)
            implementation(libs.datastore.preferences.core)
            implementation(libs.jewel.int.ui.standalone)
            implementation(libs.kaml)
            implementation(libs.kotlin.result)
            implementation(libs.kotlinpoet)
            implementation(libs.precompose)
            implementation(libs.precompose.viewmodel)
        }

        commonTest.dependencies {
            implementation(project(":core:model"))
            implementation(kotlin("test-junit"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.desktop.uiTestJUnit4)
            implementation(compose.desktop.currentOs)
            implementation(libs.jewel.int.ui.standalone)
        }
        all {
            optInComposeExperimentalApis()
            optInKotlinExperimentalApis()
        }
    }
}

// Configure KSP for LLM tools
dependencies {
    add("kspDesktop", project(":ksp-llm-tools"))
}

// Configure KSP options
ksp {
    // Set output directory for LLM tool JSON files
    arg("llmToolsOutputDir", "${project.layout.buildDirectory.get()}/generated/llm-tools")
}

