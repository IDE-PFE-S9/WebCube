<script>
	import DirectoryItem from './DirectoryItem.svelte';
	import FileItem from './FileItem.svelte';
	import DirectoryIcon from './DirectoryIcon.svelte';

	export let directory;
	let expanded = false;

	function toggleExpand() {
		expanded = !expanded;
	}
</script>

<div id="container">
	<button class="header" on:click={toggleExpand}>
		<svelte:component this={DirectoryIcon} />
		{directory.name}
	</button>
	{#if expanded}
		<ul class="files">
			{#each directory.children as item }
				{#if item.type === 'directory'}
					<li>
						<DirectoryItem directory={item} />
					</li>
				{:else}
					<li class="file-item-li">
						<FileItem file={item} />
					</li>
				{/if}
			{/each}
		</ul>
	{/if}
</div>

<style lang="scss">
	#container {
		overflow: scroll;
		width: 100%;

		.header {
			all: unset;
			display: flex;
			flex-direction: row;
			align-items: center;
			gap: 0.3rem;
			color: rgb(204, 204, 204);
			padding: 0.3rem;
			padding-left: 0;
			font-size: 0.9rem;
			width: 100%;

			&:hover {
				background-color: rgb(43, 45, 46);
			}
		}

		.files {
			list-style: None;
			margin: 0;
			padding-left: 1.5rem;
		
		}
	}
</style>
