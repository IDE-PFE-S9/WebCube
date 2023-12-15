<script>
	import { onMount } from 'svelte';
	import {
		openedCodes,
		selectedFile,
		editorUpdateTrigger,
		goToLineTrigger,
		goToLineColumnTrigger,
		archiveMode,
		selectedArchiveFile,
		openedArchive,
		openedArchiveTabs,
		readOnly,
		logs
	} from '$lib/stores.js';

	let editor;
	let editorContainer;

	const updateEditorContent = () => {
		// console.log('updateEditorContent', $selectedFile, $openedCodes);
		let codeObj;
		const codes = $openedCodes;
		if ($archiveMode) {
			if (!$selectedArchiveFile) {
				editor.setValue(''); // Clear the editor content if selectedFile is null
				return;
			}
			codeObj = codes.find((code) => code.name === $selectedArchiveFile);
		} else {
			if (!$selectedFile) {
				editor.setValue(''); // Clear the editor content if selectedFile is null
				return;
			}
			const selectedFileName = $selectedFile?.name;
			codeObj = codes.find((code) => code.name === selectedFileName);
		}
		if (codeObj && editor && codeObj.code !== editor.getValue()) {
			editor.setValue(codeObj.code);
		}
	};

	const editorGoToLine = async (file, line) => {
		let lineNumber = parseInt(line);
		selectedArchiveFile.set(file);
		openedArchiveTabs.update((tabs) => {
			if (!tabs.includes(file)) {
				return [...tabs, file];
			}
			return tabs;
		});
		openedCodes.update((codes) => {
			// Check if the code from this file is already opened
			const isAlreadyOpened = codes.some((code) => code.name === file);
			if (!isAlreadyOpened) {
				let fileCode = getFileCode(file);
				console.log(fileCode);
				return [
					...codes,
					{
						name: file,
						code: fileCode,
						status: 'saved'
					}
				];
			}
			return codes;
		});
		await editorUpdateTrigger.set(file);
		editor.revealLineInCenter(lineNumber, 0);
		editor.setPosition({ lineNumber: lineNumber, column: 1000 });
		editor.focus();
	};

	const editorGoToLineColumn = async (file, line, column) => {
		let lineNumber = parseInt(line);
		selectedArchiveFile.set(file);
		openedArchiveTabs.update((tabs) => {
			if (!tabs.includes(file)) {
				return [...tabs, file];
			}
			return tabs;
		});
		openedCodes.update((codes) => {
			// Check if the code from this file is already opened
			const isAlreadyOpened = codes.some((code) => code.name === file);
			if (!isAlreadyOpened) {
				let fileCode = getFileCode(file);
				console.log(fileCode);
				return [
					...codes,
					{
						name: file,
						code: fileCode,
						status: 'saved'
					}
				];
			}
			return codes;
		});
		await editorUpdateTrigger.set(file);
		editor.revealLineInCenter(lineNumber, 0);
		editor.setPosition({ lineNumber: lineNumber, column: column });
		editor.focus();
	};

	function getFileCode(file, directory) {
		// If the directory is not provided, use the root of the archive
		const currentDirectory = directory || $openedArchive;

		for (const child of currentDirectory.children) {
			if (child.type === 'file') {
				console.log(child.name, file);
			}
			if (child.type === 'file' && child.name === file) {
				return child.data; // Return the data if the file is found
			} else if (child.type === 'directory') {
				const found = getFileCode(file, child); // Recursively search in the subdirectory
				if (found) {
					return found; // Return the data if found in a subdirectory
				}
			}
		}

		return null; // Return null if the file is not found
	}

	onMount(async () => {
		const monaco = await import('monaco-editor');

		editor = monaco.editor.create(editorContainer, {
			value: '', // initial value can be an empty string
			language: 'java',
			theme: 'vs-dark',
			readOnly: $readOnly, // Set initial value based on the store
			automaticLayout: true
		});

		const updateCode = () => {
			const newValue = editor.getValue();

			// Update the openedCodes store with new content for the currently selected file
			openedCodes.update((codes) => {
				// Find the index of the code object for the currently selected file
				if (codes.length > 0) {
					let index;
					index = codes.findIndex((code) => code.name === $selectedArchiveFile);

					if (index !== -1) {
						// Create a new array with the updated code object
						const updatedCodes = [...codes];
						updatedCodes[index] = {
							...updatedCodes[index],
							code: newValue,
							status: 'unsaved' // Update the status to 'unsaved' since the content has changed
						};
						return updatedCodes;
					}
				}
				// If the currently selected file is not found in the openedCodes array,
				// return the existing codes array unchanged
				return codes;
			});
		};

		// Listen for changes in the editor content and update the store
		editor.onDidChangeModelContent(updateCode);

		editor.onDidPaste(() => {
			logs.update((currentLogs) => [...currentLogs, `Pasted text in ${$selectedFile.name}`]);
		});

		const updateEditorValue = () => {
			let codes = $openedCodes;
			let codeObj;
			if ($archiveMode) {
				codeObj = codes.find((code) => code.name === $selectedArchiveFile);
			}
			if (codeObj && editor && codeObj.code !== editor.getValue()) {
				editor.setValue(codeObj.code);
			}
		};

		// Initial update to set the editor value based on the initial value of the openedCodes store
		updateEditorValue();

		// Subscribe to the store to get its current value
		const unsubscribe = editorUpdateTrigger.subscribe(() => {
			updateEditorContent();
		});

		const unsubscribeGoToLine = goToLineTrigger.subscribe(({ filePath, lineNumber }) => {
			console.log('goToLineTrigger', filePath, lineNumber);
			if (filePath && lineNumber) {
				editorGoToLine(filePath, lineNumber);
			}
		});

		const unsubscribeGoToLineColumn = goToLineColumnTrigger.subscribe(
			({ file, lineNumber, column }) => {
				console.log('goToLineColumnTrigger', file, lineNumber, column);
				if (file && lineNumber && column) {
					editorGoToLineColumn(file, lineNumber, column);
				}
			}
		);

		const updateReadOnlyStatus = (status) => {
			if (editor) {
				editor.updateOptions({ readOnly: status });
			}
		};

		// Subscribe to the readOnly store
		const unsubscribeReadOnly = readOnly.subscribe(updateReadOnlyStatus);

		// Return a cleanup function to unsubscribe when the component is destroyed
		return () => {
			unsubscribeReadOnly();
			unsubscribe();
			unsubscribeGoToLine();
			unsubscribeGoToLineColumn();
		};
	});
</script>

<div id="editor-container" bind:this={editorContainer} />

<style lang="scss">
	#editor-container {
		height: 67%;
		width: 100%;
	}
</style>
