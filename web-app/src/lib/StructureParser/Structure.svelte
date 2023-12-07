<script>
    import Chevron from '$lib/assets/StructureParserIcons/chevron.svelte';
    import ClassIcon from '$lib/assets/StructureParserIcons/class.svelte';
    import MethodIcon from '$lib/assets/StructureParserIcons/method.svelte';
    import ObjectIcon from '$lib/assets/StructureParserIcons/object.svelte';

    export let structure;
    let expanded = false;

    function toggleExpand() {
        expanded = !expanded;
    }
</script>

<div id="container">
    {#if structure.classes != null}
        {#each structure.classes as classe}
            <button class="classe" on:click={toggleExpand}>
                <ClassIcon />
                {classe.name}
            </button>
            {#if expanded}

                {#if classe.methods != null}
                    {#each classe.methods as method}
                        <div class="methods">
                            <MethodIcon />
                            {method.name}
                        </div>
                    {/each}
                {/if}

                {#if classe.fields != null}
                    {#each classe.fields as field}
                        <div class="fields">
                            <ObjectIcon />
                            {field.name}
                        </div>
                    {/each}
                {/if}

            {/if}
        {/each}
    {/if}
</div>

<style lang='scss'>
    #container {
		width: 100%;
        color: rgb(204, 204, 204);
        font-size: 0.9rem;
        .classe {
			all: unset;
			display: flex;
			flex-direction: row;
			align-items: center;
			gap: 0.5rem;
			padding: 0.3rem;
			padding-left: 0.7rem;
            width: 100%;
			cursor: pointer;

			&:hover {
				background-color: rgb(43, 45, 46);
			}
		}

        .methods{
            display: flex;
            padding: 0.1rem;
			padding-left: 2rem;
            gap: 0.5rem;
        }

        .fields{
            display: flex;
            padding: 0.1rem;
			padding-left: 2rem;
            gap: 0.5rem;
        }
    }
</style>