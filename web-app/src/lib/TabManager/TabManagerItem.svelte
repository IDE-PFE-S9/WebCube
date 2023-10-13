<script>
	import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
	import DeleteIcon from '/src/lib/assets/TabManagerIcons/DeleteIcon.svelte';
	import { openedTabs, openedCodes, selectedFile, editorUpdateTrigger } from '$lib/stores.js';

	export let currentFile;

	let fileExtension = currentFile.name.split('.').pop();

	function selectTab() {
		selectedFile.set(currentFile);
		editorUpdateTrigger.set($selectedFile);
	}

	function closeTab() {
		// Keep a reference to the current selected file name before updating the stores
		const currentSelectedFileName = $selectedFile?.name;

		// Update the openedTabs and openedCodes stores to remove the closed tab
		openedTabs.update((tabs) => tabs.filter((file) => file.name !== currentFile.name));
		openedCodes.update((codes) => codes.filter((code) => code.name !== currentFile.name));

		// Get the updated list of tabs
		const updatedTabs = $openedTabs;

		// If the closed tab was the currently selected tab, select the last tab if it exists
		if (currentSelectedFileName === currentFile.name) {
			if (updatedTabs.length > 0) {
				selectedFile.set(updatedTabs[updatedTabs.length - 1]);
			} else {
				selectedFile.set(null); // Set selectedFile to null if there are no more tabs
			}
		}
		// Trigger an update to the editor content
		editorUpdateTrigger.set($selectedFile);
	}
</script>

<button
	class="tab-item"
	on:click={selectTab}
	class:selected={$selectedFile.name === currentFile.name}
>
	<FileIcon {fileExtension} />
	<div class="tab-item-name">{currentFile.name.split('/').pop()}</div>
	<button
		class="delete-icon"
		on:click={closeTab}
		class:selected={$selectedFile.name === currentFile.name}
	>
		<DeleteIcon />
	</button>
</button>

<style lang="scss">
	.tab-item {
		all: unset;
		display: flex;
		flex-direction: row;
        align-items: center;
        justify-content: center;
		padding: 0px 10px;
		cursor: pointer;
		color: rgb(136, 136, 136);
		border-left: 1px solid rgb(41, 41, 41);
		border-right: 1px solid rgb(41, 41, 41);

		&.selected {
			border-top: 3px solid rgb(54, 117, 182);
			background-color: rgb(30, 30, 30);
			color: white;

			.delete-icon {
				fill: white;
			}
		}

		&:hover {
			background-color: rgb(30, 30, 30);

			.delete-icon {
				fill: rgb(158, 158, 158);
			}
		}

		.tab-item-name {
			display: flex;
			justify-content: center;
			align-items: center;
			margin: 0 10px;
			font-size: 0.85rem;
		}

		.delete-icon {
			all: unset;
			display: flex;
			align-items: center;
            justify-content: center;
			fill: rgb(45, 45, 45);
			border-radius: 5px;

			&:hover {
				background-color: rgb(60, 60, 60);
			
				&.selected {
					background-color: (50, 50, 50);
				}
			}
		}
	}
</style>
