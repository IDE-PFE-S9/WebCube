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

<!-- TODO: change the code to use button instead of div -->
<!-- svelte-ignore a11y-click-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div class="tab-item" on:click={selectTab} class:selected={$selectedFile.name === currentFile.name}>
	<FileIcon {fileExtension} />
	<div class="tab-item-name">{currentFile.name}</div>
	<div
		class="delete-icon"
		on:click={closeTab}
		class:selected={$selectedFile.name === currentFile.name}
	>
		<DeleteIcon />
	</div>
</div>

<style lang="scss">
	.tab-item {
		display: flex;
		flex-direction: row;
		align-items: center;
		padding: 0 10px;
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
	}

	.tab-item-name {
		display: flex;
		justify-content: center;
		align-items: center;
		margin: 0 10px;
	}

	.delete-icon {
		display: flex;
		align-items: center;
		fill: rgb(45, 45, 45);

		&:hover {
			width: 1.5rem;
			height: 1.5rem;
			background-color: rgb(60, 60, 60);
			border-radius: 5px;

			&.selected {
				background-color: (50, 50, 50);
			}
		}
	}
</style>
