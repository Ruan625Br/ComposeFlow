# ComposeFlow

**ComposeFlow** is a visual UI builder for Compose Multiplatform that allows developers to create
fully working applications through an intuitive drag-and-drop interface. It generates complete,
runnable Compose Multiplatform projects with clean Kotlin code and supports multiple platforms
including Desktop, Android, iOS, and Web.

![composeflow-ui-editor.png](/assets/composeflow-ui-editor.png)

## Key Features

- **Full App Generation**: Create complete, runnable Compose Multiplatform applications, not just UI
  components
- **Visual UI Builder**: Drag-and-drop interface for building complex UI layouts
- **AI-Powered Code Generation**: Built-in Claude AI integration for intelligent code assistance
- **Cross-Platform Support**: Generate apps for Desktop, Android, iOS, and Web platforms
- **Real-time Preview**: See your UI changes instantly with live preview
- **State Management**: Visual state management with automatic code generation
- **Component Library**: Rich set of pre-built UI components and modifiers
- **Theme Support**: Visual theme editor with Material Design integration
- **Project Export**: Export fully functional Compose Multiplatform projects ready to build and
  deploy

## Demo Videos

Create an initial project from a prompt

https://github.com/user-attachments/assets/4c59c4e6-e5d6-441d-a9c4-ce36b2b5e111

Edit the project within the editor with AI assistant

https://github.com/user-attachments/assets/b09cfa9c-7612-4a2e-9a3a-cfad3f4b454f

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

## Initialize submodule

Documents repository is added as a git submodule.

Update the documents by following command.

```bash
git submodule update --remote
```

## How to run

Choose a run configuration for an appropriate target in Android Studio and run it. You can either:

- Click the gutter icon next to the `./gradlew desktopApp:run` command in README
  ![run-from-readme.png](/assets/run-from-readme.png)

- Use the run configurations dropdown in the toolbar
  ![run-configurations-as.png](/assets/run-configuration-as.png)

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

