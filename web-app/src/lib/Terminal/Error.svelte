<script>
    export let file;
	export let lineNumber;
	export let codeSnippet;
	export let errorMessage;

    import { goToLineColumnTrigger } from '$lib/stores.js';

	function findErrorColumn(codeSnippet) {
		// Split the snippet into lines
		let lines = codeSnippet.split('\n');

		if (lines.length >= 2) {
			// The second line contains the caret
			const caretLine = lines[1];

			// Find the index of the caret, which corresponds to the column number
			const columnIndex = caretLine.indexOf('^');
			return columnIndex + 1; // Adding 1 because columns are usually 1-indexed
		}

		return -1; // Return -1 or any error indicator if the caret is not found
	}

    function goToLineColumn() {
        goToLineColumnTrigger.set({file, lineNumber, column});
    }
    
    let column = findErrorColumn(codeSnippet);

</script>

<button class="error" on:click={goToLineColumn}>
	<p>{errorMessage} [Ln {lineNumber}, Col {column}]</p>
</button>


<style lang="scss">
    .error {
        all: unset;
        width: 100%;
        background-color: #1e1e1e;
        border-left: 2px solid #ff3860;
        padding: 0.2rem 0.5rem;
        margin-bottom: 0.5rem;
        margin-left: 0.7rem;

        &:hover {
            background-color: #2e2e2e;
        }
    }

    .error p {
        margin: 0;
        font-size: 0.9rem;
    }
</style>