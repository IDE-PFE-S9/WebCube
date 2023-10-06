<script>
	import FileExplorer from '$lib/FileExplorer/FileExplorer.svelte';
  	import Navbar from "$lib/Navbar/Navbar.svelte";
	import MonacoEditor from '$lib/MonacoEditor/MonacoEditor.svelte';
	import Terminal from '../lib/Terminal/Terminal.svelte';
	import StructureParser from '../lib/StructureParser/StructureParser.svelte';
</script>

<div class="main">
    <Navbar />
	<div class="mid">
		<FileExplorer />
		<StructureParser />
	</div>
	<div class="right">
		<MonacoEditor />
		<Terminal />
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
		font-family: "Roboto", sans-serif;
        height: 100%;
        width: 100%;

        position: absolute;
        top: 0;
        left: 0;

		.mid{
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
		}
	}
</style>
