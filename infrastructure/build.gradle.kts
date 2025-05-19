
plugins {
    alias(infrastructureLibs.plugins.kotlin.jvm)
    application
}

group = "io.composeflow"
version = "0.0.1"

application {
    mainClass.set("io.composeflow.MainKt")
}

dependencies {
    implementation(infrastructureLibs.cdktf)
    implementation(infrastructureLibs.cdktf.provider.google)
    implementation(infrastructureLibs.cdktf.provider.google.beta)
    implementation(infrastructureLibs.constructs)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
