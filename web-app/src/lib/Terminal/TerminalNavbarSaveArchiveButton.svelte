<script>
	import { openedArchive, archiveHandle, archiveMode } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';

	import JSZip from 'jszip';

	async function saveArchiveFiles() {
		const zipFile = new JSZip();

		async function createZipFromStructure(structure, parentZip) {
			for (const item of structure.children) {
				if (item.type === 'directory') {
					const folder = parentZip.folder(item.name);
					await createZipFromStructure(item, folder);
				} else if (item.type === 'file') {
                    console.log(`Adding file: ${item.name} with content: ${item.data}`);
					if (typeof item.data === 'string') {
						parentZip.file(item.name.split('/').pop(), item.data);
                        console.log(item.name.split('/').pop(), item.data);
					} else {
						// If the data is binary, we assume it's in base64 and decode it
						const binaryData = JSZip.utils.binaryStringToArrayBuffer(atob(item.data));
						parentZip.file(item.name.split('/').pop(), binaryData);
					}
				}
			}
		}
 
		await createZipFromStructure($openedArchive, zipFile);

		const content = await zipFile.generateAsync({ type: 'blob' });
		const writable = await $archiveHandle.createWritable(); // assuming you have the necessary file handle
        console.log(content)
		await writable.write(content);
		await writable.close();
	}
</script>

<button class="save-button" on:click={saveArchiveFiles}>
	<SaveIcon />
</button>

<style lang="scss">
	.save-button {
		all: unset;
		cursor: pointer;
	}
</style>
