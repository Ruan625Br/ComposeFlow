import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kotlin-multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            val kotlinMultiplatformExtension = extensions.getByType<KotlinMultiplatformExtension>()
            val compose = extensions.getByType<ComposeExtension>().dependencies
            with(kotlinMultiplatformExtension) {
                with(sourceSets) {
                    getByName("commonMain").apply {
                        dependencies {
                            implementation(project(":core:resources"))
                            implementation(compose.runtime)
                            implementation(compose.animation)
                            implementation(compose.foundation)
                            implementation(compose.material3)
                            implementation(compose.materialIconsExtended)
                            implementation(compose.ui)
                            implementation(compose.components.resources)
                            implementation(compose.components.uiToolingPreview)
                        }
                    }
                }
            }
        }
    }
}

// FIXME: We need to setup these optIns after sourceSets are created.
// so we have to do it as a extension function.
fun KotlinSourceSet.optInComposeExperimentalApis() {
    languageSettings {
        optIn("androidx.compose.foundation.ExperimentalFoundationApi")
        optIn("androidx.compose.foundation.layout.ExperimentalLayoutApi")
        optIn("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn("androidx.compose.ui.text.ExperimentalTextApi")
        optIn("androidx.compose.ui.ExperimentalComposeUiApi")
    }
}
