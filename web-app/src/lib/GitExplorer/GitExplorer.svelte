<script>
	import { onMount } from 'svelte';
	import TpButton from './TpButton.svelte';
	import {isResponseOk} from '$lib/auth.js';
	import Cookies from 'js-cookie';
	import { examMode } from '$lib/stores.js';

	let apiUrl = process.env.API_URL;

	let tpJson = [];

	onMount(async () => {
		let headersList = {
			Accept: '*/*',
			'Authorization-Azure': 'Bearer ' + Cookies.get('azureJWT'),
			'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
		};
		const tpResponse = await fetch(`${apiUrl}/api/tp`, {
			method: 'GET',
			headers: headersList
		});
		if(isResponseOk(tpResponse)) {
			let data = await tpResponse.json();
			tpJson = data.filter(tp => $examMode ? tp.type === 'examen' : tp.type === 'TP');
		}
	});
</script>

<div class="tp-explorer">
	<div id="title-container">
		<h1 id="title">Explorateur</h1>
	</div>
	{#each tpJson as tp}
		<TpButton {tp} />
	{/each}
</div>

<style lang="scss">
	.tp-explorer {
		display: flex;
		flex-direction: column;
		background-color: rgb(37, 37, 38);
		min-width: 20rem;
		height: 70%;

		// Base styles for the scrollbar
		&::-webkit-scrollbar {
			width: 12px;
			background-color: rgba(255, 255, 255, 0); // Fully transparent track
		}

		// Styling the thumb (the draggable scrolling element)
		&::-webkit-scrollbar-thumb {
			background-color: rgba(255, 255, 255, 0); // Fully transparent by default
			border: 2px solid transparent;
			background-clip: content-box; // Makes the thumb smaller than the track
			transition: background-color 1s ease; // Transition effect for the hover state
		}

		// Hiding the corner where horizontal and vertical scrollbars meet
		&::-webkit-scrollbar-corner {
			background-color: transparent;
		}

		// Styles when the container is hovered
		&:hover {
			// Make the thumb semi-transparent white on hover
			&::-webkit-scrollbar-thumb {
				background-color: rgba(255, 255, 255, 0.1);
			}

			// Hide the horizontal scrollbar on hover
			&::-webkit-scrollbar:horizontal {
				display: none;
			}
		}

		#title-container {
			align-self: self-start;
			#title {
				padding-left: 1rem;
				font-size: 0.7rem;
				text-transform: uppercase;
				font-weight: 300;
				color: rgb(187, 187, 187);
			}
		}
	}
</style>
