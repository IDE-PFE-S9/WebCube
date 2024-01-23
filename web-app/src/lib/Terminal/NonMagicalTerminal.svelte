<script>
	import TerminalNavbar from './NonMagicalTerminalNavbar.svelte';
	import Out from './Out.svelte';
	import { terminalNavbarActiveItem } from '$lib/stores.js';
	import Problems from './Problems.svelte';
</script>

<div class="terminal">
	<TerminalNavbar />
	{#if $terminalNavbarActiveItem === 'Problèmes'}
		<Problems />
	{:else if $terminalNavbarActiveItem === 'Sortie'}
		<Out />
	{/if}
</div>

<style lang="scss">
	.terminal {
		min-height: 30%;
		max-height: 30%;
		max-width: 100%;
		display: flex;
		flex-direction: column;
		background-color: #1e1e1e;
		border-top: 1px solid rgb(65, 65, 65);
	}
</style>
