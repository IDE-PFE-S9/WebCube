<script>
	import ArchiveDirectoryItem from './ArchiveDirectoryItem.svelte';
	import ArchiveFileItem from './ArchiveFileItem.svelte';
	import DirectoryIcon from '$lib/assets/FileExplorerIcons/DirectoryIcon.svelte';

	export let directory;
	let expanded = false;

	function toggleExpand() {
		expanded = !expanded;
	}
</script>

<div id="container" class:hidden={!directory.visible}>
	<button class="header" on:click={toggleExpand}>
		<DirectoryIcon />
		{directory.name}
	</button>
	{#if expanded}
		<div class="files">
			{#each directory.children as item}
				{#if item.type === 'directory'}
					<ArchiveDirectoryItem directory={item} />
				{:else}
					<ArchiveFileItem file={item} />
				{/if}
			{/each}
		</div>
	{/if}
</div>

<style lang="scss">
	#container {
		width: 100%;

		.header {
			all: unset;
			display: flex;
			flex-direction: row;
			align-items: center;
			gap: 0.3rem;
			color: rgb(204, 204, 204);
			padding: 0.3rem;
			padding-left: 0.7rem;
			font-size: 0.9rem;
			width: 100%;
			cursor: pointer;

			&:hover {
				background-color: rgb(43, 45, 46);
			}
		}

		.files {
			list-style: None;
			margin: 0;
			padding-left: 0.7rem;
		}
	}

    #container.hidden {
        display: none;
    }
</style>
