<script>
	import TabManagerItem from "./TabManagerItem.svelte";

    let activeItem = "fichier1.svelte";

    let files = [
        {name:"fichier1.svelte"}, 
        {name:"fichier2.py"},
        {name:"fichier.js"},
        {name:"fichier.c"}
    ];

    function manageItemClick(name) {
        activeItem = name;
        console.log("coucou");
    }

    function deleteFile(name) {
        files = files.filter(file => file.name !== name);
    }

</script>

<div class="tab-manager">
    {#each files as {name}}
        <TabManagerItem
            name={name}
            onClick={() => manageItemClick(name)}
            activeItem={activeItem}
            onDelete={() => deleteFile(name)}
        />
    {/each}
</div>

<style lang="scss">    
    .tab-manager {
        height: 5%;
        width: 100%;
        display: flex;
        background-color: rgb(45,45,45);
    }
</style>