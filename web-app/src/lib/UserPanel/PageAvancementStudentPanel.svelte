<script>
	import TpAdvancement from './GaugeChartAdvancement.svelte';
	import Cookies from 'js-cookie';
	import { onMount } from 'svelte';
	import { isResponseOk } from '$lib/auth.js';


	let apiUrl = process.env.API_URL;
	$: tpJson = null;

	onMount(async () => {
		const tpResponse = await fetch(`${apiUrl}/api/tp/myCompletion`, {
			method: 'GET',
			headers: {
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			}
		});
		if (isResponseOk(tpResponse)){
			tpJson = await tpResponse.json();
			console.log(tpJson)
			// keep only the TP with type 'TP'
			tpJson.tpDetails = tpJson.tpDetails.filter(tp => tp.type === 'TP');
		}
	});
</script>

<div class="avancement-container">
	<h1 class="title">Dashboard Avancement</h1>

	<div class="graph-container">
		{#if tpJson != null}
			{#each tpJson.tpDetails as tp (tp.id)}
				<TpAdvancement name={tp.nom} completions={tp.completion} groups={null}/>
			{/each}
		{:else}
			<p>Aucun TP trouvé.</p>
		{/if}
	</div>

</div>

<style lang="scss">
	.avancement-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.title {
		text-align: center;
		flex-grow: 1;
	}

	.graph-container {
		display: flex;
		flex-wrap: wrap;
		justify-content: center;
		margin: 20px;
	}
</style>
