<script>
	import { openedCodes, openedArchive } from '$lib/stores.js';
	import SaveIcon from '$lib/assets/TerminalNavbarIcons/SaveIcon.svelte';
	import Swal from 'sweetalert2';
	import JSZip from 'jszip';
	import Cookies from 'js-cookie';

	let apiUrl = process.env.API_URL;

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
		updateArchive($openedArchive);

		const zipBlob = await createZip($openedArchive);

		let headersList = {
			Accept: '*/*',
			'Authorization-Azure': 'Bearer ' + Cookies.get('azureJWT'),
			'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
		};

		const userRes = await fetch(`${apiUrl}/api/user`, {
			method: 'GET',
			headers: headersList
		}); 

		const user = await userRes.json();

		let username = user.uniqueName.split("@")[0].replace(".", "-");

		const formData = new FormData();
		formData.append(
			'directory',
			`/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/${username}/${$openedArchive.name}`
		);
		formData.append('file', zipBlob, 'archive.zip');

		const response = await fetch(`${apiUrl}/api/files/upload`, {
			method: 'POST',
			headers: headersList,
			body: formData
		});

		const responseData = await response.text();

		// traverse the $openedArchive and change all the modified fields to false
		function resetModified(node) {
			if (node.type === 'file') {
				node.modified = false;
			} else if (node.children) {
				node.children.forEach(resetModified); // Recurse into directories
			}
		}

		resetModified($openedArchive);

		Swal.fire({
			title: 'Fichiers sauvegardés !',
			icon: 'success'
		});
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
