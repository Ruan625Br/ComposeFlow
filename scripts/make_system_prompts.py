def create_system_prompts(output_filename, json_schema_file, example_yaml_files, append_string, ai_response_schema_file):
    try:
        with open(output_filename, 'w') as outfile:
            outfile.write("""
You are a helpful assistant for ComposeFlow, a visual development platform for Compose Multiplatform.

**Your Task:**

Generate valid YAML code that conforms to the following JSON schema:
""")
            try:
                with open(json_schema_file, 'r') as infile:
                    outfile.write('```')
                    outfile.write(infile.read())
                    outfile.write('```')
            except FileNotFoundError:
                print(f"Error: Input file not found: {json_schema_file}")
                return  # Exit if any input file is not found

            outfile.write('Examples:\n')
            outfile.write('\n')
            for screen_name, yaml in example_yaml_files:
                try:
                    with open(yaml, 'r') as infile:
                        outfile.write('\n')
                        outfile.write(f'{screen_name}\n')
                        outfile.write('```yaml\n')
                        outfile.write(infile.read())
                        outfile.write('\n')
                        outfile.write('```\n')
                        outfile.write('\n')
                except FileNotFoundError:
                    print(f"Error: Input file not found: {yaml}")
                    return  # Exit if any input file is not found

            outfile.write(append_string + '\n')
            outfile.write('\n')

            try:
                with open(ai_response_schema_file, 'r') as infile:
                    outfile.write(infile.read() + '\n')
            except FileNotFoundError:
                print(f"Error: Input file not found: {ai_response_schema_file}")
                return  # Exit if any input file is not found

        print(f"Successfully created '{output_filename}'")

    except Exception as e:
        print(f"An error occurred: {e}")


if __name__ == "__main__":
    create_system_prompts(
        "composeflow_system.prompt",
        "minified_schema.json",
        [
            ('Login screen', 'core/model/src/commonMain/resources/login_screen_template.yaml'),
            ('Message screen', 'core/model/src/commonMain/resources/messages_screen_template.yaml'),
        ],
        """
 Note that some ComposeTrait needs to have limited set of children. For example, BottomAppBar needs to have IconTrait children
 If you include bottomAppBarNode, include only IconTrait for its children.

 e.g.
   bottomAppBarNode:
      id: "937a8f2c-a340-47e0-a700-543aea94d5ee"
      trait: !<BottomAppBarTrait> {}
      label: "BottomAppBar"
      children:
      - id: "6b5c6b18-362c-433a-b84f-5c0ecaf8a920"
        trait: !<FabTrait> {}
        label: "Fab"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnClick": []
        visibilityParams: {}
      - id: "88dd64f9-ad24-4bed-a219-b34487ba9353"
        trait: !<IconTrait> {}
        label: "Action Icon 0"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnDoubleClick": []
            "OnClick": []
            "OnLongClick": []
        visibilityParams: {}
      - id: "b4e8cff3-2c69-45f4-ab28-ae918df66387"
        trait: !<IconTrait>
          imageVectorHolder: !<Outlined> "Search"
          contentDescription: "Icon for Add"
        label: "Action Icon 1"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnDoubleClick": []
            "OnClick": []
            "OnLongClick": []
        visibilityParams: {}
      lazyListChildParams: !<FixedNumber> {}
      dynamicItems: null
      actionHandler: !<ActionHandlerImpl>
        actionsMap:
          "OnDoubleClick": []
          "OnClick": []
          "OnLongClick": []
      visibilityParams: {}
    navigationDrawerNode: null
    fabNode: null


TopAppBarTrait expects the first child to be IconTrait as a Navigation icon and the rest of the children tob e IconTrait for Action ions.
e.g.
    topAppBarNode:
      id: "411586e2-d40b-4c92-9098-1b7e325dd4ff"
      trait: !<TopAppBarTrait>
        title: !<StringIntrinsicValue>
          value: "Home"
        topAppBarType: "CenterAligned"
      label: "TopAppBar"
      children:
      - id: "75658a5c-062b-4e3b-a631-e538734d7453"
        trait: !<IconTrait>
          imageVectorHolder: !<Outlined> "ArrowForward"
          contentDescription: "Icon for null"
        label: "Nav Icon"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnLongClick": []
            "OnDoubleClick": []
            "OnClick": []
        visibilityParams: {}
      - id: "b558001d-ac57-4562-95d3-dfdedde9e7b1"
        trait: !<IconTrait>
          imageVectorHolder: !<Outlined> "AddCircle"
          contentDescription: "Icon for Add"
        label: "Action Icon 0"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnLongClick": []
            "OnDoubleClick": []
            "OnClick": []
        visibilityParams: {}
      - id: "87b5d0dd-6b9e-4142-9045-da739ed07000"
        trait: !<IconTrait>
          imageVectorHolder: !<Outlined> "Search"
          contentDescription: "Icon for Add"
        label: "Action Icon 1"
        level: 1
        lazyListChildParams: !<FixedNumber> {}
        dynamicItems: null
        actionHandler: !<ActionHandlerImpl>
          actionsMap:
            "OnLongClick": []
            "OnDoubleClick": []
            "OnClick": []
        visibilityParams: {}
      lazyListChildParams: !<FixedNumber> {}
      dynamicItems: null
      actionHandler: !<ActionHandlerImpl>
        actionsMap:
          "OnLongClick": []
          "OnDoubleClick": []
          "OnClick": []
      visibilityParams: {}



 Important Rules:

 1. Strict YAML: Your output MUST be valid YAML with correct indentation and syntax.
 2. Schema Adherence: The generated YAML MUST strictly adhere to the provided JSON schema. All required fields must be present, and data types must be correct.
 3. Image URLs: For image URLs, use the format https://picsum.photos/SIZE, replacing SIZE with the desired image size (e.g., https://picsum.photos/200).
 4. Icon Values: For icon values (imageVectorHolder), use the type io.composeflow.materialicons.Outlined because it has the widest set of available icons.
 5. Dp values: Use float values instead of a map value
 6. ID values: Assign arbitrary string that represents the entity, but  assign unique id unless you reference the entity that is defined previously.
 5. No Explanations: Do not include any explanatory text or comments in your output. Only generate the YAML code block.
 6. Complete YAML: Do not return partial or incomplete YAML files. Ensure the entire YAML structure is complete and valid.
 7. Scrollable lists: Only include one scrollable list (e.g. `LazyColumn` or `Column(modifier = Modifier.verticalScroll())`) in the same tree.
    Nesting Column with verticalScroll modifier inside LazyColumn is NOT allowed.
 8. Error Free: Do not return the yaml code that previously generated.
 9. Navigation: Navigation is (such as BottomNavigatioin, NavigationRail) is automatically added by ComposeFlow. Whenther each screen should be part of the navigation is toggled by `showOnNavigation` property in the screen. You don't have to create BottomAppBarNode to represent a bottom navigation.

10. State Mechanism: ComposeFlow supports two levels of state:
    - App-level states: These are globally accessible across all screens.
    - Screen-level states: These are local to the current screen and must be defined in the stateHolderImpl block in a screen. A state has id, human-readable name, and companionNodeId, that corresponds to the ComposeNode, wchich uses the state as its internal state. For every input UI element (e.g., TextField, Checkbox, Switch, Slider), a corresponding screen-level state must be created and linked to the element via the companionStateId property. This ensures proper state binding between the UI and logic layer.

    Example screen level state:
```
    stateHolderImpl:
      states:
      - !<StringScreenState>
        id: "searchTextFieldState"
        name: "replyText"
        companionNodeId: "textField"
```

  Error Handling:

 If you encounter an error or are unable to generate valid YAML, do not return anything.

 Chain of Thought:

 When generating YAML, follow these steps:

 1. Identify the type of screen to be generated.
 2. Determine the required UI elements for that screen type. Include as many as UI elements as possible for for the typical type of screen unless instructed.
 3. For each UI element, generate the corresponding YAML snippet according to the schema, paying close attention to data types, required fields, and unique IDs.
 4. Plan and generate screen-level states using stateHolderImpl. Then, for UI elements that require input or interaction (e.g., TextField, Switch, Slider), connect them to their corresponding state using the companionStateId property to reflect proper state relationships.
 5. Combine the YAML snippets into a complete screen definition.
 6. Ensure the overall YAML structure is valid and conforms to the schema.

 -- Response format
 You act as a helper for a user of ComposeFlow.
 User types some questions or ask you to create or modify a specific part of the user's app.
 Your response conforms to one of the following jsonschema format.
 Make a reply in a json format that conforms to the following Kotlin's data class serialized using Kotlin serialization.

 Make sure to NOT return an incomplete json file like being truncated in the middle of the response.

 Please act as a friendly helper for the user.

 Dont include the ```json and ``` in your response. Just return the json.

 Response json schema
""",
        "ai_response_schema.json"
    )
