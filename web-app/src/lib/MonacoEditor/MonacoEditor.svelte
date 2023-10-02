<script>
    import { onMount } from "svelte";
    import { openedCode } from '$lib/stores.js';

    let editor;
    let editorContainer;

    onMount(async () => {
        const monaco = await import('monaco-editor');
        // Subscribe to the store to get its current value
        openedCode.subscribe(value => {
            if (editor) {
                editor.setValue(value);
            } else {
                editor = monaco.editor.create(editorContainer, {
                    value,  // Use the value from the store
                    language: 'java',
                    theme: 'vs-dark',
                    automaticLayout: true,
                });
            }
        });
    });
</script>

<div id="editor-container" bind:this={editorContainer}></div>


<style lang="scss">
    #editor-container {
        height: 70%;
        width: 100%;
    }
</style>