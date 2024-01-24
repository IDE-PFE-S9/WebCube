<script>
	import { openedArchive, archiveHandle, archiveMode } from '$lib/stores.js';
	import DownloadIcon from '$lib/assets/TerminalNavbarIcons/DownloadIcon.svelte';
	import { get } from 'svelte/store';
	import { archiveDownloadPopup, archiveDownloadErrorPopup} from '/src/lib/PopUps/popup.js';

	import JSZip from 'jszip';
	import CryptoJS from 'crypto-js';
	import { getUserInformations, getPublicKeyTeacher } from '$lib/auth.js';
	import { RSA, Crypt } from 'hybrid-crypto-js';


	async function saveArchiveFiles() {
		try {
			const zipFile = new JSZip();
			const user = await getUserInformations();
			
			const password = 'pfe_webcube_user_' + user.uniqueName;

			let publicKey = await getPublicKeyTeacher();

			const encryptedPassword = Encryption(password, publicKey);

			zipFile.file('encryptedKey', encryptedPassword);

			async function createZipFromStructure(structure, parentZip) {
				for (const item of structure.children) {
					if (item.type === 'directory') {
						const folder = parentZip.folder(item.name.split('/').pop());
						await createZipFromStructure(item, folder);
					} else if (item.type === 'file') {
						try {
							const encryptedData = CryptoJS.AES.encrypt(item.data, password).toString();
							parentZip.file(item.name.split('/').pop(), encryptedData);
						} catch {
							console.error('Patatra:', item.name);
						}
					}
				}
			}

			// Get the current value of the openedArchive store
			const archiveStructure = get(openedArchive);

			await createZipFromStructure(archiveStructure, zipFile);

			// Generate the encrypted zip with the specified password
			const content = await zipFile.generateAsync({
			type: 'blob' // Définir le mot de passe
			});

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
		} catch (error) {
			console.error('Une erreur est survenue lors du téléchargement de l archive :', error);
			archiveDownloadErrorPopup();
		}
	}

	function Encryption(message, publicKey) {
		var entropy = 'Testing of RSA algorithm in javascript.';
		var crypt = new Crypt({
			rsaStandard: 'RSA-OAEP',
			entropy: entropy
		});
		var rsa = new RSA({
			entropy: entropy
		});
		try {
			let encrypted = crypt.encrypt(publicKey, message);
			console.log('Encrypted:', encrypted);
			return encrypted;
		} catch (error) {
			console.error('Encryption error:', error);
		}
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
