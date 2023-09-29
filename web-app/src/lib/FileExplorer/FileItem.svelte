<script>
	export let file;

	import FileIcon from './FileIcon.svelte';
	import { selectedFile } from './selectedFileStore.js';

	function openFile() {
		selectedFile.set(file);
		console.log($selectedFile);  // Note the $ to access the value
	}
</script>

<button
	on:click={openFile}
	on:keypress={openFile}
	class:selected={file === $selectedFile}
	class="file"
>
	<svelte:component this={FileIcon} />
	{file}
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
