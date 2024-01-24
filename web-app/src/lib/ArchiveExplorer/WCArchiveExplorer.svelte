<script>
	import ArchiveDirectoryItem from './ArchiveDirectoryItem.svelte';
	import { openedArchive, archiveMode, archiveHandle, graphical } from '$lib/stores.js';

	$: directoryObject = {};

	import JSZip from 'jszip';
	import TpButton from '../GitExplorer/TpButton.svelte';
	import CryptoJS from 'crypto-js';
	import { getUserInformations, getPrivateKeyTeacher } from '$lib/auth.js';
	import { RSA, Crypt } from 'hybrid-crypto-js';
	import { openLocalFileErrorPopup, openLocalFilePopup} from '/src/lib/PopUps/popup.js';



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
		const user = await getUserInformations();
		const key = await loadedZip.files.encryptedKey.async('text');
		let password;
		const hasTeacherRole = user.roles.some(role => role.role === 'ROLE_TEACHER');
		if (hasTeacherRole) {
			const privateKey = await getPrivateKeyTeacher();
			let symetricKey = Decryption(key, privateKey);
			password = symetricKey.message;
		} else {
			password = 'pfe_webcube_user_' + user.uniqueName;
		}
		const rootDirectoryName = file.name.replace(/\.[^/.]+$/, ''); // Supprime l'extension du fichier
		const rootDirectory = {
				type: 'directory',
				name: rootDirectoryName,
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
						try {
							const decryptedData = CryptoJS.AES.decrypt(content, password).toString(CryptoJS.enc.Utf8);
							// Traitement supplémentaire si le déchiffrement réussit
							current.children.push({
								type: 'file',
								name: currentPath, // Use the constructed full path
								data: decryptedData, // Content will be text or Uint8Array depending on file type
								visible: false,
								write: false,
								modified: false
							});
							
						} catch (error) {
							openLocalFileErrorPopup();
							if (error.message === 'Malformed UTF-8 data') {
								console.error('La clé de déchiffrement est incorrecte.');
								// Ajoutez ici le code à exécuter en cas de clé incorrecte
							} else {
								console.error('Erreur pendant le déchiffrement :', error);
								// Autre gestion d'erreur pour d'autres types d'erreurs possibles
							}
						}						
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

			archiveHandle.set(fileHandle);
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

			const parsedXml = parseXml(xmlDescriptorString);
			updateFromDescriptor(archiveStructure, parsedXml.documentElement);
			sortDirectoryStructure(archiveStructure);

			openedArchive.set(archiveStructure);
			archiveMode.set(true);

			openLocalFilePopup();
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
		return null
	}

	// Fonction pour déchiffrer le mot de passe avec la clé privée
	function Decryption(encryptedKey, privateKey) {
		var entropy = 'Testing of RSA algorithm in javascript.';
		
		var crypt = new Crypt({
			rsaStandard: 'RSA-OAEP',
			entropy: entropy
		});
		var rsa = new RSA({
			entropy: entropy
		});
		var decrypted = crypt.decrypt(privateKey, encryptedKey);
		return decrypted;
	}
	

	function parseXml(xml) {
		const parser = new DOMParser();
		const xmlDoc = parser.parseFromString(xml, 'text/xml');
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

<div class="file-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	{#if $openedArchive}
		<ArchiveDirectoryItem directory={$openedArchive} />
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
