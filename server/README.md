# ComposeFlow Server

This project provides backend processing for ComposeFlow, such as Google authentication.

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

- Google Auth

## Prerequisites

You need to set the following environment variables

```
export GOOGLE_CLOUD_PROJECT=<projectId>
export REGION=<region name>
export REPO_NAME=composeflow-server
```

e.g. 
```
export GOOGLE_CLOUD_PROJECT=composeflow
export REGION=us-central1
export REPO_NAME=composeflow-server
```

### Firebase Emulators

Set up the Firebase emulator.

```bash
npm install --global firebase-tools
firebase login
cd server
firebase init emulators

=== Emulators Setup
✔ Which Firebase emulators do you want to set up? Press Space to select emulators, then Enter to confirm your choices. Firestore Emulator
i  Port for firestore already configured: 8080
i  Emulator UI already enabled with port: (automatic)
✔ Would you like to download the emulators now? Yes

i  Writing configuration info to firebase.json...
i  Writing project information to .firebaserc...

✔  Firebase initialization complete!
```

## Building & Running

Require environments

```
export GOOGLE_CLIENT_ID=
export GOOGLE_CLIENT_SECRET=
export STRIPE_API_KEY=
```

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

