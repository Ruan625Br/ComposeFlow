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

# Define directories
COMPOSEFLOW_API_DIR="${COMPOSEFLOW_API_DIR:-../composeflow-api-ts}"
PROMPTS_DIR="$COMPOSEFLOW_API_DIR/prompts"
EXAMPLE_YAML_DIR="$PROMPTS_DIR/example_yaml"

# Move minified_schema.json to composeflow-api-ts if the directory exists
if [ -d "$PROMPTS_DIR" ]; then
    echo "Moving minified_schema.json to $PROMPTS_DIR..."
    if [ -f "minified_schema.json" ]; then
        cp "minified_schema.json" "$PROMPTS_DIR/"
        echo "✓ Copied minified_schema.json"
    else
        echo "⚠ minified_schema.json not found"
    fi
else
    echo "⚠ Target directory $PROMPTS_DIR does not exist, skipping minified_schema.json move"
fi

# Move YAML template files to composeflow-api-ts if the directory exists
if [ -d "$EXAMPLE_YAML_DIR" ]; then
    echo "Moving YAML template files to $EXAMPLE_YAML_DIR..."
    
    # Move login screen template
    if [ -f "core/model/src/commonMain/resources/login_screen_template.yaml" ]; then
        cp "core/model/src/commonMain/resources/login_screen_template.yaml" "$EXAMPLE_YAML_DIR/"
        echo "✓ Copied login_screen_template.yaml"
    else
        echo "⚠ login_screen_template.yaml not found"
    fi
    
    # Move messages screen template
    if [ -f "core/model/src/commonMain/resources/messages_screen_template.yaml" ]; then
        cp "core/model/src/commonMain/resources/messages_screen_template.yaml" "$EXAMPLE_YAML_DIR/"
        echo "✓ Copied messages_screen_template.yaml"
    else
        echo "⚠ messages_screen_template.yaml not found"
    fi
else
    echo "⚠ Target directory $EXAMPLE_YAML_DIR does not exist, skipping YAML file move"
fi
