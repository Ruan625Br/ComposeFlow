plugins {
    id("io.compose.flow.kmp.library")
    kotlin("plugin.serialization")
    id("io.compose.flow.compose.multiplatform")
}

version = "1.0-SNAPSHOT"

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:di"))
            implementation(project(":core:icons"))
            implementation(project(":core:formatter"))
            implementation(project(":core:model"))
            implementation(project(":core:platform"))
            implementation(project(":core:serializer"))
            implementation(project(":core:ui"))
            implementation(project(":feature:api-editor"))
            implementation(project(":feature:app-builder"))
            implementation(project(":feature:appstate-editor"))
            implementation(project(":feature:firestore-editor"))
            implementation(project(":feature:asset-editor"))
            implementation(project(":feature:datatype-editor"))
            implementation(project(":feature:settings"))
            implementation(project(":feature:theme-editor"))
            implementation(libs.compose.code.editor)
            implementation(libs.compose.color.picker)
            implementation(libs.compose.shimmer)
            implementation(libs.datastore.core.okio)
            implementation(libs.datastore.preferences.core)
            implementation(libs.kotlinpoet)
            implementation(libs.ktor.core)
            implementation(libs.ktor.kotlinx.json)
            implementation(libs.kotlin.result)
            implementation(libs.kotlinx.serialization.jsonpath)
            implementation(libs.jewel.int.ui.standalone)
            implementation(libs.jewel.int.ui.decorated.window)
            implementation(libs.reorderable)
            implementation(libs.kaml)
            api(libs.precompose)
            api(libs.precompose.viewmodel)
        }

        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.common)
            }
        }

        commonTest.dependencies {
            implementation(project(":core:model"))
            implementation(project(":core:testing"))
            implementation(kotlin("test-junit"))
        }
        all {
            optInComposeExperimentalApis()
            optInKotlinExperimentalApis()
        }
    }
}

