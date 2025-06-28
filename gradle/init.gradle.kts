val ktlintVersion = "1.6.0"

initscript {
    val spotlessVersion = "7.0.4"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion")
    }
}

rootProject {
    allprojects {
        apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
        extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                target(
                    "**/*.kt",
                    "**/*.kts",
                )
                targetExclude(
                    "**/build/**/*.kt",
                    "**/build/**/*.kts",
                    "**/*main*.kt",
                    "**/composeflowicons/*.kt",
                )
                ktlint(ktlintVersion)
            }
        }
    }
}
