<script>
	import { terminalOutput } from '$lib/stores.js';

	console.log($terminalOutput);
</script>

<div class="out">
	{#each $terminalOutput as entry}
		<p class="out-text">
			{entry}
		</p>
	{/each}
</div>

<style lang="scss">
	.out {
		padding-left: 2rem;
		color: #d4d4d4;
		overflow-x: scroll;

		&::-webkit-scrollbar {
			display: none;
		}

		.out-text {
            all: unset;
            margin-block-end: 0.5rem;
			display: block;
			font-family: Menlo, Monaco, 'Courier New', monospace;
			font-size: 0.75rem;
		}
	}
</style>
