<script>
	import { problems } from '$lib/stores.js';
	import Error from './Error.svelte';

	let problemsByFile = {};

	// Update problemsByFile whenever $problems changes
	$: {
		problemsByFile = $problems.reduce((acc, problem) => {
			if (!acc[problem.file]) {
				acc[problem.file] = [];
			}
			acc[problem.file].push(problem);
			return acc;
		}, {});

		// Sort problems by line number for each file
		Object.keys(problemsByFile).forEach((file) => {
			problemsByFile[file].sort((a, b) => a.line - b.line);
		});
	}
</script>

<div class="problems">
	{#each Object.keys(problemsByFile) as file}
        <div class="file">
            <p class="file-name">{file.split('/').pop()}</p>
            <p class="file-path">{file}</p>
        </div>
		{#each problemsByFile[file] as problem}
			<Error
                file={problem.file}
				lineNumber={problem.line}
				codeSnippet={problem.codeSnippet}
				errorMessage={problem.message}
			/>
		{/each}
	{/each}
</div>

<style lang="scss">
	.problems {
		max-width: 100%;
		display: flex;
		flex-direction: column;
		background-color: #1e1e1e;
		padding-left: 2rem;
		color: #d4d4d4;
		overflow-x: scroll;

		&::-webkit-scrollbar {
			display: none;
		}

        .file {
            display: flex;
            flex-direction: row;
            align-items: flex-end;
            justify-content: flex-start;
            gap: 10px;
            margin-bottom: 0.5rem;

            .file-name {
                margin: 0;
                font-size: 0.9rem;
            }


            .file-path {
                margin: 0;
                font-size: 0.75rem;
                color: rgb(187, 187, 187);
            }
        }
	}
</style>
