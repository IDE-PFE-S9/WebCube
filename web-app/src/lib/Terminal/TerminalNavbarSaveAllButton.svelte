<script>
    // TODO: MAKE THIS WORK

	import { openedCodes, openedTabs } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';

	async function saveAllFiles() {
		for (const codeObj of $openedCodes) {
			// Find the corresponding tab object for the current code object
			const tabObj = $openedTabs.find(tab => tab.name === codeObj.name);

			if (!tabObj || !tabObj.fileHandle) {
				console.error('File handle is not available for', codeObj.name);
				continue;  // Skip to the next codeObj if the fileHandle is not available.
			}

			const contents = codeObj.code;
			console.log('Contents to be written to', codeObj.name, ':', contents);

			try {
				// Create a writable stream using the file handle from the tab object.
				const writable = await tabObj.fileHandle.createWritable();
				// Write the contents back to the file.
				await writable.write(contents);
				// Close the stream.
				await writable.close();
				console.log('File', codeObj.name, 'has been saved');
			} catch (error) {
				console.error('Error writing file', codeObj.name, ':', error);
			}
		}
	}
</script>

<button class="save-button" on:click={saveAllFiles}>
	<SaveIcon />
    save all
</button>

<style lang="scss">
	.save-button {
		all: unset;
		cursor: pointer;
	}
</style>
