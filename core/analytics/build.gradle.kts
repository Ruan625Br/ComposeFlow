plugins {
    id("io.compose.flow.kmp.library")
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(project(":core:di"))
        }
        jvmMain.dependencies {
            implementation(libs.posthog.java)
        }
    }
}
