<script>
	import { openedCode, selectedFile } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';

	async function saveFile() {
		const fileHandle = $selectedFile.handle; // $selectedFile to access the value of selectedFile store.

		if (!fileHandle) {
			console.error('File handle is not available');
			return;
		}

		const contents = $openedCode; // $openedCode to access the value of openedCode store.
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
