<script>
	export let tp;

	import JSZip from 'jszip';
	import Cookies from 'js-cookie';
	import { openedArchive, archiveMode, currentTab, graphical, tpId } from '$lib/stores.js';
	import {isResponseOk} from '$lib/auth.js';

	let apiUrl = process.env.API_URL;

	

	const getArchive = async () => {
		let headersList = {
			Accept: '*/*',
			'Authorization-Azure': 'Bearer ' + Cookies.get("azureJWT"),
			'Authorization-API': 'Bearer ' + Cookies.get("apiJWT")
		};
		let archiveResponse = await fetch(`${apiUrl}/api/tp/archive/${tp.id}`, {
			method: 'GET',
			headers: headersList
		});
		if(isResponseOk(archiveResponse)){
			let archive = await archiveResponse.blob();

			let archiveStructure = await handleArchive(archive);

			console.log(archiveStructure);

			const xmlDescriptorString = findDescriptor(archiveStructure);

			const parsedXml = parseXml(xmlDescriptorString);

			updateFromDescriptor(archiveStructure, parsedXml.documentElement);

			sortDirectoryStructure(archiveStructure);

			openedArchive.set(archiveStructure);
			archiveMode.set(true);

			console.log($openedArchive);

			tpId.set(tp.id);
			currentTab.set('Archive');
		}
	};

	async function handleArchive(file) {
		const zip = new JSZip();
		const data = await file.arrayBuffer();
		const loadedZip = await zip.loadAsync(data);

		// Initialize the root directory with tp.name
		const rootDirectory = {
			type: 'directory',
			name: tp.name,
			visible: true,
			children: []
		};

		function addToStructure(parent, path, isDir, content) {
			const parts = path.split('/').filter(Boolean);
			let current = parent;

			for (let i = 0; i < parts.length; i++) {
				const part = parts[i];
				const isLast = i === parts.length - 1;
				// Construct the full path based on the parent's name and the relative path
				const currentPath =
					(parent.name ? parent.name + '/' : '') + parts.slice(0, i + 1).join('/');

				if (isLast && !isDir) {
					current.children.push({
						type: 'file',
						name: currentPath, // Use the constructed full path
						data: content, // Content will be text or Uint8Array depending on file type
						visible: false,
						write: false,
						modified: false
					});
				} else {
					let dir = current.children.find((c) => c.type === 'directory' && c.name === currentPath);
					if (!dir) {
						dir = {
							type: 'directory',
							name: currentPath, // Use the constructed full path
							visible: false,
							children: []
						};
						current.children.push(dir);
					}
					current = dir;
				}
			}
		}

		for (const [path, file] of Object.entries(loadedZip.files)) {
			const isDir = file.dir;
			// Check the file extension to determine how to read the content
			if (isDir) {
				addToStructure(rootDirectory, path, isDir, '');
			} else {
				const content = await file.async('text');
				addToStructure(rootDirectory, path, isDir, content);
			}
		}

		return rootDirectory;
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
		return null
	}

	function parseXml(xml) {
		const parser = new DOMParser();
		const xmlDoc = parser.parseFromString(xml, 'application/xml');
		graphical.set(xmlDoc.documentElement.getElementsByTagName('Parameters')[0].attributes.getNamedItem("graphical").value);
		// TODO: ADD USER SIGNATURE
		return xmlDoc;
	}

	const updateFromDescriptor = (jsObject, xmlElement) => {
		const updateChildren = (jsChildren, xmlChildren) => {
			for (const xmlChild of xmlChildren) {
				const xmlName = xmlChild.getAttribute('name');

				const jsChild = jsChildren.find((child) => child.name.endsWith('/' + xmlName));
				if (jsChild) {
					jsChild.visible = xmlChild.getAttribute('visible') === 'true';
					if (jsChild.type === 'file') {
						jsChild.write = xmlChild.getAttribute('write') === 'true';
					}

					if (jsChild.type === 'directory') {
						const xmlFolders = Array.from(xmlChild.getElementsByTagName('Folder'));
						const xmlFiles = Array.from(xmlChild.getElementsByTagName('File'));


						const directXmlFolders = xmlFolders.filter((folder) => folder.parentNode === xmlChild);
						const directXmlFiles = xmlFiles.filter((file) => file.parentNode === xmlChild);

						updateChildren(jsChild.children, [...directXmlFolders, ...directXmlFiles]);
					}
				}
			}
		};

		const rootFolders = Array.from(xmlElement.getElementsByTagName('Folder'));
		const rootFiles = Array.from(xmlElement.getElementsByTagName('File'));
		
		const directRootFolders = rootFolders.filter((folder) => folder.parentNode === xmlElement);
		const directRootFiles = rootFiles.filter((file) => file.parentNode === xmlElement);

		updateChildren(jsObject.children, [...directRootFolders, ...directRootFiles]);
	};

	function sortDirectoryStructure(directory) {
		if (!directory.children || directory.children.length === 0) {
			return;
		}

		directory.children.sort((a, b) => {
			if (a.type === b.type) {
				return a.name.localeCompare(b.name, undefined, { numeric: true, sensitivity: 'base' });
			}
			return a.type === 'directory' ? -1 : 1;
		});

		directory.children.forEach((child) => {
			if (child.type === 'directory') {
				sortDirectoryStructure(child);
			}
		});
	}
</script>

<!-- TODO : completion -->
<button
	class="button-archive"
	class:selected={$openedArchive?.name === tp.name}
	on:click={getArchive}
>
	{tp.name}
</button>

<style lang="scss">
	.button-archive {
		all: unset;
		color: rgb(204, 204, 204);
		padding: 0.3rem;
		padding-left: 0.7rem;
		font-size: 0.9rem;
		gap: 0.3rem;
		width: 100%;
		cursor: pointer;

		&:hover {
			background-color: rgb(43, 45, 46);
		}

		&.selected {
			background-color: rgb(55, 55, 60);
		}
	}
</style>
