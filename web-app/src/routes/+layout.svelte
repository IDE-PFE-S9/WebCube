<script>
	import FileExplorer from '$lib/FileExplorer/FileExplorer.svelte';
  import Navbar from "$lib/Navbar/Navbar.svelte";
</script>

<div class="main">
   <Navbar />
	<FileExplorer />
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
	}
</style>
