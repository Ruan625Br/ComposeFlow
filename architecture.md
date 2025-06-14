# ComposeFlow Architecture

This document provides a comprehensive overview of ComposeFlow's architecture, explaining how each component fits together to create a visual UI builder for Compose Multiplatform applications.

## Overall architecture including server side

![Overall Architecture](https://github.com/user-attachments/assets/af0aa36e-fca5-4a43-ad5f-afc62a92b565)

ComposeFlow follows a modern cloud-native architecture with clear separation between client and server components:

### Client Side
- **ComposeFlow Desktop App**: The main visual UI builder application built with Compose Multiplatform and JetBrains Jewel components. It runs on desktop platforms and provides the drag-and-drop interface for building UIs.

### Server Side (Google Cloud Platform)
- **Cloud Run Services**: Three containerized services running on Google Cloud Run:
  - **ComposeFlow-Server**: Main backend service handling project management and authentication
  - **Stripe API Proxy**: Handles subscription management and payment processing
  - **LLM API Proxy**: Manages AI assistant features and routes requests to various LLM providers

- **Authentication**: Google OAuth2 serves as the primary authentication provider, ensuring secure user access

- **Storage**: 
  - **Cloud Storage**: Stores project files as YAML and custom assets (icons, images)
  - **Firestore**: Manages user data, project metadata, and subscription information

- **External Services**:
  - **OpenRouter**: API gateway for accessing multiple LLM providers (Claude, Gemini, OpenAI)
  - **Stripe**: Payment processing and subscription management
  - **Firebase**: Additional backend services and real-time features

### Data Flow
1. Users authenticate via Google OAuth2
2. Projects are serialized as YAML files and stored in Google Cloud Storage
3. Custom assets (icons, images) are uploaded alongside project files
4. The app communicates with backend services through secure API proxies
5. AI features are powered by multiple LLM providers through OpenRouter
6. Subscription status is managed through Stripe integration

## Client app

### Client module overview

![Module Architecture](https://github.com/user-attachments/assets/2f60c27a-5ffd-4f46-a51a-1e6216628714)

ComposeFlow follows a clean layered architecture with two types of modules:

#### Core Modules
Core modules contain specific features that can be used across multiple modules. They are organized into three layers:

**UI Layer** - User interface components and utilities:
- `core:ui` - Common UI components and utilities
- `core:icons` - Material icons and custom icon components
- `core:resources` - Fonts, strings, and other resources

**Domain Layer** - Business logic and data models:
- `core:model` - Data models, interfaces, and business logic

**Data Layer** - Platform services and data persistence:
- `core:serializer` - YAML and JSON serialization
- `core:platform` - Platform-specific implementations
- `core:config` - Configuration management
- `core:logger` - Logging utilities
- `core:formatter` - Code formatting and syntax highlighting
- `core:di` - Dependency injection using custom ServiceLocator
- `core:ai` - AI/LLM integration
- `core:billing-client` - Billing and subscription management

#### Feature Modules
Feature modules typically represent screens or major features. They cannot be shared across other feature modules to maintain clear boundaries:

- `feature:uibuilder` - Main UI builder with drag-and-drop canvas
- `feature:app-builder` - App generation and export functionality
- `feature:datatype-builder` - Custom data type creation
- `feature:settings` - Application settings and preferences
- `feature:theme-editor` - Theme customization
- `feature:api-editor` - API endpoint configuration
- `feature:firestore-editor` - Firestore database integration
- `feature:asset-editor` - Asset management
- `feature:appstate-editor` - Application state management
- `feature:top` - Top-level navigation and app shell

### Module Dependencies
The architecture enforces a strict dependency hierarchy:
- Feature modules can depend on core modules
- Core modules in higher layers can depend on lower layer core modules
- Feature modules cannot depend on other feature modules
- This ensures a clean, maintainable architecture without circular dependencies

## Model overview

![Model Architecture](https://github.com/user-attachments/assets/fc5a813b-01b5-4303-a80a-f47e1563b548)

The model layer forms the heart of ComposeFlow's domain logic. All models are Kotlin data classes marked with `@Serializable` for easy persistence and network transmission.

### Key Model Categories

#### Project Models (`model.project`)
- **Project**: Root model containing all project data
- **Screen**: Individual app screens with UI hierarchy
- **ScreenHolder**: Manages multiple screens in a project

#### UI Component Models (`model.parameter`)
- **ComposeNode**: Base model for UI components
- **Trait Classes**: Specific component types (ButtonTrait, TextTrait, etc.)
- Each trait defines:
  - Visual properties
  - Constraints and validation rules
  - Code generation logic
  - Canvas rendering behavior

#### Property System (`model.property`)
- **PropertyContainer**: Manages component properties
- **AssignableProperty**: Properties that can be bound to state
- **PropertyTransformer**: Converts between UI and code representations

#### State Management (`model.state`)
- **State**: Application state definitions
- **StateHolder**: Manages state across screens
- **StateOperation**: State mutations and transformations

#### Supporting Models
- **Action**: User interactions and navigation
- **DataType**: Custom data type definitions
- **ColorScheme**: Theme and styling
- **ModifierWrapper**: Compose modifiers with visual editing

### Serialization
All models use kotlinx.serialization with YAML as the primary format:
- Human-readable project files
- Easy version control integration
- Efficient storage and transmission
- Custom serializers for complex types

## State Management

ComposeFlow provides a comprehensive state management system that enables dynamic behavior in applications. The system supports both global application states and local screen states, with automatic code generation for Compose Multiplatform applications.

For detailed information about state management, see the [ComposeFlow State Management documentation](https://docs.composeflow.io/basics/state_management/).

### State Architecture

The state management system is built on a hierarchy of interfaces and implementations:

#### State Types
ComposeFlow supports three categories of states:

1. **App States** - Global states accessible throughout the application:
   - Primitive types: String, Int, Float, Boolean, Instant
   - List types: StringList, IntList, FloatList, BooleanList
   - Custom data types defined in the project
   - Persisted across app sessions using platform preferences

2. **Screen States** - Local states scoped to individual screens:
   - String, Float, Boolean, Instant, StringList
   - Held in memory using MutableStateFlow
   - Reset when navigating away from the screen

3. **Authenticated User States** - Built-in states for user information:
   - IsSignedIn, DisplayName, Email, PhoneNumber, PhotoUrl, IsAnonymous
   - Automatically populated when authentication is configured

#### StateHolder System
The `StateHolder` interface provides state management capabilities at different levels:
- **Global**: Project-level states managed by the root Project instance
- **Screen**: Each screen maintains its own StateHolder for local states
- **Component**: Reusable components can encapsulate their own states

#### State Operations
States support various operations through the `StateOperation` interface:
- **Basic**: SetValue, ClearValue, ToggleValue (for booleans)
- **List Operations**: AddValue, RemoveFirstValue, RemoveLastValue, RemoveValueAtIndex, UpdateValueAtIndex
- **Custom Operations**: For user-defined data types

### Code Generation

ComposeFlow automatically generates proper Kotlin code for state management:

1. **ViewModel Generation**: Each screen with states generates a ViewModel class
2. **State Declaration**: App states use persistent storage, screen states use MutableStateFlow
3. **Compose Integration**: States are collected using `collectAsState()` for reactive UI updates
4. **Type Safety**: Strong typing maintained throughout with proper imports

### UI Integration

States integrate seamlessly with the visual builder:
- **Property Binding**: Any assignable property can be bound to a state
- **Visual Indicators**: State types and current values shown in the UI
- **State Selection**: Intuitive dialogs for selecting states to bind
- **Live Preview**: Changes to state values reflected immediately in the canvas

#### State Assignment Dialog
<img width="906" src="https://github.com/user-attachments/assets/52ed72e3-9a42-4aa5-bca2-5f0f76310dd1" alt="Dialog to set a state to a property" />

The state assignment dialog allows users to bind any assignable property to an existing state. Through the `AssignableProperty` system, properties can reference states which are then translated to state references in the generated code.

#### Visual Builder with State Binding
<img width="969" src="https://github.com/user-attachments/assets/6a82517b-dc57-4e66-bfa2-2fcfe3569ab3" alt="UiBuilder showing a Text component with an app state assigned" />

The visual builder clearly indicates when a component property is bound to a state, showing the state type and name directly in the UI. This provides immediate visual feedback about which properties are dynamic and state-driven.

## ComposeNode overview

![ComposeNode Architecture](https://github.com/user-attachments/assets/09b618bf-8ea2-48f0-a220-9a51fd96cda2)

ComposeNode is the fundamental building block of the UI hierarchy, representing individual Composable functions in the visual editor.

### Core Responsibilities

#### 1. Visual Representation
Each ComposeNode maps directly to a Composable function and defines:
- How it appears in the drag-and-drop palette
- How it renders on the design canvas
- Visual editing handles and resize behavior
- Property panels and configuration options

#### 2. Code Generation
ComposeNodes implement the `CodeConvertible` interface to generate:
- Kotlin code for the Composable function
- Proper imports and dependencies
- Modifier chains and parameters
- Event handlers and state bindings

#### 3. Hierarchy Management
ComposeNodes form a tree structure:
- Parent-child relationships
- Constraints on what can contain what
- Layout-specific child ordering
- Drag-and-drop validation

### ComposeNode Types

#### Container Nodes
- **Column/Row**: Linear layouts with arrangement options
- **Box**: Overlapping content with alignment
- **LazyColumn/LazyRow**: Scrollable lists with performance optimization
- **Pager**: Swipeable page containers

#### Content Nodes
- **Text**: Styled text with typography options
- **Button**: Interactive buttons with various styles
- **Image**: Local and remote image display
- **TextField**: Text input with validation

#### Specialized Nodes
- **TopAppBar/BottomAppBar**: Navigation bars
- **NavigationDrawer**: Side navigation
- **Tabs**: Tab-based navigation
- **Component**: Reusable custom components

### Trait System
Each ComposeNode has an associated Trait that defines:
- Available properties and their types
- Default values and constraints
- Palette categories and visibility
- Platform-specific behavior

### Rendering Pipeline
1. **Canvas Rendering**: Traits provide custom rendering logic for the design canvas
2. **Property Editing**: Dynamic property panels based on trait definitions
3. **Live Preview**: Real-time updates as properties change
4. **Code Preview**: Generated code shown alongside visual design

## Interface between LLM

ComposeFlow integrates deeply with Large Language Models to enable AI-assisted UI building. The integration is achieved through a sophisticated tool generation system.

### Tool Generation System

The `@LlmTool` annotation system automatically generates JSON schemas for LLM function calling:

```kotlin
@LlmTool(
    name = "add_compose_node_to_container",
    description = "Adds a Compose UI component to a container node in the UI builder. This allows placing UI elements inside containers like Column, Row, or Box."
)
fun onAddComposeNodeToContainerNode(
    project: Project,
    @LlmParam(description = "The ID of the container node where the component will be added. Must be a node that can contain other components.")
    containerNodeId: String,
    @LlmParam(description = "The YAML representation of the ComposeNode node to be added to the container.")
    composeNodeYaml: String,
    @LlmParam(
        description = "The position index where the component should be inserted in the container.",
    )
    indexToDrop: Int,
)
```

### How It Works

#### 1. Annotation Processing
- **KSP Processor**: The `LlmToolProcessor` scans for `@LlmTool` annotations at compile time
- **Schema Generation**: Automatically generates JSON schemas compatible with OpenAI function calling format
- **Type Safety**: Preserves Kotlin type information in the generated schemas

#### 2. Shared Operations
Key operations in `UiBuilderOperator` are exposed to both:
- **Visual Editor**: Direct method calls from UI interactions
- **LLM Tools**: Same methods callable via AI assistant

This ensures consistency between manual and AI-assisted editing.

#### 3. Tool Categories

**UI Manipulation**:
- Add/remove components
- Update properties
- Rearrange hierarchy
- Apply modifiers

**Project Management**:
- Create screens
- Manage navigation
- Configure themes
- Set up data bindings

**Code Generation**:
- Generate component code
- Create custom components
- Export full applications

#### 4. YAML Bridge
The LLM interface uses YAML as the data format:
- Human-readable for LLM understanding
- Same format as project files
- Validated against model schemas
- Automatic serialization/deserialization

### Integration Architecture

1. **Client Request**: User asks AI assistant to perform a task
2. **LLM Processing**: AI selects appropriate tools and parameters
3. **Tool Execution**: ComposeFlow executes the tool with provided YAML
4. **Validation**: Changes are validated against constraints
5. **UI Update**: Canvas refreshes with new changes
6. **Feedback**: Results returned to LLM for confirmation

This architecture enables sophisticated AI assistance while maintaining the integrity and constraints of the visual editor.

## Build and Distribution

### Gradle Configuration
- Multi-module Gradle project with custom build logic
- Compose Multiplatform plugin for cross-platform UI
- Version catalogs for dependency management
- Spotless for code formatting

### Platform Support
- **Desktop**: Windows, macOS, Linux via JVM
- **Mobile**: Android and iOS (via app export)
- **Web**: JavaScript target (experimental)

### Distribution
- **Conveyor**: Cross-platform installer generation
- **GitHub Releases**: Direct downloads
- **App Stores**: Generated apps can be published

## Conclusion

ComposeFlow's architecture combines modern cloud services with a sophisticated client-side application to deliver a powerful visual UI building experience. The modular design, clean architecture, and AI integration make it extensible and maintainable while providing an intuitive interface for developers of all skill levels.