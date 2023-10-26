<script>
	import DirectoryItem from './DirectoryItem.svelte';
	import { openedDirectory, archiveMode } from '$lib/stores.js';

	$: directoryObject = {};

	async function openAndListDirectoryRecursive() {
		try {
			const directoryHandle = await window.showDirectoryPicker();
			const directoryStructure = await listFilesRecursive(directoryHandle);
			// console.log(JSON.stringify(directoryStructure, null, 2));
			directoryObject = directoryStructure;
		} catch (error) {
			console.error('Error:', error);
		}
	}

	async function listFilesRecursive(handle, path = '') {
		if (!handle) {
			console.error('No directory selected');
			return;
		}

		const directoryObject = {
			type: 'directory',
			name: handle.name ? `${path}/${handle.name}` : 'root', // Adjusted here for the root
			children: []
		};

		// Get an iterator for the entries in the directory
		const iterator = handle.values();

		// Iterate through the entries
		for await (const entry of iterator) {
			const entryPath = `${path}/${handle.name}/${entry.name}`; // Construct the full path here
			const entryObject = {
				type: entry.kind,
				name: entryPath, // Use the full path as the name
				handle: entry
			};

			// If the entry is a directory, recurse into it
			if (entry.kind === 'directory') {
				const childDirectoryObject = await listFilesRecursive(entry, entryPath); // Pass the full path for recursion
				directoryObject.children.push(childDirectoryObject);
			} else {
				directoryObject.children.push(entryObject);
			}
		}

		// sort the files by alphabetical order and by type
		directoryObject.children.sort((a, b) => {
			// If both are the same type, compare by name
			if (a.type === b.type) {
				return a.name.localeCompare(b.name);
			}
			// Otherwise, directories come first
			return a.type === 'directory' ? -1 : 1;
		});

		openedDirectory.set(directoryObject);
		archiveMode.set(false)
		return directoryObject;
	}
</script>

<div class="file-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	{#if directoryObject.name}
		<DirectoryItem directory={directoryObject} />
	{:else}
		<button on:click={openAndListDirectoryRecursive} class="button-open-directory"
			>Open Directory</button
		>
	{/if}
</div>

<style lang="scss">
	.file-explorer {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		min-width: 20rem;
		height: 70%;
		overflow: scroll;

		// Base styles for the scrollbar
		&::-webkit-scrollbar {
			width: 12px;
			background-color: rgba(255, 255, 255, 0); // Fully transparent track
		}

		// Styling the thumb (the draggable scrolling element)
		&::-webkit-scrollbar-thumb {
			background-color: rgba(255, 255, 255, 0); // Fully transparent by default
			border: 2px solid transparent;
			background-clip: content-box; // Makes the thumb smaller than the track
			transition: background-color 1s ease; // Transition effect for the hover state
		}

		// Hiding the corner where horizontal and vertical scrollbars meet
		&::-webkit-scrollbar-corner {
			background-color: transparent;
		}

		// Styles when the container is hovered
		&:hover {
			// Make the thumb semi-transparent white on hover
			&::-webkit-scrollbar-thumb {
				background-color: rgba(255, 255, 255, 0.1);
			}

			// Hide the horizontal scrollbar on hover
			&::-webkit-scrollbar:horizontal {
				display: none;
			}
		}

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				font-weight: 300;
				color: rgb(187, 187, 187);
			}
		}

		.button-open-directory {
			font-size: 1rem;
			padding: 1rem 0.5rem;
			margin-top: 1rem;
			width: 70%;
			align-self: center;
			background-color: rgb(54, 117, 182);
			border: none;
			color: white;
			border-radius: 0.1rem;

			&:hover {
				background-color: rgb(41, 100, 158);
			}

			&:active {
				background-color: rgb(30, 75, 118);
			}
		}
	}
</style>
