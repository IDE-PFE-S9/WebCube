import os

def append_style_to_files(directory_path, style_block):
    for filename in os.listdir(directory_path):
        file_path = os.path.join(directory_path, filename)
        # Ensure the file has the .svelte extension before processing
        if file_path.endswith('.svelte'):
            with open(file_path, 'r+', encoding='utf-8') as file:
                content = file.read()
                # Append the style block to the content
                updated_content = content + '\n' + style_block
                # Reset file pointer to the beginning of the file
                file.seek(0)
                # Write the updated content to the file
                file.write(updated_content)
                # Truncate any remaining old content after the new content
                file.truncate()

# Specify the path to your directory
directory_path = 'web-app/src/lib/assets/icons'

# Specify the style block to append
style_block = '''
<style>

    svg {
        width: 16px;
        height: 16px;
    }

</style>
'''

# Call the function
append_style_to_files(directory_path, style_block)
