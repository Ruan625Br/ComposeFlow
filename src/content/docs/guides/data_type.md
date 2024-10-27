---
title: Data type
description: Explains data types in ComposeFlow
sidebar:
  order: 0
---

# Data Types in ComposeFlow

In ComposeFlow, **Data Types** allow you to define custom structures that represent complex data models within your application. By creating data types, you can manage and manipulate grouped sets of related properties, making your app more organized and scalable.

---

## What are Data Types?

- **Data Types** are custom-defined collections of properties that represent a specific entity or model in your app.
- They enable you to handle complex data structures as single units.
- Data types can be used to bind data to UI components, pass data between screens, and interact with external APIs.

---

## Example: Creating an `ArticleItem` Data Type

Imagine you are building a news app and need to represent articles with specific attributes.

### Properties of `ArticleItem`:

- **title**: The headline of the article.
- **description**: A brief summary of the article content.
- **headerImageUrl**: A URL to the article's header image.
- **createdDate**: The publication date of the article.

By defining an `ArticleItem` data type, you can easily manage and display articles throughout your app.

![Data type example](/image/datatype_example.png)

---

## How to Create a Data Type in ComposeFlow

### Step 1: Navigate to the Data Types Menu

1. **Open Your Project**: Launch your ComposeFlow project where you want to add the data type.
2. **Access Data Types**:
    - In the **Project Explorer** on the left side, locate and click on the **"Data Types"** section.

### Step 2: Add a New Data Type

1. **Create Data Type**:
    - Click on the **"+"** button next to **"Data Types"**.
    - A dialog or panel will appear for creating a new data type.

2. **Define Data Type Properties**:
    - **Name**: Enter a name for your data type (e.g., **"ArticleItem"**).
    - **Properties**: Add the properties for the data type:
        - **title**: Set the type to **String**.
        - **description**: Set the type to **String**.
        - **headerImageUrl**: Set the type to **String**.
        - **createdDate**: Set the type to **Instant** or **String**, depending on your preference.

3. **Save Data Type**:
    - Click **"Save"** or **"Create"** to add the data type to your project.

---

## Using Data Types in Your App

### Binding Data Types to UI Components

- Refer to the [dynamic number of items](/advanced_ui/dynamic_items)

### Populating Data

- **Static Data**:
    - Manually create instances of the data type with predefined values for testing.
- **Dynamic Data**:
    - Fetch data from external APIs or databases and map the results to your data type.
    - Use actions like **"Call External API"** to retrieve data.

---

## Managing Data Types

### Editing a Data Type

- **Update Properties**:
    - Navigate to the **"Data Types"** section.
    - Select the data type you wish to edit.
    - Add, remove, or modify properties as needed.

### Deleting a Data Type

- **Remove Unused Data Types**:
    - In the **"Data Types"** section, select the data type.
    - Click **"Delete"** or **"Remove"** to delete it from your project.

:::caution
Ensure that the data type is not in use before deleting to prevent errors.
:::
