<script>
	export let file;

	import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
	import {
		selectedArchiveFile,
		openedCodes,
		openedArchiveTabs,
		editorUpdateTrigger
	} from '$lib/stores.js';

	let fileExtension = file?.name.split('.').pop();
	let relativeName = file.name.split('/').pop();

	async function openFile() {
		selectedArchiveFile.set(file.name);

		openedArchiveTabs.update((tabs) => {
			if (!tabs.includes(file.name)) {
				return [...tabs, file.name];
			}
			return tabs;
		});

		openedCodes.update((codes) => {
			// Check if the code from this file is already opened
			const isAlreadyOpened = codes.some((code) => code.name === file.name);
			if (!isAlreadyOpened) {
				return [
					...codes,
					{
						name: file.name,
						code: file.data,
						status: 'saved'
					}
				];
			}
			console.log($openedCodes);
			return codes;
		});

		editorUpdateTrigger.set(file);
	}
</script>

<div class="container">
	<button on:click={openFile} on:keypress={openFile} class:selected={file.name === $selectedArchiveFile} class="file">
		<FileIcon {fileExtension} />
		{relativeName}
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
