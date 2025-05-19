pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "ComposeFlow"

include(":desktopApp")
include(":generate-jsonschema-cli")
include(":core:ai")
include(":core:config")
include(":core:di")
include(":core:formatter")
include(":core:icons")
include(":core:kxs-ts-gen-core")
include(":core:logger")
include(":core:model")
include(":core:platform")
include(":core:resources")
include(":core:serializer")
include(":core:testing")
include(":core:ui")
include(":core:billing-client")
include(":feature:api-editor")
include(":feature:app-builder")
include(":feature:appstate-editor")
include(":feature:firestore-editor")
include(":feature:asset-editor")
include(":feature:datatype-editor")
include(":feature:uibuilder")
include(":feature:theme-editor")
include(":feature:settings")
include(":feature:top")


includeBuild("build-logic")
includeBuild("feature/app-builder/app-template")

includeBuild("server")
includeBuild("infrastructure")
