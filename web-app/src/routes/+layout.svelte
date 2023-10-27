<script>
	import FileExplorer from '$lib/FileExplorer/FileExplorer.svelte';
	import TabManager from '$lib/TabManager/TabManager.svelte';
	import Navbar from '$lib/Navbar/Navbar.svelte';
	import MonacoEditor from '$lib/MonacoEditor/MonacoEditor.svelte';
	import Terminal from '../lib/Terminal/Terminal.svelte';
	import StructureParser from '../lib/StructureParser/StructureParser.svelte';
	import WcArchiveExplorer from '../lib/ArchiveExplorer/WCArchiveExplorer.svelte';
	import ArchiveTabManager from '../lib/TabManager/ArchiveTabManager.svelte';
	import MarkdownViewer from '../lib/MarkdownViewer/MarkdownViewer.svelte';

	import { archiveMode, markdownMode } from '$lib/stores.js';
</script>

<div class="main">
	<Navbar />
	<div class="mid">
		{#if $archiveMode}
			<WcArchiveExplorer />
		{:else}
			<FileExplorer />
		{/if}
		<StructureParser />
	</div>
	<div class="right">
		{#if $archiveMode}
			<ArchiveTabManager />
		{:else}
			<TabManager />
		{/if}
		{#if $markdownMode}
			<MarkdownViewer />
		{:else}
			<MonacoEditor />
			<Terminal />
		{/if}
	</div>
	<slot />
</div>

<style lang="scss">
	* {
		box-sizing: border-box;
		margin: 0;
		padding: 0;
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
	}
</style>
