<script>
	import { terminalOutput, openedDirectory } from '$lib/stores.js';
	import RunIcon from '../assets/TerminalNavbarIcons/RunIcon.svelte';

	import JSZip from 'jszip';

	async function createZip(directoryStructure) {
		const zip = new JSZip();

		async function processDirectory(directory, zipFolder) {
			await Promise.all(
				directory.children.map(async (item) => {
					if (item.type === 'directory') {
						const newFolder = zipFolder.folder(item.name.split('/').pop());
						await processDirectory(item, newFolder);
					} else if (item.type === 'file') {
						const fileHandle = item.handle;
						const file = await fileHandle.getFile();
						if (item.name.endsWith('.jar')) {
							// Handle JAR files as binary
							zipFolder.file(item.name.split('/').pop(), file);
						} else {
							// Handle other files as text
							const contents = await file.text();
							zipFolder.file(item.name.split('/').pop(), contents);
						}
					}
				})
			);
		}

		await processDirectory(directoryStructure, zip); // awaiting this call as well

		const content = await zip.generateAsync({ type: 'blob' });
		return content;
	}

	async function runCode() {
		$terminalOutput = [...$terminalOutput, 'Compiling...'];

		const zipBlob = await createZip($openedDirectory);
		
		let headersList = {
			Accept: '*/*'
		};

		let username = "arthur";

		const formData = new FormData();
		formData.append('directory', `/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/${username}${$openedDirectory.name}`);
		formData.append('file', zipBlob, 'archive.zip');

		const response = await fetch('http://localhost:4444/api/files/upload', {
			method: 'POST',
			headers: headersList,
			body: formData
		});

		const responseData = await response.text();
		console.log(responseData);

		// API call to compile the code and get the API response
		let compilationResponse = await fetch(
			`http://localhost:4444/api/compileAndRun?projectPath=/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/${username}${$openedDirectory.name}`,
			{
				method: 'GET',
				headers: headersList
			}
		);
		let compilationResult = await compilationResponse.text();
		$terminalOutput = [...$terminalOutput, compilationResult];
	}
</script>

<button class="run-button" on:click={runCode}>
	<RunIcon />
</button>

<style lang="scss">
	.run-button {
		all: unset;
		cursor: pointer;
	}
</style>
