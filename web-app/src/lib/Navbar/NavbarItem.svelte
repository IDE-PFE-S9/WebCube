<script>
    export let onClick;
    export let text;
    export let icon;
    export let activeItem;

    let showText = false;

    function handleMouseOver() {
        showText = true;
    }

    function handleMouseOut() {
        showText = false;
    }

    function handleClick() {
        if (onClick) {
            onClick();
        }
    }
</script>

<!-- svelte-ignore a11y-no-static-element-interactions -->
<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<!-- svelte-ignore a11y-click-events-have-key-events -->
<div class="navbar-item" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut} on:click={handleClick} class:selected={activeItem === text}>
    <div class="indicator" class:selected={activeItem === text}></div>
    {#if showText}
        <div class="tooltip">
            {text}
        </div>
    {/if}
    <svelte:component this={icon}/>
</div>


<style lang="scss">
    .navbar-item {
        display: flex;
        align-items: center;
        cursor: pointer;
        margin-top: 1rem;
        fill:rgb(133, 133, 133);

        &:hover {
                fill:lightgray;
            &.selected {
                fill:white;
            }
        }

        &.selected {
            fill:white;
        }
    }
    
    .indicator {
        position: relative;
        left:-0.5rem;
        top: 0;
        width: 2px;
        height: 100%;
        background-color: white;
        opacity: 0;

        &.selected{
            opacity: 1;
        }
    }

    .tooltip {
        position: absolute;
        background-color: rgba(0, 0, 0, 0.4);
        color: white;
        padding: 0.5rem;
        border-radius: 10px;
        pointer-events: none;
        left: 4rem;
    }
</style>
