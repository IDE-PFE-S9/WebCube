<script>
	// Import markdown conversion library
	import * as marked from 'marked';
	import { openedCodes, selectedFile, selectedArchiveFile } from '$lib/stores.js';

    let filename = $selectedArchiveFile;
    console.log(filename)
	console.log($selectedArchiveFile)
	let codeObj = $openedCodes.find((code) => code.name === filename);
    console.log($openedCodes)
	console.log(codeObj);
</script>

<div class="preview">
	{@html marked.marked(codeObj.code)}
</div>

<style lang="scss">
	.preview {
		box-sizing: border-box;
		display: block;
		width: 100%;
		height: 96%;
		padding: 2rem 4rem;
		font-family: 'DM Sans', 'Roboto', sans-serif;
        overflow: scroll;
        color: white;
        background-color: #1e1e1e;
	}
</style>
