<script>
	export let file;

	import FileIcon from './FileIcon.svelte';
	import { selectedFile, openedCode } from './selectedFileStore.js';

	async function openFile() {
		selectedFile.set(file);
		const fileHandle = file.handle;
        try {
            if (fileHandle) {
                const file = await fileHandle.getFile();
                const contents = await file.text();
				openedCode.set(contents);
                // console.log(contents);  // Log the file contents to the console
            } else {
                console.error('File handle is not available');
            }
        } catch (error) {
            console.error('Error reading file:', error);
        }
	}
</script>

<button
	on:click={openFile}
	on:keypress={openFile}
	class:selected={file === $selectedFile}
	class="file"
>
	<svelte:component this={FileIcon} />
	{file.name}
</button>

<style lang="scss">
	.file {
		all: unset;
		display: flex;
		align-items: center;
		color: rgb(204, 204, 204);
		padding: 0.3rem;
		padding-left: 0;
		font-size: 0.9rem;
		gap: 0.3rem;
		width: 100%;

		&:hover {
			background-color: rgb(43, 45, 46);
		}

		&.selected {
			background-color: rgb(55, 55, 60);
		}
	}
</style>
