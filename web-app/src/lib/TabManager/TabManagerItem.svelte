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
        <FileIcon fileExtension={fileExtension} />
        <div class="tab-item-name">{name}</div>
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
        cursor: pointer;
        color: rgb(136,136,136);
        border-left: 1px solid rgb(41,41,41);
        border-right: 1px solid rgb(41,41,41);

        &.selected {
            border-top: 3px solid rgb(54, 117, 182);
            background-color: rgb(30,30,30);
            color: white;

            .delete-icon{
                fill: white;
            }
        }

        &:hover{
            background-color: rgb(30,30,30);

            .delete-icon{
                fill: rgb(158,158,158);
            }
        }
    }

    .tab-item-name {
        display:flex;
        justify-content: center;
        align-items: center;
        margin: 0 10px;
    }

    .delete-icon { 
        display: flex;
        align-items: center;
        fill: rgb(45,45,45);
        
        &:hover{
            width: 1.5rem;
            height: 1.5rem;
            background-color: rgb(60,60,60);
            border-radius: 5px;

            &.selected{
                background-color: (50,50,50);
            }
        }
    }

</style>