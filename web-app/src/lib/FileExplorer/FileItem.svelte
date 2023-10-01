<script>
	export let file;

	import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
	import { selectedFile, openedCode } from './selectedFileStore.js';

	let fileExtension = file?.name.split('.').pop();

	async function openFile() {
		selectedFile.set(file);
		const fileHandle = file.handle;
		try {
			if (fileHandle) {
				const file = await fileHandle.getFile();
				const contents = await file.text();
				openedCode.set(contents);
			} else {
				console.error('File handle is not available');
			}
		} catch (error) {
			console.error('Error reading file:', error);
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
		<FileIcon fileExtension={fileExtension} />
		{file.name}
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
