<script>
    import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
    import DeleteIcon from "/src/lib/assets/TabManagerIcons/DeleteIcon.svelte";

    export let name;
    export let onClick;
    export let activeItem;
    export let onDelete;

    let fileExtension = name.split('.').pop();

    function handleClick() {
        if (onClick) {
            onClick();
        }
    }

    function handleDelete() {
        if (onDelete) {
            onDelete(name);
        }
    }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div class="tab-item"  on:click={handleClick} class:selected={activeItem === name}>
    <div class="file-item">
        <FileIcon fileExtension={fileExtension} />
        <div class="tab-item-name">{name}</div>
    </div>
    <div class="delete-icon" on:click={handleDelete} class:selected={activeItem === name}>
        <svelte:component this={DeleteIcon} />
    </div>
</div>

<style lang="scss">
    .tab-item {
        display: flex;
        flex-direction: row;
        align-items: center;
        padding: 0 10px;
        justify-content: center;
        cursor: pointer;
        color: rgb(136,136,136);

        &.selected {
            background-color: rgb(30,30,30);
            color: white;
        }

        &:hover{
            background-color: rgb(30,30,30);
        }
    }

    .file-item{
        display: flex;
        flex-direction: row;
        align-items: center; 
    }

    .tab-item-name {
        margin: 0 5px;
    }

    .delete-icon {
        display: flex;
        align-items: center; 
    }

</style>