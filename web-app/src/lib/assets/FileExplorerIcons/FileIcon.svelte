<script>
	import { onMount } from 'svelte';
	import DefaultIcon from './DefaultIcon.svelte';
	export let fileExtension;

	let IconComponent = null;

	onMount(async () => {
		if (fileExtension) {
			try {
                console.log(fileExtension)
				const module = await import(`./icons/${fileExtension}.svelte`);
				IconComponent = module.default;
			} catch (error) {
				console.error(`Icon component for extension ${fileExtension} not found.`);
			}
		}
	});
</script>

{#if IconComponent}
	<svelte:component this={IconComponent} />
{:else}
	<!-- Default icon if no icon component is found -->
	<DefaultIcon />
{/if}
