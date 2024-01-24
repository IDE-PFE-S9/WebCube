<script>
	import RunIcon from '../assets/TerminalNavbarIcons/RunIcon.svelte';
    import { terminalOutput, openedArchive, graphical, cheerpjState, problems, openedCodes, tpId, dateOpened }from '$lib/stores.js';
    import { getUserInformations, isResponseOk } from '$lib/auth.js';
	import { workCompilePopup, workCompileErrorPopup, showLoginPopup, nothingCompiled, workSavePopup, workSaveErrorPopup } from '/src/lib/PopUps/popup.js';
    import JSZip from 'jszip';
    import Cookies from 'js-cookie';

    let command = '';
    let apiUrl = process.env.API_URL;
    let projectPath = process.env.PROJECT_PATH;

    const compileCode = async () => {
		try {
			if (true) {  // change the condition to handle the reloading of the jar
				let headersList = {
					Accept: '*/*',
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
                    workCompilePopup();
				}
			}
		} catch (error) {
			console.error('Une erreur est survenue lors de la compilation du fichier :', error);
			workCompileErrorPopup();
		}
	}

    async function createZip(directoryStructure) {
		const zip = new JSZip();

		async function processDirectory(directory, zipFolder) {
			await Promise.all(
				directory.children.map(async (item) => {
					const itemName = item.name.split('/').pop(); // Get the relative name
					if (item.type === 'directory') {
						const newFolder = zipFolder.folder(itemName);
						await processDirectory(item, newFolder);
					} else if (item.type === 'file' && item.modified === true) {
						zipFolder.file(itemName, item.data);
					}
				})
			);
		}

		await processDirectory(directoryStructure, zip); // Start the process with the root directory

		const content = await zip.generateAsync({ type: 'blob' });
		return content;
	}

	async function saveFile() {
		// Function to recursively update the archive
		function updateArchive(node) {
			if (node.type === 'file') {
				const codeToUpdate = $openedCodes.find((code) => code.name === node.name);
				if (codeToUpdate) {
					node.data = codeToUpdate.code; // Update the data field
					node.modified = true;
				}
			} else if (node.children) {
				node.children.forEach(updateArchive); // Recurse into directories
			}
		}

		try {
			updateArchive($openedArchive);

			const zipBlob = await createZip($openedArchive);

			let headersList = {
				Accept: '*/*',
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			let headersList2 = {
				Accept: '*/*',
				'Content-Type': 'application/json',
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			};

			const now = new Date();
			const timeElapsedInMilliseconds = now - $dateOpened;
			const timeElapsedInMinutes = Math.floor(timeElapsedInMilliseconds / 60000);
			
			await fetch(`${apiUrl}/api/tp/timeElapsed/${$tpId}`, {
				method: 'PUT',
				headers: headersList2,
				body: JSON.stringify(timeElapsedInMinutes)
			});

			const user = await getUserInformations();

			let username = user.uniqueName.split('@')[0].replace('.', '-');

			const formData = new FormData();
			formData.append(
				'directory',
				`${projectPath}/${username}/${$openedArchive.name}`
			);
			formData.append('file', zipBlob, 'archive.zip');

			const response = await fetch(`${apiUrl}/api/files/upload`, {
				method: 'POST',
				headers: headersList,
				body: formData
			});
			if (isResponseOk(response)) {
				const responseData = await response.text();
			}

			// traverse the $openedArchive and change all the modified fields to false
			function resetModified(node) {
				if (node.type === 'file') {
					node.modified = false;
				} else if (node.children) {
					node.children.forEach(resetModified); // Recurse into directories
				}
			}

			$cheerpjState.reloadJar = true; 
			$cheerpjState.reloadTestJar = true; 

			workSavePopup();
		} catch (error) {
			console.error('Une erreur est survenue lors de la sauvegarde du fichier :', error);
			workSaveErrorPopup();
		}
	}

    async function handleCommand() {
        if (command === `save`) {
            saveFile();
            $terminalOutput = [...$terminalOutput, command];
        }
        else if (command === `javac`) {
            compileCode();
            $terminalOutput = [...$terminalOutput, command];
        } else if (command === `java`) {
            try {
                const blob = await cjFileBlob("/str/application.jar");
                const data = new Uint8Array(await blob.arrayBuffer());
                if (data) {
                    if ($graphical === "true") {
                        cheerpjState.set({ showPopup: true, runJar: true, reloadJar: false });
                    } else {
                        cheerpjState.set({ showPopup: false, runJar: true, reloadJar: false });
                    }
                } else {
                    nothingCompiled();
                }
            } catch (e) {
                nothingCompiled();
            }
            $terminalOutput = [...$terminalOutput, command];
        } else if (command === "clear") {
            terminalOutput.set([]);
        } else if (command === "help") {
            $terminalOutput = [...$terminalOutput, "Commandes disponibles :"];
            $terminalOutput = [...$terminalOutput, "save : enregistrer l'archive actuelle"];
            $terminalOutput = [...$terminalOutput, "javac : compiler l'archive actuelle"];
            $terminalOutput = [...$terminalOutput, "java : exécuter l'archive actuelle"];
            $terminalOutput = [...$terminalOutput, "clear : nettoyer le terminal"];
            $terminalOutput = [...$terminalOutput, "help : afficher ce message d'aide"];
        } else {
            $terminalOutput = [...$terminalOutput, command];
            $terminalOutput = [...$terminalOutput, "Commande non trouvée. Tapez 'help' pour afficher les commandes disponibles."];
        }
        command = '';
    }

    // Function to handle keypress
    function checkForEnter(event) {
        if (event.key === 'Enter') {
            handleCommand();
        }
    }
</script>

<div class="command-prompt">
    <input
        type="text"
        bind:value={command}
        on:keypress={checkForEnter}
        placeholder="Enter command"
    />
    <!-- <button class="run-button" on:click={handleCommand}>
		<RunIcon />
	</button> -->
</div>

<style lang=scss>
    .command-prompt {
        display: flex;
        flex-direction: row;

        input { 
            font-family: 'Fira Code', monospace;
            outline: none;
            background-color: #1e1e1e;
            border: 1px solid rgb(65, 65, 65);
            color: #d4d4d4;
            width: 250px;
        }

        .run-button {
            all: unset;
            cursor: pointer;
            margin-left: 5px;
        }
    }
</style>