<script>
    export let text;
    export let icon;

    import { currentTab, archiveMode } from '$lib/stores.js'
    import { login } from '$lib/auth.js'

    let showText = false;

    function handleMouseOver() {
        showText = true;
    }

    function handleMouseOut() {
        showText = false;
    }

    function handleClick() {
        currentTab.set(text);
        if ($currentTab == "Archive") {
            archiveMode.set(true);
        } else if ($currentTab == "Explorer") {
            archiveMode.set(false);
        } else if ($currentTab == "Utilisateur") {
            login()
        }
    }
</script>

<!-- svelte-ignore a11y-no-static-element-interactions -->
<!-- svelte-ignore a11y-mouse-events-have-key-events -->
<!-- svelte-ignore a11y-click-events-have-key-events -->
<div
	class="navbar-item"
	on:mouseover={handleMouseOver}
	on:mouseout={handleMouseOut}
	on:click={handleClick}
	class:selected={$currentTab === text}
>
	<div class="indicator" class:selected={$currentTab === text} />
	{#if showText}
		<div class="tooltip">
			{text}
		</div>
	{/if}
	<svelte:component this={icon} />
</div>

<style lang="scss">
	.navbar-item {
		display: flex;
		align-items: center;
		cursor: pointer;
		margin-top: 2rem;
		fill: rgb(133, 133, 133);

		&:hover {
			fill: lightgray;
			&.selected {
				fill: white;
			}
		}

		&.selected {
			fill: white;
		}
	}

	.indicator {
		position: relative;
		left: -0.5rem;
		top: 0;
		width: 2px;
		height: 100%;
		background-color: white;
		opacity: 0;

		&.selected {
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
