<script>
	import TabManagerItem from "./TabManagerItem.svelte";
    import { openedTabs } from "$lib/stores.js";
</script>

<div class="tab-manager">
    {#each $openedTabs as file (file.name)}
        <TabManagerItem
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