import json
import sys

def remove_whitespace_from_json(input_file_path, output_file_path):
    """
    Reads a JSON file, removes unnecessary whitespace, and writes the
    compact JSON to the specified output file.

    Args:
        input_file_path (str): The path to the input JSON file.
        output_file_path (str): The path to the output JSON file.
    """
    try:
        with open(input_file_path, 'r') as f:
            data = json.load(f)

        with open(output_file_path, 'w') as f:
            json.dump(data, f)
        print(f"Successfully removed whitespace from '{input_file_path}' and saved to '{output_file_path}'.")

    except FileNotFoundError:
        print(f"Error: Input file not found at '{input_file_path}'.")
    except json.JSONDecodeError:
        print(f"Error: Invalid JSON format in '{input_file_path}'.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python remove_whitespace.py <path_to_input_json_file> <path_to_output_json_file>")
    else:
        input_json_file_path = sys.argv[1]
        output_json_file_path = sys.argv[2]
        remove_whitespace_from_json(input_json_file_path, output_json_file_path)
