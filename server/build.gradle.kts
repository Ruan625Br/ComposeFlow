import io.ktor.plugin.features.DockerImageRegistry

plugins {
    alias(serverLibs.plugins.kotlin.jvm)
    alias(serverLibs.plugins.ktor)
    alias(serverLibs.plugins.kotlin.serialization)
}

group = "io.composeflow"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(serverLibs.ktor.server.core)
    implementation(serverLibs.ktor.server.auth)
    implementation(serverLibs.ktor.server.sessions)
    implementation(serverLibs.ktor.server.netty)
    implementation(serverLibs.ktor.server.forwarded.header)
    implementation(serverLibs.logback.classic)
    implementation(serverLibs.ktor.server.config.yaml)
    implementation(serverLibs.ktor.client.okhttp)
    implementation(serverLibs.ktor.client.negotiation)
    implementation(serverLibs.ktor.server.content.negotiation)
    implementation(serverLibs.kotlinx.serialization.json)
    implementation(serverLibs.kotlinx.serialization.jsonpath)
    implementation(serverLibs.ktor.kotlinx.json)
    implementation(serverLibs.stripe)
    implementation(serverLibs.firebase.admin)
    testImplementation(serverLibs.ktor.server.test.host)
    testImplementation(serverLibs.kotlin.test.junit)
}

private class ArtifactRegistry(
    projectName: Provider<String>,
    regionName: Provider<String>,
    repositoryName: Provider<String>,
    imageName: Provider<String>,
) : DockerImageRegistry {
    override val username: Provider<String> = provider { "" }
    override val password: Provider<String> = provider { "" }
    override val toImage: Provider<String> = provider {
        val region = regionName.get()
        val project = projectName.get()
        val repository = repositoryName.get()
        val image = imageName.get()
        "$region-docker.pkg.dev/$project/$repository/$image"
    }
}

val deployedImageTag: String = System.getenv("REPO_IMAGE_TAG") ?: "latest"

val deployedImageName = "composeflow/${project.name.lowercase()}"

private val artifactRegistryConfig = ArtifactRegistry(
    projectName = provider { System.getenv("GOOGLE_CLOUD_PROJECT") },
    regionName = provider { System.getenv("REGION") },
    repositoryName = provider { System.getenv("REPO_NAME") },
    imageName = provider { deployedImageName },
)

ktor {
    docker {
        jreVersion = JavaVersion.VERSION_21
        localImageName.set(deployedImageName)
        imageTag.set(deployedImageTag)
        externalRegistry.set(artifactRegistryConfig)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    val runWithEmulator by registering(Exec::class) {
        description = "Runs the Ktor server within the Firebase Emulator context"
        commandLine(
            "firebase",
            "emulators:exec",
            "--ui",
            "--import",
            "emulators-data",
            "--export-on-exit",
            "emulators-data",
            "../gradlew run",
        )
        standardInput = System.`in`
    }
}