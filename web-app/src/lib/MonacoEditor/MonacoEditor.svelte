<script>
	import { onMount } from 'svelte';
	import {
		openedCodes,
		selectedFile,
		editorUpdateTrigger,
		archiveMode,
		selectedArchiveFile,
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
