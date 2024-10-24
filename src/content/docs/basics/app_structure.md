---
title: App structure
description: Explains what is the project in ComposeFlow
sidebar:
  order: 0
---

# App Structure in ComposeFlow

At the core of ComposeFlow's architecture is the **Project**. An app in ComposeFlow is structured within a project, which serves as the container for all the elements required to build and deploy your application. The project encapsulates everything from user interface designs to data models and external integrations.

---

## Project Structure

A ComposeFlow project comprises several critical components:

1. **Screens**
2. **App States**
3. **Data Types**
4. **External API Definitions**
5. **Assets and Resources**
6. **Settings and Configurations**

Each component plays a vital role in the development process. Below is a detailed explanation of each.

---

### 1. Screens

**Screens** are the visual building blocks of your application. They represent the different interfaces or pages that users interact with.

- **Design with Drag-and-Drop UI Builder**: ComposeFlow provides a user-friendly interface to design screens using a drag-and-drop UI builder.
- **Components Library**: Access a rich set of UI components like buttons, text fields, images, and more.
- **Layout Management**: Arrange components using layout managers to ensure responsiveness across different devices.
- **Navigation**: Define how users navigate between screens using navigation components.

**Example**:

- **Home Screen**: Displays a welcome message and main menu.
- **Profile Screen**: Allows users to view and edit their profile information.
- **Settings Screen**: Provides options to customize app preferences.

---

### 2. App States

**App States** manage the dynamic aspects of your application, controlling how it behaves in response to user interactions or data changes.

- **State Management**: Use state variables to track information like user input, authentication status, or loading states.
- **Reactivity**: ComposeFlow ensures that UI components automatically update when the underlying state changes.
- **Event Handling**: Define actions that occur in response to events like button clicks or data retrieval.

**Example**:

- **LoggedIn State**: Determines the UI components displayed when a user is authenticated.
- **Loading State**: Shows a loading indicator while data is being fetched from an API.

---

### 3. Data Types

**Data Types** define the structure of the data used within your application.

- **Custom Data Models**: Create custom data types to represent entities like users, products, or messages.
- **Type Safety**: Ensure data consistency by defining the expected data types for variables and functions.
- **Data Binding**: Bind data types to UI components to display dynamic content.

**Example**:

- **User Data Type**: Contains fields like `username`, `email`, and `profilePicture`.
- **Product Data Type**: Includes properties such as `productName`, `price`, and `description`.

---

### 4. External API Definitions

**External API Definitions** enable your application to communicate with external services and APIs.

- **API Integration**: Connect to external services like RESTful APIs or GraphQL endpoints.
- **Data Fetching**: Retrieve and send data to external sources.
- **Authentication**: Handle API authentication mechanisms like API keys or OAuth tokens.

**Example**:

- **Firebase Integration**: Use Firebase APIs for user authentication and real-time database.
- **Weather API**: Fetch weather data from a third-party service to display in your app.

---

### 5. Themes

**Themes** allow you to manage the overall look and feel of your application, including colors, typography, and other stylistic elements. By defining themes, you can ensure a consistent user interface across your app and easily make global style changes.

- **Color Scheme**: Define a set of colors for primary, secondary, background, surface, error, and other elements. This helps maintain visual consistency and supports features like dark mode.
- **Typography**: Customize fonts, sizes, weights, and styles for various text elements such as headings, body text, captions, and buttons.

### 6. Assets and Resources

Manage your app's assets and resources within the project.

- **Images and Icons**: Include visual assets like logos, background images, and icons.
- **Localization Files**: Support multiple languages by providing localized strings.

**Example**:

- **App Logo**: An image file used in the app's header.
- **Custom Font**: A specific font file applied to text components.

---

### 7. Settings and Configurations

Configure global settings that affect the entire application.

- **App Metadata**: Define app name, version, and package identifiers.
- **Firebase configuration**: Define the Firebase project ID and other configuration settings.
- **Platform-Specific Settings**: Configure settings unique to Android, iOS, or Web platforms.

**Example**:

- **App Version**: Set the current version of your app for release.
- **Android Package Name**: Define the unique identifier for your Android app.

---
