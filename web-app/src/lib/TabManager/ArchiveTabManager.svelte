<script>
	import ArchiveTabManagerItem from "./ArchiveTabManagerItem.svelte";
    import { openedArchiveTabs } from "$lib/stores.js";
</script>

<div class="tab-manager">
    {#each $openedArchiveTabs as file (file)}
        <ArchiveTabManagerItem
            currentFile={file}
        />
    {/each}
</div>

<style lang="scss">    
    .tab-manager {
        height: 4%;
        width: 100%;
        display: flex;
        background-color: rgb(45,45,45);
    }
</style>