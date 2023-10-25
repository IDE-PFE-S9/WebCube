<script>
	import ArchiveDirectoryItem from './ArchiveDirectoryItem.svelte';
	import { openedArchive, archiveMode } from '$lib/stores.js';

	$: directoryObject = {};

	import JSZip from 'jszip';

	/*
        A WebCube Archive must contain different things:
		- descriptor.xml
		- src folder for Java source files
		- lib folder for Java .jar libs
		- test folder for Java 
		- assets folder for assets
	
	
	<Folder name="folderName" visible="true" write="true" modifDate="2023-11-15">
		<File name="fileName1" visible="false" read="true" write="true" modifDate="2023-11-15" />
		<File name="fileName2" visible="true" read="true" write="false" modifDate="2023-11-15" />
	</Folder>
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
				visible: false,
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
						data: await zipObject.files[name].async('text'), // Change "text" to other types as needed
						visible: false,
						write: false
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
			let archiveStructure = await handleArchive(archive);

			const xmlDescriptorString = findDescriptor(archiveStructure);

			if (!xmlDescriptorString) {
				console.error('Descriptor not found in the archive.');
				return;
			}

			const description = parseXMLToJson(xmlDescriptorString);
			console.log("descriptor file: ", description);

			console.log("directory before mergin:", archiveStructure.children[0])
			directoryObject = mergeObjects(description, archiveStructure.children[0]);

			openedArchive.set(directoryObject);
			archiveMode.set(true);
			console.log("directory object:", directoryObject);
		} catch (err) {
			console.error('Error reading file:', err);
		}
	}

	function findDescriptor(data) {
		if (data.type === 'file' && data.name.endsWith('descriptor.xml')) {
			return data.data;
		}
		if (data.type === 'directory' && data.children) {
			for (let child of data.children) {
				const descriptor = findDescriptor(child);
				if (descriptor) return descriptor;
			}
		}
		return null;
	}

	const parseXMLToJson = (xmlString) => {
		const parser = new DOMParser();
		const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

		const traverseNode = (node, path) => {
			let result = {};
			let nodeName = node.nodeName;
			let nameAttr = node.getAttribute('name');
			let visibleAttr = node.getAttribute('visible') === 'true';
			let writeAttr = node.getAttribute('write') === 'true';

			if (nodeName === 'Folder') {
				result.type = 'directory';
				result.name = nameAttr;
				result.visible = visibleAttr;
				result.children = [];

				for (let i = 0; i < node.childNodes.length; i++) {
					let child = node.childNodes[i];
					if (child.nodeType === 1) {
						// Check if node is an Element node
						result.children.push(traverseNode(child, path ? path + '/' + nameAttr : nameAttr));
					}
				}
			} else if (nodeName === 'File') {
				result.type = 'file';
				result.name = (path ? path + '/' : '') + nameAttr;
				result.data = 'test'; // Placeholder as specific data for each file isn't provided in XML
				result.visible = visibleAttr;
				result.write = writeAttr;
			}

			return result;
		};
		return traverseNode(xmlDoc.childNodes[0], '');
	};

	function mergeObjects(obj1, obj2) {
		for (let key in obj1) {
			if (obj1.hasOwnProperty(key)) {
				if (
					obj2.hasOwnProperty(key) &&
					typeof obj1[key] === 'object' &&
					typeof obj2[key] === 'object'
				) {
					mergeObjects(obj1[key], obj2[key]);
				} else if (key === 'write' || key === 'visible') {
					obj2[key] = obj1[key];
				} else if (!obj2.hasOwnProperty(key)) {
					obj2[key] = obj1[key];
				}
			}
		}
		return obj2;
	}
</script>

<div class="file-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	{#if directoryObject.name}
		<ArchiveDirectoryItem directory={directoryObject} />
	{:else}
		<button on:click={openArchive} class="button-open-directory">Open WebCube Archive</button>
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
