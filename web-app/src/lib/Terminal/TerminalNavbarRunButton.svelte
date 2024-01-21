<script>
	import { terminalOutput, openedArchive, cheerpjState, graphical, problems } from '$lib/stores.js';
	import RunIcon from '../assets/TerminalNavbarIcons/RunIcon.svelte';
	import Cookies from 'js-cookie';
	import { getUserInformations } from '$lib/auth.js';
	import {
		workCompilePopup,
		workCompileErrorPopup,
		showLoginPopup
	} from '/src/lib/PopUps/popup.js';
	import { isResponseOk } from '$lib/auth.js';

	let apiUrl = process.env.API_URL;
	let projectPath = process.env.PROJECT_PATH;

	async function runGraphicalCode() {
		try {
			if (true) {  // change the condition to handle the reloading of the jar
				let headersList = {
					Accept: '*/*',
					'Authorization-Azure': 'Bearer ' + Cookies.get('azureJWT'),
					'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
				};

				const user = await getUserInformations();

				if (!user) {
					showLoginPopup();
					return;
				}

				let username = user.uniqueName.split('@')[0].replace('.', '-');

				// Returns a jar inside a blob
				let compilationResponse = await fetch(
					`${apiUrl}/api/compileAndJar?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
					{
						method: 'GET',
						headers: headersList
					}
				);
				if (isResponseOk(compilationResponse)) {
					if (compilationResponse.headers.get('Content-Type') !== 'application/octet-stream') {
						let compilationResult = await compilationResponse.text();
						$terminalOutput = [...$terminalOutput, compilationResult.split('\n')[0]];
						let errors = parseCompilationErrors(compilationResult);
						let newProblems = [];
						errors.forEach((error) => {
							const buttonTag = `<button class="terminal-link-button" data-filepath="${error.filePath}" data-linenumber="${error.lineNumber}">${error.filePath}:${error.lineNumber} error: ${error.errorMessage}</button>`;
							$terminalOutput = [...$terminalOutput, buttonTag];
							newProblems.push({
								message: error.errorMessage,
								file: error.filePath,
								line: error.lineNumber,
								codeSnippet: error.codeSnippet
							});
						});
						problems.set(newProblems);
						workCompileErrorPopup();
						return;
					}

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

				if($graphical === 'true')
					cheerpjState.set({ showPopup: true, runJar: true, reloadJar: false });
				else
					cheerpjState.set({ showPopup: false, runJar: true, reloadJar: false });

				workCompilePopup();
			} else {
				if($graphical === 'true')
					cheerpjState.set({ showPopup: true, runJar: true, reloadJar: false });
				else
					cheerpjState.set({ showPopup: false, runJar: true, reloadJar: false });
				
				workCompilePopup();
			}
		} catch (error) {
			console.error('Une erreur est survenue lors de la compilation du fichier :', error);
			workCompileErrorPopup();
		}
	}

	async function runCode() {
		try {
			let headersList = {
				Accept: '*/*',
				'Authorization-Azure': 'Bearer ' + Cookies.get('azureJWT'),
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			const user = await getUserInformations();

			if (!user) {
				showLoginPopup();
				return;
			}

			let username = user.uniqueName.split('@')[0].replace('.', '-');

			// API call to compile the code and get the API response
			let compilationResponse = await fetch(
				`${apiUrl}/api/compileAndRun?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
				{
					method: 'GET',
					headers: headersList
				}
			);
			if (isResponseOk(compilationResponse)) {
				let compilationResult = await compilationResponse.text();
				if (compilationResult.includes('Compilation failed')) {
					$terminalOutput = [...$terminalOutput, compilationResult.split('\n')[0]];
					let errors = parseCompilationErrors(compilationResult);
					let newProblems = [];
					errors.forEach((error) => {
						const buttonTag = `<button class="terminal-link-button" data-filepath="${error.filePath}" data-linenumber="${error.lineNumber}">${error.filePath}:${error.lineNumber} error: ${error.errorMessage}</button>`;
						$terminalOutput = [...$terminalOutput, buttonTag];
						newProblems.push({
							message: error.errorMessage,
							file: error.filePath,
							line: error.lineNumber,
							codeSnippet: error.codeSnippet
						});
					});
					problems.set(newProblems);
					workCompileErrorPopup();
					return;
				} else if (compilationResult.includes('Run failed')) {
					let errors = parseRunningErrors(compilationResult, $openedArchive);
					$terminalOutput = [...$terminalOutput, compilationResult.split('\n')[0]];
					$terminalOutput = [...$terminalOutput, compilationResult.split('\n')[1]];
					$terminalOutput = [...$terminalOutput, errors.exception];
					errors.frames.forEach((frame) => {
						const buttonTag = `at <button class="terminal-link-button" data-filepath="${frame.filePath}" data-linenumber="${frame.lineNumber}">${frame.filePath}:${frame.lineNumber}</button>`;
						$terminalOutput = [...$terminalOutput, buttonTag];
					});
				} else {
					$terminalOutput = [...$terminalOutput, compilationResult];
				}
				workCompilePopup();
			}
		} catch (error) {
			console.error('Une erreur est survenue lors de la compilation du fichier :', error);
			workCompileErrorPopup();
		}
	}

	function parseRunningErrors(stackTrace, projectStructure) {
		const lines = stackTrace.split('\n');
		let parsedStackTrace = {
			exception: null,
			frames: []
		};

		if (lines.length > 0) {
			// Parse the first line for the exception message
			parsedStackTrace.exception = lines[3].trim();

			// Regular expression to match the stack frame lines
			const frameRegex = /at ([\w.]+)\(([\w.]+):(\d+)\)/;

			for (const line of lines) {
				const match = line.match(frameRegex);
				if (match) {
					const fullyQualifiedMethodName = match[1];
					const lineNumber = parseInt(match[3], 10);

					// Extract class path from the fully qualified method name
					const classPath = fullyQualifiedMethodName.substring(
						0,
						fullyQualifiedMethodName.lastIndexOf('.')
					);

					// Resolve the full file path from the class path
					const filePath = resolveFilePath(classPath, projectStructure).slice(1);

					if (filePath) {
						parsedStackTrace.frames.push({ filePath, lineNumber });
					}
				}
			}
		}

		return parsedStackTrace;
	}

	function resolveFilePath(classPath, projectStructure) {
		// Convert classPath to a relative file path
		let relativePath = classPath.replace(/\./g, '/') + '.java';

		// Logic to match relativePath with actual file path in projectStructure
		return findFilePathInStructure(relativePath, projectStructure);
	}

	function findFilePathInStructure(relativePath, structure, currentPath = '') {
		if (structure.type === 'file' && structure.name.endsWith(relativePath)) {
			return currentPath + '/' + structure.name;
		}

		if (structure.type === 'directory') {
			let newPath = currentPath;
			// Check if current directory is part of the relative path
			if (relativePath.startsWith(structure.name)) {
				newPath += (newPath ? '/' : '') + structure.name;
			}

			for (const child of structure.children) {
				const result = findFilePathInStructure(relativePath, child, newPath);
				if (result) return result;
			}
		}

		return null;
	}

	function parseCompilationErrors(log) {
		const regex = /(.*\.java):(\d+): error: (.*)/;
		let errors = [];

		// Split the output into lines and process each line
		const lines = log.split('\n');
		for (let i = 0; i < lines.length; i++) {
			const match = lines[i].match(regex);
			if (match) {
				let filePath = match[1];
				const lineNumber = parseInt(match[2], 10);
				const errorMessage = match[3];
				const fullLogLine = match[0];

				// Trim the file path to start from the archive name
				const archiveIndex = filePath.indexOf(`/${$openedArchive.name}/`);
				if (archiveIndex !== -1) {
					filePath = filePath.substring(archiveIndex + 1);
				}

				let codeSnippet = lines[i + 1] + '\n' + lines[i + 2];

				errors.push({ filePath, lineNumber, errorMessage, fullLogLine, codeSnippet });
			}
		}
		return errors;
	}
</script>

{#if $graphical === 'true'}
	<button class="run-button" on:click={runGraphicalCode}>
		<RunIcon />
	</button>
{:else}
	<button class="run-button" on:click={runGraphicalCode}>
		<RunIcon />
	</button>
{/if}

<style lang="scss">
	.run-button {
		all: unset;
		cursor: pointer;
	}
</style>
