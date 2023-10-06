<script>
    import { onMount } from "svelte";
    import { openedCode } from '$lib/stores.js';

    let editor;
    let editorContainer;

    onMount(async () => {
        const monaco = await import('monaco-editor');
        
        editor = monaco.editor.create(editorContainer, {
            value: '', // initial value can be an empty string
            language: 'java',
            theme: 'vs-dark',
            automaticLayout: true,
        });
        
        const updateOpenedCode = () => {
            const newValue = editor.getValue();
            openedCode.set(newValue); // Update the store with new content
        };
        
        // Listen for changes in the editor content and update the store
        editor.onDidChangeModelContent(updateOpenedCode);

        // Subscribe to the store to get its current value
        const unsubscribe = openedCode.subscribe(value => {
            if(editor) {
                // Avoid updating the editor value if it's already the same as the store value
                if(value !== editor.getValue()) {
                    editor.setValue(value);
                }
            }
        });

        // Return a cleanup function to unsubscribe from the store when the component is destroyed
        return () => unsubscribe();
    });
</script>

<div id="editor-container" bind:this={editorContainer}></div>


<style lang="scss">
    #editor-container {
        height: 70%;
        width: 100%;
    }
</style>