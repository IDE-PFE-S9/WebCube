<script>
	import TabManagerItem from "./TabManagerItem.svelte";

    let activeItem = "fichier1.svelte";

    const files = [
        {name:"fichier1.svelte"}, 
        {name:"fichier2.py"},
        {name:"fichier.js"},
        {name:"fichier.c"}
    ];

    function manageItemClick(name) {
        activeItem = name;
    }

</script>

<div class="tab-manager">
    {#each files as {name}}
        <TabManagerItem
            name={name}
            onClick={() => manageItemClick(name)}
            activeItem={activeItem}
        />
    {/each}
</div>

<style lang="scss">    
    .tab-manager {
        min-height: 4%;
        max-height: 4%;
        width: 100%;
        display: flex;
        background-color: rgb(51,51,51);
    }
</style>