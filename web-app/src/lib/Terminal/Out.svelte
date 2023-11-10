<script>
	import { terminalOutput } from '$lib/stores.js';
	import { onMount, afterUpdate } from 'svelte';

    let terminal;

    function scrollToBottom() {
        terminal.scrollTop = terminal.scrollHeight;
    }

    onMount(scrollToBottom);
    afterUpdate(scrollToBottom);
</script>

<div class="out" bind:this={terminal}>
	{#each $terminalOutput as entry}
		<p class="out-text">
			{@html entry.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, '<br>')}
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
