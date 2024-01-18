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
		
		/*let privateKey = "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";
				
		if (!privateKey.includes('-----BEGIN RSA PRIVATE KEY-----')) {
			privateKey = '-----BEGIN RSA PRIVATE KEY-----\n' + privateKey + '\n-----END RSA PRIVATE KEY-----';
			}*/
		
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
