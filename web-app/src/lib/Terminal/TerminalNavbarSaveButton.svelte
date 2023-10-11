<script>
	import { openedCodes, selectedFile } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';

	async function saveFile() {
		const fileHandle = $selectedFile.handle; // Access the file handle from the selectedFile store.

		if (!fileHandle) {
			console.error('File handle is not available');
			return;
		}

		// Find the code object for the currently selected file.
		const codeObj = $openedCodes.find(code => code.name === $selectedFile.name);
		if (!codeObj) {
			console.error('Code not found for the selected file');
			return;
		}

		const contents = codeObj.code;  // Access the code from the code object.
        console.log('Contents to be written:', contents);

		try {
			// Create a writable stream.
			const writable = await fileHandle.createWritable();
			// Write the contents back to the file.
			await writable.write(contents);
			// Close the stream.
			await writable.close();
			console.log('File has been saved');
		} catch (error) {
			console.error('Error writing file:', error);
		}
	}
</script>

<button class="save-button" on:click={saveFile}>
	<SaveIcon />
</button>

<style lang="scss">
	.save-button {
		all: unset;
		cursor: pointer;
	}
</style>
