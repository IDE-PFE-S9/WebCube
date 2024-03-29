<script>
	import { openedCodes, openedArchive, cheerpjState, tpId, dateOpened } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';
	import { workSavePopup, workSaveErrorPopup } from '/src/lib/PopUps/popup.js';
	import JSZip from 'jszip';
	import Cookies from 'js-cookie';
	import { getUserInformations } from '$lib/auth.js';
	import {isResponseOk} from '$lib/auth.js';

	let apiUrl = process.env.API_URL;
	let projectPath = process.env.PROJECT_PATH;

	async function createZip(directoryStructure) {
		const zip = new JSZip();

		async function processDirectory(directory, zipFolder) {
			await Promise.all(
				directory.children.map(async (item) => {
					const itemName = item.name.split('/').pop(); // Get the relative name
					if (item.type === 'directory') {
						const newFolder = zipFolder.folder(itemName);
						await processDirectory(item, newFolder);
					} else if (item.type === 'file' && item.modified === true) {
						zipFolder.file(itemName, item.data);
					}
				})
			);
		}

		await processDirectory(directoryStructure, zip); // Start the process with the root directory

		const content = await zip.generateAsync({ type: 'blob' });
		return content;
	}

	async function saveFile() {
		// Function to recursively update the archive
		function updateArchive(node) {
			if (node.type === 'file') {
				const codeToUpdate = $openedCodes.find((code) => code.name === node.name);
				if (codeToUpdate) {
					node.data = codeToUpdate.code; // Update the data field
					node.modified = true;
				}
			} else if (node.children) {
				node.children.forEach(updateArchive); // Recurse into directories
			}
		}

		try {
			updateArchive($openedArchive);

			const zipBlob = await createZip($openedArchive);

			let headersList = {
				Accept: '*/*',
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			let headersList2 = {
				Accept: '*/*',
				'Content-Type': 'application/json',
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			const now = new Date();
			const timeElapsedInMilliseconds = now - $dateOpened;
			const timeElapsedInMinutes = Math.floor(timeElapsedInMilliseconds / 60000);
			
			await fetch(`${apiUrl}/api/tp/timeElapsed/${$tpId}`, {
				method: 'PUT',
				headers: headersList2,
				body: JSON.stringify(timeElapsedInMinutes)
			});

			const user = await getUserInformations();

			let username = user.uniqueName.split('@')[0].replace('.', '-');

			const formData = new FormData();
			formData.append(
				'directory',
				`${projectPath}/${username}/${$openedArchive.name}`
			);
			formData.append('file', zipBlob, 'archive.zip');

			const response = await fetch(`${apiUrl}/api/files/upload`, {
				method: 'POST',
				headers: headersList,
				body: formData
			});
			if (isResponseOk(response)) {
				const responseData = await response.text();
			}

			// traverse the $openedArchive and change all the modified fields to false
			function resetModified(node) {
				if (node.type === 'file') {
					node.modified = false;
				} else if (node.children) {
					node.children.forEach(resetModified); // Recurse into directories
				}
			}

			$cheerpjState.reloadJar = true; 
			$cheerpjState.reloadTestJar = true; 

			workSavePopup();
		} catch (error) {
			console.error('Une erreur est survenue lors de la sauvegarde du fichier :', error);
			workSaveErrorPopup();
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
