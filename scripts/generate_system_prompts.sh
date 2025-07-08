#!/bin/bash

# Change to the root directory of the project
cd "$(dirname "$0")/.."

# Now we're in the root directory, so all paths are relative to the root
./gradlew :generate-jsonschema-cli:run

# Need to install `npm install typescript-json-schema -g` beforehand
typescript-json-schema generate-jsonschema-cli/composeflow.d.ts "*" > schema.json

typescript-json-schema generate-jsonschema-cli/airesponse.d.ts "*" > ai_response_schema.json

typescript-json-schema generate-jsonschema-cli/create_project_airesponse.d.ts "*" > create_project_ai_response_schema.json

# Now we can use the correct paths relative to the root directory
python3 scripts/trim_json_space.py schema.json minified_schema.json
python3 scripts/make_system_prompts.py

# Move YAML template files to composeflow-api-ts if the directory exists
COMPOSEFLOW_API_DIR="../composeflow-api-ts/prompts/example_yaml"
if [ -d "$COMPOSEFLOW_API_DIR" ]; then
    echo "Moving YAML template files to $COMPOSEFLOW_API_DIR..."
    
    # Move login screen template
    if [ -f "core/model/src/commonMain/resources/login_screen_template.yaml" ]; then
        cp "core/model/src/commonMain/resources/login_screen_template.yaml" "$COMPOSEFLOW_API_DIR/"
        echo "✓ Copied login_screen_template.yaml"
    else
        echo "⚠ login_screen_template.yaml not found"
    fi
    
    # Move messages screen template
    if [ -f "core/model/src/commonMain/resources/messages_screen_template.yaml" ]; then
        cp "core/model/src/commonMain/resources/messages_screen_template.yaml" "$COMPOSEFLOW_API_DIR/"
        echo "✓ Copied messages_screen_template.yaml"
    else
        echo "⚠ messages_screen_template.yaml not found"
    fi
else
    echo "⚠ Target directory $COMPOSEFLOW_API_DIR does not exist, skipping YAML file move"
fi
