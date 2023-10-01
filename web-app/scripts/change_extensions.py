import os

def rename_files_in_directory(directory_path, old_extension, new_extension):
    for filename in os.listdir(directory_path):
        if filename.endswith(old_extension):
            base_file = os.path.splitext(filename)[0]
            new_file = f"{base_file}{new_extension}"
            old_file_path = os.path.join(directory_path, filename)
            new_file_path = os.path.join(directory_path, new_file)
            os.rename(old_file_path, new_file_path)

# Specify the path to your directory
directory_path = 'web-app/src/lib/assets/icons'

# Call the function
rename_files_in_directory(directory_path, '.svg', '.svelte')