<script>
    import { terminalOutput } from '$lib/stores.js';
	import RunIcon from "../assets/TerminalNavbarIcons/RunIcon.svelte";
</script>

<button class="run-button" on:click={() => $terminalOutput = [...$terminalOutput, `/opt/homebrew/Cellar/git/2.38.1/bin/git -C "/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Co
    urs-i3/S9/PFE/WebCube" -c "core.editor=code --wait --reuse-window" branch WC54-Terminal dev'`]}>
    <RunIcon />
</button>

<style lang="scss">
    .run-button {
        all: unset;
        cursor: pointer;
    }
</style>
