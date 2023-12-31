<script>
	export let file;

	import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
	import { selectedFile, openedCodes, openedTabs, editorUpdateTrigger, readOnly, markdownMode } from '$lib/stores.js';

	let fileExtension = file?.name.split('.').pop();

	async function openFile() {
		selectedFile.set(file);
		readOnly.set(false)

		openedTabs.update((tabs) => {
			if (!tabs.includes(file)) {
				return [...tabs, file];
			}
			return tabs;
		});

		const fileHandle = file.handle;
		try {
			if (fileHandle) {
				const file = await fileHandle.getFile();
				const contents = await file.text();
				openedCodes.update((codes) => {
					// Check if the code from this file is already opened
					const isAlreadyOpened = codes.some((code) => code.name === $selectedFile.name);
					if (!isAlreadyOpened) {
						return [
							...codes,
							{
								name: $selectedFile.name,
								code: contents,
								status: 'saved'
							}
						];
					}
					return codes;
				});
			} else {
				console.error('File handle is not available');
			}
		} catch (error) {
			console.error('Error reading file:', error);
		}
		editorUpdateTrigger.set(file);
		if (fileExtension === "md") {
			markdownMode.set(true);
		} else {
			markdownMode.set(false);
		}
	}
</script>

<div class="container">
	<button
		on:click={openFile}
		on:keypress={openFile}
		class:selected={file === $selectedFile}
		class="file"
	>
		<FileIcon {fileExtension} />
		{file.name.split('/').pop()}
	</button>
</div>

<style lang="scss">
	.container {
		width: 100%;

		.file {
			all: unset;
			display: flex;
			align-items: center;
			color: rgb(204, 204, 204);
			padding: 0.3rem;
			padding-left: 0.7rem;
			font-size: 0.9rem;
			gap: 0.3rem;
			width: 100%;
			cursor: pointer;

			&:hover {
				background-color: rgb(43, 45, 46);
			}

			&.selected {
				background-color: rgb(55, 55, 60);
			}
		}
	}
</style>
