<script>
    export let onClick;
    export let text;
    export let icon;
    export let active;

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
<div class="navbar-item" on:mouseover={handleMouseOver} on:mouseout={handleMouseOut} on:click={handleClick}>
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
        fill:white;
    }

    .tooltip {
        position: absolute;
        background-color: rgba(0, 0, 0, 0.8);
        color: white;
        padding: 0.5rem;
        border-radius: 10px;
        pointer-events: none;
    }
</style>
