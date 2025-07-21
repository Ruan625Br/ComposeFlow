import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    js(IR) {
        outputModuleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer =
                    (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static =
                            (static ?: mutableListOf()).apply {
                                // Serve sources to debug inside browser
                                add(project.rootDir.path)
                                add(project.rootDir.path + "/composeApp/commonMain/")
                                add(project.rootDir.path + "/composeApp/jsMain/")
                            }
                    }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.animation)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.compose.adaptive)
            implementation(libs.compose.adaptive.layout)
            implementation(libs.compose.adaptive.navigation)
            implementation(libs.compose.navigation)
            implementation(libs.compose.image.loader)
            implementation(libs.compose.shimmer)
            implementation(libs.kermit)
            implementation(libs.kmpauth.firebase)
            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.uihelper)
            implementation(libs.gitlive.firebase.auth)
            implementation(libs.gitlive.firebase.firestore)
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.result)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.jsonpath)
            implementation(libs.ktor.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.kotlinx.json)
            implementation(libs.paging.common)
            implementation(libs.paging.compose)
            implementation(libs.richeditor.compose)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.multiplatform.settings)
            api(libs.multiplatform.settings.serialization)
            api(libs.multiplatform.settings.coroutines)
            api(libs.precompose)
            api(libs.precompose.koin)
            api(libs.precompose.viewmodel)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.datastore.preferences)
            implementation(libs.datastore.preferences.core)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.multiplatform.settings.datastore)
            // workaround for https://youtrack.jetbrains.com/issue/CMP-5959/Invalid-redirect-in-window-core#focus=Comments-27-10365630.0-0
            implementation("androidx.window:window-core:1.3.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        all {
            languageSettings.optIn("androidx.compose.foundation.ExperimentalFoundationApi")
            languageSettings.optIn("androidx.compose.material3.ExperimentalMaterial3Api")
            languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsApi")
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "packageName"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "packageName.projectName"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    packaging {
        resources {
            excludes += "/META-INF/INDEX.LIST"
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

// a temporary workaround for a bug in jsRun invocation - see https://youtrack.jetbrains.com/issue/KT-48273
afterEvaluate {
    rootProject.extensions.configure<NodeJsRootExtension> {
        versions.webpackDevServer.version = "4.0.0"
        versions.webpackCli.version = "4.10.0"
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "io.composeflow"
}

// workaround for https://youtrack.jetbrains.com/issue/CMP-5959/Invalid-redirect-in-window-core#focus=Comments-27-10365630.0-0
configurations.configureEach {
    exclude("androidx.window.core", "window-core")
}
