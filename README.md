# ComposeFlow

## Setting up your development environment

To set up the environment, please consult
these [instructions](https://github.com/JetBrains/compose-multiplatform-template#setting-up-your-development-environment).

## Prerequisites

### JetBrain's Runtime

You need to install the [JBR](https://github.com/JetBrains/JetBrainsRuntime) (JetBrain's Runtime) to
run ComposeFlow since ComposeFlow depends
on [jewel](https://github.com/JetBrains/jewel)

### Android Studio or Intellij IDEA

To show the Previews for Composables, you need to use Android Studio (Narwhal 2025.1.1. or higher)
or Intellij IDEA (2025.1.1 or higheer)

#### Recommended plugins for IDE

* [ktlint](https://plugins.jetbrains.com/plugin/15057-ktlint)

### Prerequisites On Windows (Only Windows users need to follow this section)

You need to create a following file to run the tests successfully otherwise the gradle process in
tests is not able to detect the Android SdK location.

<Project-root>/feature/app-builder/app-template/local.properties

```
sdk.dir=<Path to Android SDK>
```

e.g.

```
sdk.dir=C:\\Users\\thagikura\\AppData\\Local\\Android\\Sdk
```

## How to run

Choose a run configuration for an appropriate target in Android Studio and run it.

![run-configurations.png](run-configurations.png)

## Run desktop via Gradle

`./gradlew desktopApp:run`

## How to run tests

`./gradlew allTests`

## Release build is distributed using [Conveyor](https://conveyor.hydraulic.dev/)

Install the conveyor first, then run (release key is needed to upload):

```bash
./gradlew :jvmJar -Prelease
conveyor make copied-site
```

## How to apply spotless

```sh
# Apply spotless to all modules in main project
./gradlew --init-script gradle/init.gradle.kts --no-configuration-cache --continue spotlessApply

# Apply spotless to build-logic and app-template projects
./gradlew --project-dir build-logic --init-script ../gradle/init.gradle.kts --no-configuration-cache --continue spotlessApply
./gradlew --project-dir feature/app-builder/app-template --init-script ../../../gradle/init.gradle.kts --no-configuration-cache --continue spotlessApply
```

## How to generate a system prompt for Gemini

`bash generate_system_prompts.sh`

