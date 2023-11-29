<script>
	import { openedArchive, archiveHandle, archiveMode } from '$lib/stores.js';
	import DownloadIcon from '$lib/assets/TerminalNavbarIcons/DownloadIcon.svelte';
	import { get } from 'svelte/store';
	import { archiveDownloadPopup } from '/src/lib/PopUps/popup.js';

	import JSZip from 'jszip';

	async function saveArchiveFiles() {
		const zipFile = new JSZip();

		async function createZipFromStructure(structure, parentZip) {
			for (const item of structure.children) {
				if (item.type === 'directory') {
					const folder = parentZip.folder(item.name.split('/').pop());
					await createZipFromStructure(item, folder);
				} else if (item.type === 'file') {
					parentZip.file(item.name.split('/').pop(), item.data);
				}
			}
		}

		// Get the current value of the openedArchive store
		const archiveStructure = get(openedArchive);

		await createZipFromStructure(archiveStructure, zipFile);

		const content = await zipFile.generateAsync({ type: 'blob' });

		// Create a blob URL for the zip file
		const blobUrl = URL.createObjectURL(content);

		// Create a temporary anchor element to trigger the download
		const a = document.createElement('a');
		a.href = blobUrl;
		a.download = `${$openedArchive.name}.wc`; // Name of the downloaded file
		document.body.appendChild(a);
		a.click();

		// Clean up: remove the anchor element and revoke the blob URL
		document.body.removeChild(a);
		URL.revokeObjectURL(blobUrl);

		archiveDownloadPopup();
	}
</script>

<button class="save-button" on:click={saveArchiveFiles}>
	<DownloadIcon />
</button>

<style lang="scss">
	.save-button {
		all: unset;
		cursor: pointer;
	}
</style>
