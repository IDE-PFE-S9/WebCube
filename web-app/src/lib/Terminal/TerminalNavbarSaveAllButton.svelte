<script>
	// TODO: MAKE THIS WORK

	import { openedArchive, archiveHandle } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';

	import JSZip from 'jszip';

	async function saveArchiveFiles() {
		const zip = new JSZip();

		async function processDirectory(directory, zipFolder) {
			await Promise.all(
				directory.children.map(async (item) => {
					if (item.type === 'directory') {
						const newFolder = zipFolder.folder(item.name);
						await processDirectory(item, newFolder);
					} else if (item.type === 'file') {
						const fileHandle = item.handle;
						const file = await fileHandle.getFile();
						if (item.name.endsWith('.jar')) {
							// Handle JAR files as binary
							zipFolder.file(item.name, file);
						} else {
							// Handle other files as text
							const contents = await file.text();
							zipFolder.file(item.name, contents);
						}
					}
				})
			);
		}

		await processDirectory(directoryStructure, zip); // awaiting this call as well

		const content = await zip.generateAsync({ type: 'blob' });
		
		console.log($archiveHandle)
	}
</script>

<button class="save-button" on:click={saveArchiveFiles}>
	<SaveIcon />
	save all
</button>

<style lang="scss">
	.save-button {
		all: unset;
		cursor: pointer;
	}
</style>
