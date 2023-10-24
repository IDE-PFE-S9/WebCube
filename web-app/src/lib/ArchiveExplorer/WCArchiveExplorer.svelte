<script>
	import ArchiveDirectoryItem from './ArchiveDirectoryItem.svelte';
	import { openedArchive, archiveMode } from '$lib/stores.js';

	$: directoryObject = {};

	import JSZip from 'jszip';

	/*
        A WebCube Archive must contain different things:

        A descriptor.json that describes all the files and directories in the archive
        A directory named "src" that contains all the source files
        A directory named "lib" that contains all the libraries
        A directory named "test" that contains all the test files
        A directory named "res" that contains all the resources
        A directory named "web" that contains all the web files

        for each file, we need to know:
        - the name of the file
        - the path of the file
        - the type of the file (src, lib, test, res, web)
        - its visibility
        - the modification rights
    */

	async function handleArchive(file) {
		const zip = new JSZip();
		const data = await file.arrayBuffer();
		const loadedZip = await zip.loadAsync(data);

		// Recursive function to extract files from the ZIP archive, including nested directories
		async function extractToDirectoryObject(zipObject, path = '') {
			const directoryObject = {
				type: 'directory',
				name: path ? path.slice(0, -1).split('/').pop() : 'root',
				children: []
			};

			for (const [name, content] of Object.entries(zipObject.files)) {
				if (!name.startsWith(path)) {
					continue; // Skip files and directories not under the current path
				}

				const relativeName = name.substring(path.length);

				// We only want to process files and directories directly inside the current path, not nested ones
				if (
					relativeName.split('/').length > 2 ||
					(relativeName.split('/').length > 1 && !relativeName.endsWith('/'))
				) {
					continue;
				}

				if (relativeName.endsWith('/')) {
					// It's a directory, so we recursively extract its contents
					const childDirectoryObject = await extractToDirectoryObject(
						zipObject,
						`${path}${relativeName}`
					);
					directoryObject.children.push(childDirectoryObject);
				} else if (relativeName !== '') {
					// Ensure the name is not empty
					// It's a file
					directoryObject.children.push({
						type: 'file',
						name: name,
						data: await zipObject.files[name].async('text') // Change "text" to other types as needed
					});
				}
			}

			directoryObject.children.sort((a, b) => {
				// If both are the same type, compare by name
				if (a.type === b.type) {
					return a.name.localeCompare(b.name);
				}
				// Otherwise, directories come first
				return a.type === 'directory' ? -1 : 1;
			});

			return directoryObject;
		}

		const archiveDirectoryObject = await extractToDirectoryObject(loadedZip);
		return archiveDirectoryObject;
	}

	async function openArchive() {
		try {
			const [fileHandle] = await window.showOpenFilePicker({
				types: [
					{
						description: 'WebCube Archive',
						accept: {
							'application/zip': ['.wc']
						}
					}
				]
			});
			let archive = await fileHandle.getFile();
			if (!archive.name.endsWith('.wc')) {
				console.error('Invalid file type selected.');
				return;
			}
			const archiveStructure = await handleArchive(archive);
			console.log(JSON.stringify(archiveStructure, null, 2));
			// You can set your directory object here
			directoryObject = archiveStructure;
			openedArchive.set(directoryObject);
            archiveMode.set(true);
		} catch (err) {
			console.error('Error reading file:', err);
		}
	}
</script>

<div class="file-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	{#if directoryObject.name}
		<ArchiveDirectoryItem directory={directoryObject} />
	{:else}
		<button on:click={openArchive} class="button-open-directory">Open WebCube Project</button>
	{/if}
</div>

<style lang="scss">
	.file-explorer {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		min-width: 20rem;
		height: 70%;

		overflow: scroll;

		&::-webkit-scrollbar {
			display: none;
		}

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				font-weight: 300;
				color: rgb(187, 187, 187);
			}
		}

		.button-open-directory {
			font-size: 1rem;
			padding: 1rem 0.5rem;
			margin-top: 1rem;
			width: 70%;
			align-self: center;
			background-color: rgb(54, 117, 182);
			border: none;
			color: white;
			border-radius: 0.1rem;

			&:hover {
				background-color: rgb(41, 100, 158);
			}

			&:active {
				background-color: rgb(30, 75, 118);
			}
		}
	}
</style>
