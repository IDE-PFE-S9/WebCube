<script>
	import { terminalOutput, goToLineTrigger } from '$lib/stores.js';
	import { onMount, afterUpdate } from 'svelte';

	let terminal;

	function scrollToBottom() {
		terminal.scrollTop = terminal.scrollHeight;
	}

	onMount(() => {
		scrollToBottom();
		attachEventListeners();
	});

	afterUpdate(() => {
		scrollToBottom();
		attachEventListeners();
	});

	function attachEventListeners() {
		const buttons = terminal.querySelectorAll('button');
		buttons.forEach((button) => {
			const filePath = button.getAttribute('data-filepath');
			const lineNumber = button.getAttribute('data-linenumber');
			console.log(lineNumber)
			button.addEventListener('click', () => goToLine(filePath, lineNumber));
		});
	}

	function goToLine(filePath, lineNumber) {
		console.log('goToLine', filePath, lineNumber);
		goToLineTrigger.set({ filePath, lineNumber });
	}
</script>

<div class="out" bind:this={terminal}>
	{#each $terminalOutput as entry}
		<p class="out-text">
			{@html entry
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/\n/g, '<br>')
				.replace(/&lt;button(.*?)&gt;(.*?)&lt;\/button&gt;/g, '<button$1>$2</button>')}
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
			margin-block-end: 0;
		}

		&:last-child {
			margin-block-end: 0.5rem;
		}
	}

	:global(.terminal-link-button) {
		all: unset;
		cursor: pointer;
		text-decoration: underline;
	}
</style>
