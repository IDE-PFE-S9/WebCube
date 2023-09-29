<script>
	import DirectoryItem from './DirectoryItem.svelte';

	$: directoryObject = {};

	async function openAndListDirectoryRecursive() {
		try {
			const directoryHandle = await window.showDirectoryPicker();
			const directoryStructure = await listFilesRecursive(directoryHandle);
			console.log(JSON.stringify(directoryStructure, null, 2));
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
			name: handle.name || 'root',
			children: []
		};

		// Get an iterator for the entries in the directory
		const iterator = handle.values();

		// Iterate through the entries
		for await (const entry of iterator) {
			const entryObject = {
				type: entry.kind,
				name: entry.name,
				handle: entry 
			};

			// If the entry is a directory, recurse into it
			if (entry.kind === 'directory') {
				const childDirectoryObject = await listFilesRecursive(entry, `${path}/${entry.name}`);
				directoryObject.children.push(childDirectoryObject);
			} else {
				directoryObject.children.push(entryObject);
			}
		}

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
		width: 20rem;
		height: 100%;

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				letter-spacing: 0.1rem;
				color: rgb(187, 187, 187);
			}
		}

		.button-open-directory {
			font-size: 1rem;
			padding: 1rem 0.5rem;
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
