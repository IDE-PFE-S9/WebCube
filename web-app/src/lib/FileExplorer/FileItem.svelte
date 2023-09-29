<script>
	export let file;
	export let selectFile;
	export let selectedFile;
</script>


<button
	on:click={() => selectFile(file)}
	on:keypress={() => selectFile(file)}
	class:selected={file === selectedFile}
	class="file"
>
    <!-- TODO add icon-->
	{file}
</button>

<style lang="scss">
	.file {
		all: unset;
		display: flex;
		justify-content: space-between;
		align-items: center;
		color: rgb(204, 204, 204);
		padding: 0.3rem;
		padding-left: 1rem;
		font-size: 0.9rem;

		&:hover {
			background-color: rgb(43, 45, 46);
		}

		&.selected {
			background-color: rgb(55, 55, 60);
		}
	}
</style>
