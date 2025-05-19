plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
    application
}

dependencies {
    implementation(project(":core:ai"))
    implementation(project(":core:model"))
    implementation(project(":core:kxs-ts-gen-core"))
    implementation(libs.kotlinx.serialization.json)
}

application {
    mainClass.set("io.composeflow.MainKt")
}

