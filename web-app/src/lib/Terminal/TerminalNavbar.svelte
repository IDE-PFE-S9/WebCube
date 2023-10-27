<script>
	import TerminalNavItem from './TerminalNavItem.svelte';
	import { terminalNavbarActiveItem, terminalOutput, archiveMode } from '$lib/stores.js';
	import TerminalNavbarClearButton from './TerminalNavbarClearButton.svelte';
	import TerminalNavbarRunButton from './TerminalNavbarRunButton.svelte';
	import TerminalNavbarSaveButton from './TerminalNavbarSaveButton.svelte';
	import TerminalNavbarCompilButton from './TerminalNavbarCompilButton.svelte';
	import TerminalNavbarSaveArchiveButton from './TerminalNavbarSaveArchiveButton.svelte';

	const navbarItems = [{ text: 'Problèmes' }, { text: 'Sortie' }];

	function manageItemClick(itemText) {
		terminalNavbarActiveItem.set(itemText);
	}
</script>

<div class="navbar">
	<div class="navbar-buttons">
		{#each navbarItems as { text }}
			<TerminalNavItem {text} onClick={() => manageItemClick(text)} />
		{/each}
	</div>
	{#if $terminalNavbarActiveItem === 'Sortie'}
		<div class="navbar-buttons">
			{#if $archiveMode}
				<TerminalNavbarSaveArchiveButton />
			{:else}
				<TerminalNavbarSaveButton />
			{/if}
			<TerminalNavbarCompilButton />
			<TerminalNavbarRunButton />
			<TerminalNavbarClearButton />
		</div>
	{/if}
</div>

<style lang="scss">
	.navbar {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding-left: 1rem;
		padding-right: 1rem;

		.navbar-buttons {
			display: flex;
			flex-direction: row;
			align-items: flex-start;
			gap: 0.6rem;
		}
	}
</style>
