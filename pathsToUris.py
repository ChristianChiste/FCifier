import json
import sys

def rename_and_prepend(input_string, obj):
    if isinstance(obj, list):
        return [rename_and_prepend(input_string, item) for item in obj]
    elif isinstance(obj, dict):
        new_dict = {}
        for key, value in obj.items():
            if key == 'downloadFilePaths':
                new_dict['downloadUris'] = [input_string + uri for uri in value]
            elif key == 'uploadFilePaths':
                new_dict['uploadUris'] = [input_string + uri for uri in value]
            else:
                new_dict[key] = rename_and_prepend(input_string, value)
        return new_dict
    else:
        return obj


def process_function_definitions(json_data):
    for func in json_data:
        if 'inputVariables' in func:
            for input_var in func['inputVariables']:
                if 'parallel' in input_var and input_var['parallel'].lower() == 'true':
                    # Remember the value of the 'identifier' key
                    identifier_value = input_var.get('identifier')
                        
                    # Iterate over 'forEachIterations'
                    if 'forEachIterations' in func:
                        for iter_obj in func['forEachIterations']:
                            if 'iterationObject' in iter_obj:
                                # Rename 'iterationObject' to the remembered 'identifier'
                                iter_obj[identifier_value] = [iter_obj.pop('iterationObject')]

    return json_data

def main():

    args = sys.argv[1:]

    if len(args) != 2:
        print("Usage: python3 pathsToUris.py <pathToJsonInputFile> <bucketUriPrefix>")
        exit(1)

    file_path = args[0]
    input_string = args[1]

    try:
        # Read the JSON file
        with open(file_path, 'r') as file:
            data = json.load(file)

        # Rename keys and prepend input string
        updated_data = rename_and_prepend(input_string, data)

        # Process function definitions
        updated_data = process_function_definitions(updated_data)

        # Write the updated JSON data back to the file
        with open(file_path, 'w') as file:
            json.dump(updated_data, file, indent=2)

        print("JSON structure processed successfully.")

    except FileNotFoundError:
        print(f"Error: File '{file_path}' not found.")
    except json.JSONDecodeError:
        print(f"Error: Invalid JSON format in '{file_path}'.")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    main()
