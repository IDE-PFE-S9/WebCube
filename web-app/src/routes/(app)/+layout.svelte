<script>
	import TabManager from '$lib/TabManager/TabManager.svelte';
	import Navbar from '$lib/Navbar/Navbar.svelte';
	import MonacoEditor from '$lib/MonacoEditor/MonacoEditor.svelte';
	import Terminal from '$lib/Terminal/Terminal.svelte';
	import StructureParser from '$lib/StructureParser/StructureParser.svelte';
	import WcArchiveExplorer from '$lib/ArchiveExplorer/WCArchiveExplorer.svelte';
	import ArchiveTabManager from '$lib/TabManager/ArchiveTabManager.svelte';
	import MarkdownViewer from '$lib/MarkdownViewer/MarkdownViewer.svelte';
	import UserPanel from '$lib/UserPanel/UserPanel.svelte';
	import GitExplorer from '$lib/GitExplorer/GitExplorer.svelte';
	import Diagram from '$lib/Modeling/Diagram.svelte';
	import ConfigPanel from '$lib/Modeling/ConfigPanel.svelte';

	import { markdownMode, currentTab } from '$lib/stores.js';

	let startX;
	let startWidth;
	let dragging = false;

	function onMouseDown(event) {
		startX = event.clientX;
		startWidth = document.querySelector('.mid').clientWidth;
		dragging = true;
		document.body.classList.add('no-select'); // Disable text selection

		document.addEventListener('mousemove', onMouseMove);
		document.addEventListener('mouseup', onMouseUp);
	}

	function onMouseMove(event) {
		if (dragging) {
			const currentWidth = startWidth + event.clientX - startX;
			document.querySelector('.mid').style.width = `${currentWidth}px`;
		}
	}

	function onMouseUp() {
		dragging = false;
		document.body.classList.remove('no-select'); // Re-enable text selection

		document.removeEventListener('mousemove', onMouseMove);
		document.removeEventListener('mouseup', onMouseUp);
	}
</script>

<div class="main">
	<Navbar />
	{#if $currentTab == 'Utilisateur'}
		<UserPanel />
	{:else if $currentTab == 'Uml'}
		<div class="mid">
			<ConfigPanel />
		</div>
		<div class="right">
			<Diagram />
		</div>
	{:else}
		<div class="mid">
			{#if $currentTab == 'Archive'}
				<WcArchiveExplorer />
			{:else if $currentTab == 'Explorer'}
				<GitExplorer />
			{/if}
			<StructureParser />
		</div>
		<!-- <div class="resizer" on:mousedown={onMouseDown} /> -->
		<div class="right">
			{#if $currentTab == 'Archive'}
				<ArchiveTabManager />
			{:else if $currentTab == 'Explorer'}
				<TabManager />
			{/if}
			{#if $markdownMode}
				<MarkdownViewer />
			{:else}
				<MonacoEditor />
				<Terminal />
			{/if}
		</div>
	{/if}
	<slot />
</div>

<style lang="scss">
	* {
		box-sizing: border-box;
		margin: 0;
		padding: 0;
	}

	.no-select {
		user-select: none;
		-webkit-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
	}

	.main {
		display: flex;
		flex-direction: row;
		font-family: 'Roboto', sans-serif;
		height: 100%;
		width: 100%;
		background-color: #1e1e1e;

		position: absolute;
		top: 0;
		left: 0;

		.mid {
			display: flex;
			flex-direction: column;
			width: 20rem;
			height: 100%;
		}

		.right {
			display: flex;
			flex-direction: column;
			width: 100%;
			height: 100%;
			overflow: hidden; /* Add this line to prevent scrolling on the right container */
		}

		.resizer {
			width: 5px;
			cursor: col-resize;
			background-color: transparent;
			height: 100%;
			position: relative; /* Needed for absolute positioning of the pseudo-element */

			&:hover::after {
				content: '';
				position: absolute;
				top: 0;
				right: 0;
				bottom: 0;
				left: 0;
				background-color: rgb(54, 117, 182);
				animation: fadeInBackground 0.5s forwards; /* Animation to delay the background appearance */
			}
		}

		@keyframes fadeInBackground {
			0% {
				background-color: transparent;
			}
			99% {
				background-color: transparent;
			}
			100% {
				background-color: rgb(54, 117, 182);
			}
		}
	}
</style>
