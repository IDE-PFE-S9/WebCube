<script>
    import ClearIcon from "$lib/assets/TerminalNavbarIcons/ClearIcon.svelte";
    import { terminalOutput } from '$lib/stores.js';
</script>

<button class="clear-button" on:click={() => $terminalOutput = []}>
    <ClearIcon />
</button>

<style lang="scss">
    .clear-button {
        all: unset;
        cursor: pointer;
    }
</style>
