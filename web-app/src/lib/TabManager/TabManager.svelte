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
        max-width: 100%;
        display: flex;
        background-color: rgb(45,45,45);
        overflow-x: scroll;
        white-space: nowrap;

        // Hiding the scrollbar by default
		&::-webkit-scrollbar {
			height: 2px; 
			background-color: transparent; // This ensures the scrollbar track is invisible
		}

        // Setting scrollbar thumb color to be fully transparent initially
		&::-webkit-scrollbar-thumb {
			background-color: rgba(255, 255, 255, 0); // fully transparent
			transition: background-color 0.3s ease; 
		}

        // Displaying scrollbar thumb as transparent white on hover
		&:hover::-webkit-scrollbar-thumb {
			background-color: rgba(255, 255, 255, .1);
		}
    }
</style>