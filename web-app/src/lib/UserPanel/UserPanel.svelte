<script>
    import UserPanelNavItem from './UserPanelNavItem.svelte';
    import { adminNavbarActiveItem, terminalOutput, archiveMode } from '$lib/stores.js';
	
    import GeneralPagePanel from './GeneralPagePanel.svelte';
    import ExamenPagePanel from './ExamenPagePanel.svelte';
    import AvancementPagePanel from './AvancementPagePanel.svelte';
    import AutresPagePanel from './AutresPagePanel.svelte';

    const navbarItems = [{ text: 'Général' }, { text: 'Examen' }, { text: 'Avancement' }, { text: 'Autres' }];

    function manageItemClick(itemText) {
		adminNavbarActiveItem.set(itemText);
	}
</script>

<div>
    <div class="navbar">
        <div class="navbar-buttons">
            {#each navbarItems as { text }}
                <UserPanelNavItem {text} onClick={() => manageItemClick(text)} />
            {/each}
        </div>
    </div>

    <div class="admin-container">
        {#if $adminNavbarActiveItem === 'Général'}
            <GeneralPagePanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Examen'}
            <ExamenPagePanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Avancement'}
            <AvancementPagePanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Autres'}
            <AutresPagePanel />
        {/if}
    </div>
</div>

<style lang="scss">
   .navbar {
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
		padding-left: 1rem;
		padding-right: 1rem;

		.navbar-buttons {
			display: flex;
			flex-direction: row;
			align-items: flex-start;
			gap: 0.6rem;
		}
	}
    .admin-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 100%;
        color: white;
}
</style>
