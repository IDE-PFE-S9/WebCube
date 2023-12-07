<script>
	import { terminalOutput, openedArchive, cheerpjState, graphical } from '$lib/stores.js';
	import RunIcon from '../assets/TerminalNavbarIcons/RunIcon.svelte';
	import Cookies from 'js-cookie';

	import { workCompilePopup, workCompileErrorPopup } from '/src/lib/PopUps/popup.js';

	let apiUrl = process.env.API_URL;
	let projectPath = process.env.PROJECT_PATH;

	async function runGraphicalCode() {
		try {
			$terminalOutput = [...$terminalOutput, 'Compiling...'];

			if ($cheerpjState.reloadJar) {
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
				let username = user.uniqueName.split('@')[0].replace('.', '-');

				// Returns a jar inside a blob
				let compilationResponse = await fetch(
					`${apiUrl}/api/compileAndJar?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
					{
						method: 'GET',
						headers: headersList
					}
				);

				// The Jar File to be executed
				let compilationResult = await compilationResponse.blob();

				// Create a new FileReader object
				let reader = new FileReader();

				// Define a function to be run when the FileReader has finished reading the Blob
				reader.onloadend = function () {
					// The result of reading the Blob is available as an ArrayBuffer in reader.result
					let arrayBuffer = reader.result;

					// Convert the ArrayBuffer to a Uint8Array
					let uint8Array = new Uint8Array(arrayBuffer);

					// Now you can call cheerpjAddStringFile with the Uint8Array
					cheerpOSAddStringFile('/str/application.jar', uint8Array);
				};

				reader.readAsArrayBuffer(compilationResult);
			}

			cheerpjState.set({ showPopup: true, runJar: true, reloadJar: false });

			workCompilePopup();
		} catch (error) {
			console.error('Une erreur est survenue lors de la compilation du fichier :', error);
			workCompileErrorPopup();
		}
	}

	async function runCode() {
		try {
			$terminalOutput = [...$terminalOutput, 'Compiling...'];

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
			let username = user.uniqueName.split('@')[0].replace('.', '-');

			// API call to compile the code and get the API response
			let compilationResponse = await fetch(
				`${apiUrl}/api/compileAndRun?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
				{
					method: 'GET',
					headers: headersList
				}
			);
			let compilationResult = await compilationResponse.text();
			$terminalOutput = [...$terminalOutput, compilationResult];
			workCompilePopup();
		} catch (error) {
			console.error('Une erreur est survenue lors de la compilation du fichier :', error);
			workCompileErrorPopup();
		}
	}
</script>

{#if ($graphical === "true")}
	<button class="run-button" on:click={runGraphicalCode}>
		<RunIcon />
	</button>
{:else}
	<button class="run-button" on:click={runCode}>
		<RunIcon />
	</button>
{/if}

<style lang="scss">
	.run-button {
		all: unset;
		cursor: pointer;
	}
</style>
