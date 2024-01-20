<script>
	import { terminalOutput, openedArchive, tpId, cheerpjState, problems } from '$lib/stores.js';
	import TestIcon from '../assets/TerminalNavbarIcons/TestIcon.svelte';
	import Cookies from 'js-cookie';
	import { workTestPopup, workTestErrorPopup, showLoginPopup, workCompileErrorPopup } from '/src/lib/PopUps/popup.js';
	import { getUserInformations } from '$lib/auth.js';
	import { isResponseOk } from '$lib/auth.js';

	let apiUrl = process.env.API_URL;
	let projectPath = process.env.PROJECT_PATH;

	async function testCode() {
		try {
			$terminalOutput = [...$terminalOutput, 'Testing...'];

			let headersList = {
				Accept: '*/*',
				'Content-Type': 'application/json',
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
				`${apiUrl}/api/compileAndTest?projectPath=${projectPath}/${username}/${$openedArchive.name}`,
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

				cheerpjState.set({ showPopup: false, runJar: true, reloadJar: true });

				workTestPopup();
			}
		} catch (error) {
			console.error('Une erreur est survenue lors du test du fichier :', error);
			workTestErrorPopup();
		}
	}

	function objectToString(obj) {
		let resultString = `Test: ${obj.Test}\nStatus: ${obj.Status}`;

		if (obj.Data) {
			const parsedErrors = parseErrorLog(obj.Data, $openedArchive);
			let dataString = obj.Data;

			parsedErrors.forEach((error) => {
				if (error.filePath) {
					// Check if filePath is not null
					// Construct the button tag for the file path
					const buttonTag = `<button class="terminal-link-button" data-filepath="${error.filePath}" data-linenumber="${error.lineNumber}">${error.fullLogLine}</button>`;

					// Replace the file path in the data string with the button tag
					dataString = dataString.replace(`${error.fullLogLine}`, buttonTag);
				}
			});

			resultString += `\nData: ${dataString}`;
		}
		return resultString;
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

	function parseErrorLog(log, projectStructure) {
		const regex = /(\w+(?:\.\w+)*)\((\w+\.java):(\d+)\)/g;
		let matches = [...log.matchAll(regex)];
		return matches.map((match) => {
			let classPath = match[1].split('.').slice(0, -1).join('.'); // Updated to remove the last part of the class path
			const fileName = match[2];
			const lineNumber = match[3];
			let filePath = resolveFilePath(classPath, projectStructure);
			// remove the first character of the classpath
			if (filePath && filePath[0] === '/') {
				filePath = filePath.slice(1);
			}

			// Add the full log line to the return object
			const fullLogLine = match[0];

			// Include the full log line in the return object
			return { filePath, lineNumber, fullLogLine };
		});
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

	// Main parsing function
	function parseInput(input) {
		input = input.slice(0, -1).slice(1);
		let entries = input.split(', Test:').map((entry) => entry.trim());

		const parsedEntries = entries.map((item, index) => {
			if (index === 0) {
				// Return the first item as is
				return item;
			} else {
				// Add "Test:" to the beginning of the item
				return 'Test: ' + item;
			}
		});

		console.log(parsedEntries);

		const result = parsedEntries.map((entry) => extractInfo(entry));

		function extractInfo(str) {
			// Extract 'Test' and 'Status' values
			const testMatch = str.match(/Test: (.*?),/);
			const statusMatch = str.match(/Status: (.*?),/);
			let testData = str.match(/Data: (.*)/s); // 's' flag to include newline characters

			let test = testMatch && testMatch[1] ? testMatch[1].trim() : null;
			let status = statusMatch && statusMatch[1] ? statusMatch[1].trim() : null;
			let data = testData && testData[1] ? testData[1].trim() : null;

			// Check if 'Data' is 'null' as a string
			if (data === 'null') {
				data = null;
			}

			return {
				Test: test,
				Status: status,
				Data: data
			};
		}

		return result;
	}
</script>

<button class="run-button" on:click={testCode}>
	<TestIcon />
</button>

<style lang="scss">
	.run-button {
		all: unset;
		cursor: pointer;
	}
</style>
