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
