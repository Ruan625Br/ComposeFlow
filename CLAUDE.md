# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ComposeFlow is a **Compose Multiplatform visual UI builder** that allows developers to create user interfaces through a drag-and-drop interface. It generates Kotlin code for Jetpack Compose applications and supports multiple platforms (Desktop, Android, iOS, Web).

## Essential Commands

### Development
```bash
./gradlew desktopApp:run                    # Run desktop app
./gradlew allTests                          # Run all tests  
./gradlew --init-script gradle/init.gradle.kts --no-configuration-cache spotlessApply  # Format code
```

### Build & Release
```bash
./gradlew :jvmJar -Prelease                 # Build release JAR
conveyor make copied-site                   # Build distributable (requires Conveyor)
```

### Server Development
```bash
cd server && ./gradlew run                  # Start backend server
```

### AI Schema Generation
```bash
bash scripts/generate_system_prompts.sh     # Generate AI system prompts
```

## Prerequisites

- **JBR (JetBrains Runtime)** - Required for Jewel components used in the desktop UI
- **Android SDK** - Set `sdk.dir` in `feature/app-builder/app-template/local.properties` (Windows only)
- **OAuth2 Credentials** - Configure in `local.properties`:
  ```
  google.client.id=<OAuth2 client id>
  google.client.secret=<OAuth2 client secret>
  ```

## Architecture

### Module Structure
- **`core/`** - Shared modules (ai, model, ui, platform, di, logger, config, formatter, icons, resources)
- **`feature/`** - Feature modules (uibuilder, app-builder, theme-editor, settings, api-editor, firestore-editor, etc.)
- **`desktopApp/`** - Desktop application entry point
- **`server/`** - Backend services (Ktor-based)
- **`infrastructure/`** - Cloud deployment (CDKTF/Terraform)
- **`build-logic/`** - Custom Gradle plugins

### Key Technologies
- **UI Framework:** Jetpack Compose Multiplatform + JetBrains Jewel
- **Navigation:** PreCompose
- **DI:** Custom ServiceLocator (plans to migrate to kotlin-inject)
- **Serialization:** kotlinx.serialization
- **HTTP:** Ktor client/server
- **Testing:** Roborazzi for screenshot testing
- **Code Generation:** KotlinPoet

### Build System
- Multi-module Gradle project with custom plugins
- Version catalogs in `gradle/libs.versions.toml`
- Composite builds for server, infrastructure, and app-template
- Spotless formatting with ktlint (configured in `gradle/init.gradle.kts`)

## Testing

### Test Execution
```bash
./gradlew allTests                          # All tests
./gradlew :desktopApp:test                  # Desktop app tests
./gradlew :core:model:test                  # Specific module tests
```

### Test Types
- **Unit Tests:** Standard Kotlin tests in `src/commonTest/`
- **Screenshot Tests:** Roborazzi tests for UI components
- **Integration Tests:** Custom test rules and robots in `desktopApp/src/jvmTest/`

### Test Configuration
- Roborazzi recording enabled by default (`roborazzi.test.record=true`)
- Custom test rules: `ScreenTestRule`, `DependencyInjectionRule`, `CoroutinesDispatcherRule`
- Test robots pattern used for complex UI testing

## Code Generation & AI Integration

### AI Features
- Built-in Claude integration for code assistance
- TypeScript schema generation for AI prompts (in `generate-jsonschema-cli/`)
- System prompt generation based on codebase structure

### Code Generation
- **KotlinPoet** for dynamic code generation
- **Template System** for app generation (`feature/app-builder/app-template/`)
- **TypeScript Schema Generation** for AI integration

## Development Workflow

1. **Setup:** Install JBR, configure `local.properties`
2. **Development:** Use `./gradlew desktopApp:run` for quick iteration
3. **Testing:** Run `./gradlew allTests` before commits
4. **Formatting:** Apply `spotlessApply` before submitting
5. **Release:** Use Conveyor for distribution builds

## Important Patterns

### Dependency Injection
- Custom `ServiceLocator` implementation in `core/di/`
- Platform-specific implementations using expect/actual
- Planned migration to kotlin-inject

### State Management
- PreCompose ViewModels for UI state
- Flow-based reactive programming
- DataStore for configuration persistence

### Platform Abstractions
- Common business logic in `commonMain/`
- Platform-specific code in respective sourcesets
- Expect/actual declarations for platform differences

### Modular Architecture
- Feature modules are independent and composable
- Core modules provide shared functionality
- Clean dependency graph with no circular dependencies
