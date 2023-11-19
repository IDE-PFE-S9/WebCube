<script>
	import FileExplorer from '$lib/FileExplorer/FileExplorer.svelte';
	import TabManager from '$lib/TabManager/TabManager.svelte';
	import Navbar from '$lib/Navbar/Navbar.svelte';
	import MonacoEditor from '$lib/MonacoEditor/MonacoEditor.svelte';
	import Terminal from '$lib/Terminal/Terminal.svelte';
	import StructureParser from '$lib/StructureParser/StructureParser.svelte';
	import WcArchiveExplorer from '$lib/ArchiveExplorer/WCArchiveExplorer.svelte';
	import ArchiveTabManager from '$lib/TabManager/ArchiveTabManager.svelte';
	import MarkdownViewer from '$lib/MarkdownViewer/MarkdownViewer.svelte';
	import UserPanel from '$lib/UserPanel/UserPanel.svelte';
	import GitExplorer from '../../lib/GitExplorer/GitExplorer.svelte';

	import { archiveMode, markdownMode, currentTab } from '$lib/stores.js';
	import Diagram from '../../lib/Modelling/Diagram.svelte';
</script>

<div class="main">
	<Navbar />
	{#if $currentTab == 'Utilisateur'}
		<UserPanel />
	{:else if $currentTab == 'Uml'}
		<Diagram />
	{:else}
		<div class="mid">
			{#if $currentTab == 'Archive'}
				<WcArchiveExplorer />
			{:else if $currentTab == 'Explorer'}
				<GitExplorer />
			{/if}
			<StructureParser />
		</div>
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
