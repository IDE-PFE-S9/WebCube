<script>
	import { terminalOutput, openedArchive } from '$lib/stores.js';
	import RunIcon from '../assets/TerminalNavbarIcons/RunIcon.svelte';
	import Cookies from 'js-cookie';
	import { workCompilePopup } from '/src/lib/PopUps/popup.js';
	
	let apiUrl = process.env.API_URL;

	async function runCode() {
		$terminalOutput = [...$terminalOutput, 'Compiling...'];

		let headersList = {
		Accept: '*/*',
		'Authorization-Azure': 'Bearer ' + Cookies.get("azureJWT"),
		'Authorization-API': 'Bearer ' + Cookies.get("apiJWT")
	};

		let username = "arthur"

		// API call to compile the code and get the API response
		let compilationResponse = await fetch(
			`${apiUrl}/api/compileAndJar?projectPath=/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/${username}/${$openedArchive.name}`,
			{
				method: 'GET',
				headers: headersList
			}
		);
		let compilationResult = await compilationResponse.blob();

		// Create a new URL object from the Blob
		let url = window.URL.createObjectURL(compilationResult);

		// Create an anchor (`<a>`) element and set its href to the Blob URL
		let a = document.createElement('a');
		a.href = url;

		// Set the filename for the downloaded file
		a.download = $openedArchive.name.replace(/\.[^/.]+$/, '') + '.jar';

		// Append the anchor to the body, click it to trigger the download, then remove it
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);

		// It's a good practice to release the created URL object after use
		window.URL.revokeObjectURL(url);

		// // Create a new FileReader object
		// let reader = new FileReader();

		// // Define a function to be run when the FileReader has finished reading the Blob
		// reader.onloadend = function () {
		// 	// The result of reading the Blob is available as an ArrayBuffer in reader.result
		// 	let arrayBuffer = reader.result;

		// 	// Convert the ArrayBuffer to a Uint8Array
		// 	let uint8Array = new Uint8Array(arrayBuffer);

		// 	// Now you can call cheerpjAddStringFile with the Uint8Array
		// 	cheerpjAddStringFile('/str/application.jar', uint8Array);
		// };

		// reader.readAsArrayBuffer(compilationResult);

		// const exitCode = await cheerpjRunJar("/str/application.jar");
		// $terminalOutput = [...$terminalOutput, `Program exited with code ${exitCode}`];

		workCompilePopup();
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
