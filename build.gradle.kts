plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("jvm") version libs.versions.kotlin apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("android") version libs.versions.kotlin apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://packages.jetbrains.team/maven/p/kpm/public/")
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
        maven("https://maven.pkg.jetbrains.space/public/p/intellij-dependencies/dev")
        maven("https://maven.pkg.jetbrains.space/public/p/kotlin/dev")
        maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/releases/") }
        mavenLocal()
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.plugin)
        classpath(libs.buildconfig.plugin)
    }
}
