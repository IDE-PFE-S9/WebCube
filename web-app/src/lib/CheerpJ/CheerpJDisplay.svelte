<script>
	import { onMount } from 'svelte';
	import {
		terminalOutput,
		cheerpjState,
		cheerpjWidth,
		cheerpjHeight,
		openedArchive
	} from '$lib/stores.js';

	let previousContent = '';
	let iframe;

	const stopExecution = () => {
		cheerpjState.set({ showPopup: true, runJar: false, reloadJar: false });
		$terminalOutput = [...$terminalOutput, `Process terminated by the user`];
		popup.style.display = 'none';
	};

	onMount(async () => {
		await cheerpjInit();
		let popup = document.getElementById('popup');

		// run the code when the button is pressed in another component
		cheerpjState.subscribe(({ showPopup, runJar }) => {
			if (showPopup) {
				popup.style.display = 'flex';
			}
			if (runJar) {
				iframe.src = 'about:blank';
				iframe.onload = async () => {
					const iframeWindow = iframe.contentWindow;
					cheerpjCreateDisplay(-1, -1, iframeWindow.document.body);
					const exitCode = await cheerpjRunJar('/str/application.jar');
					$terminalOutput = [...$terminalOutput, `Program exited with code ${exitCode}`];
					console.log('Program exited with code', exitCode);
				};
			}
		});

		// redirect the console inside the Terminal component
		const consoleElement = document.getElementById('console');
		const observer = new MutationObserver((mutations) => {
			let currentContent = consoleElement.innerText;
			let newContent = currentContent.replace(previousContent, '');
			if (newContent) {
				if (newContent.startsWith('Exception')) {
					let errors = parseRunningErrors(newContent, $openedArchive);
					$terminalOutput = [...$terminalOutput, newContent.split('\n')[0]];
					errors.frames.forEach((frame) => {
						if (frame.filePath === null) {
							const buttonTag = `${frame.fullLogLine}`;
							$terminalOutput = [...$terminalOutput, buttonTag];
						} else {
							if (frame.lineNumber === null) {
								frame.lineNumber = 1;
								const buttonTag = `<button class="terminal-link-button" data-filepath="${
									frame.filePath
								}" data-linenumber="${frame.lineNumber}">${frame.fullLogLine}</button>`;
								$terminalOutput = [...$terminalOutput, buttonTag];
							} else {
								const buttonTag = `<button class="terminal-link-button" data-filepath="${frame.filePath}" data-linenumber="${frame.lineNumber}">${frame.fullLogLine}</button>`;
								$terminalOutput = [...$terminalOutput, buttonTag];
							}
						}
					});
				} else {
					terminalOutput.update((output) => [...output, newContent]);
				}
			}
			previousContent = currentContent;
		});

		observer.observe(consoleElement, { childList: true, characterData: true, subtree: true });

		return () => {
			observer.disconnect();
		};
	});

	function parseRunningErrors(stackTrace, projectStructure) {
		const lines = stackTrace.split('\n');
		let parsedStackTrace = {
			exception: null,
			frames: []
		};

		if (lines.length > 0) {
			// Parse the first line for the exception message
			parsedStackTrace.exception = lines[0].trim();

			// Regular expression to match the stack frame lines, making file and line optional
			const frameRegex = /at ([\w.]+)\((?:([\w.]+):(\d+)|Unknown Source)\)/;

			for (const line of lines) {
				const match = line.match(frameRegex);
				if (match) {
					const fullyQualifiedMethodName = match[1];
					const fileName = match[2];
					const lineNumber = match[3] ? parseInt(match[3], 10) : null;
					const fullLogLine = match[0];

					let filePath = null;
					if (fileName !== 'Unknown Source') {
						// Extract class path from the fully qualified method name
						const classPath = fullyQualifiedMethodName.substring(
							0,
							fullyQualifiedMethodName.lastIndexOf('.')
						);
						// Resolve the full file path from the class path
						filePath = resolveFilePath(classPath, projectStructure);

						if (filePath) {
							filePath = filePath.slice(1);
						}
					}

					parsedStackTrace.frames.push({
						methodName: fullyQualifiedMethodName,
						filePath,
						lineNumber,
						fullLogLine
					});
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
</script>

<div id="popup">
	<button on:click={stopExecution} aria-label="Close">
		<svg viewBox="0 0 24 24" width="12" height="12" fill="black">
			<path
				d="M18.3,5.7l-1.4-1.4L12,9.2L7.1,4.3L5.7,5.7l4.9,4.9l-4.9,4.9l1.4,1.4l4.9-4.9l4.9,4.9l1.4-1.4l-4.9-4.9
                     L18.3,5.7z"
			/>
		</svg>
	</button>
	<iframe
		id="cheerpjIframe"
		title="Output"
		bind:this={iframe}
		width={$cheerpjWidth}
		height={$cheerpjHeight}
	/>
	<p id="console" />
</div>

<style lang="scss">
	#popup {
		display: none;
		position: fixed;
		flex-direction: column;
		top: 40%;
		left: 50%;
		padding: 10px;
		transform: translate(-40%, -50%);
		background-color: rgb(51, 51, 51);

		#cheerpjIframe {
			border: 0;
		}

		p {
			display: none;
		}

		button {
			align-self: flex-start;
			display: flex;
			justify-content: center;
			align-items: center;
			width: 16px;
			height: 16px;
			border-radius: 50%;
			border: none;
			background-color: #ff3b30;
			padding: 0;
			padding-top: 1px;
			cursor: pointer;
			box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);

			svg {
				width: 12px;
				height: 12px;
				fill: black;
			}

			&:hover {
				background-color: darken(#ff3b30, 10%);
			}

			&:active {
				background-color: darken(#ff3b30, 20%);
			}
		}
	}
</style>
