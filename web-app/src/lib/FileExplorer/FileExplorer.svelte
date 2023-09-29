<script>
	import FileItem from './FileItem.svelte';

	let path;
	let files = ['test.java', 'index.java', 'main.java'];

	let selectedFile = null;

	function selectFile(file) {
		selectedFile = file;
	}
</script>

<div class="file-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	<div class="files">
		{#each files as file}
			<FileItem {file} {selectFile} {selectedFile} />
		{/each}
	</div>
</div>

<style lang="scss">
	.file-explorer {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		width: 20rem;
		height: 100%;

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				letter-spacing: 0.1rem;
				color: rgb(187, 187, 187);
			}
		}

		.files {
			display: flex;
			flex-direction: column;
		}
	}
</style>
