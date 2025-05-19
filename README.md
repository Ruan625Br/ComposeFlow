# ComposeFlow

## Setting up your development environment

To set up the environment, please consult
these [instructions](https://github.com/JetBrains/compose-multiplatform-template#setting-up-your-development-environment).

## Prerequisites

You need to install the [JBR](https://github.com/JetBrains/JetBrainsRuntime) (JetBrain's Runtime) to run ComposeFlow since ComposeFlow depends
on [jewel](https://github.com/JetBrains/jewel)

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

`./gradlew --init-script gradle/init.gradle.kts --no-configuration-cache spotlessApply`

## How to generate a system prompt for Gemini

`bash generate_system_prompts.sh`

## API secrets for ID providers

Secrets for ID providers are retrieved from `local.properties` file.
Ask for the secrets or register a OAuth2 by
following https://support.google.com/cloud/answer/6158849?hl=en

```

google.client.id=<OAuth2 client id for Google>
google.client.secret=<OAuth2 client secret for Google>

```
