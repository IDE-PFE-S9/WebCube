<script>
	export let onClick;
	export let text;
	import { terminalNavbarActiveItem } from '$lib/stores.js';

</script>

<button class="navbar-item" on:click={onClick()} class:selected={$terminalNavbarActiveItem === text}>
	<h3 class="nav-title">{text}</h3>
	<div class="indicator" class:selected={$terminalNavbarActiveItem === text} />
</button>

<style lang="scss">
	.navbar-item {
		all: unset;
		cursor: pointer;
		color: rgb(187, 187, 187);
		padding: 0 1rem;

		&:hover {
			color: white;
		}

		&.selected {
			color: white;
		}

		.nav-title {
			font-size: 0.75rem;
			text-transform: uppercase;
			font-weight: 300;
		}

		.indicator {
			position: relative;
			bottom: 0.5rem;
			width: 100%;
			height: 1px;
			background-color: white;
			opacity: 0;

			&.selected {
				opacity: 1;
			}
		}
	}
</style>
