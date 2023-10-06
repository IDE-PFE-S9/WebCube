<script>
    import FileIcon from '$lib/assets/FileExplorerIcons/FileIcon.svelte';
    import DeleteIcon from "/src/lib/assets/TabManagerIcons/DeleteIcon.svelte";

    export let name;
    export let onClick;
    export let activeItem;

    let fileExtension = name.split('.').pop();

    function handleClick() {
        if (onClick) {
            onClick();
        }
    }
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<!-- svelte-ignore a11y-no-static-element-interactions -->
<div class="tab-item" on:click={handleClick} class:selected={activeItem === name}>
    <FileIcon fileExtension={fileExtension} />
    <div>{name}</div>
    <svelte:component this={DeleteIcon}/>
</div>

<style lang="scss">
    .tab-item {
        display: flex;
        align-items: center;
        padding: 0 10px;
        cursor: pointer;
        color: white;

        &.selected {
            background-color: #1e1e1e;
        }
    }

    .tab-item:hover {
        background-color: #1e1e1e;
    }
</style>