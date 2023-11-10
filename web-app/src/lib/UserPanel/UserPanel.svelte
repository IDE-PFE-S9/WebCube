<script>
    import UserPanelNavItem from './UserPanelNavItem.svelte';
    import { adminNavbarActiveItem, terminalOutput, archiveMode } from '$lib/stores.js';
	
    import PageGeneralPanel from './PageGeneralPanel.svelte';
    import PageExamenPanel from './PageExamenPanel.svelte';
    import PageAvancementPanel from './PageAvancementPanel.svelte';
    import PageAutresPanel from './PageAutresPanel.svelte';

    const navbarItems = [{ text: 'Général' }, { text: 'Examen' }, { text: 'Avancement' }, { text: 'Autres' }];

    function manageItemClick(itemText) {
		adminNavbarActiveItem.set(itemText);
	}
</script>

<div class="admin-container">
    <div class="navbar">
        <div class="navbar-buttons">
            {#each navbarItems as { text }}
                <UserPanelNavItem {text} onClick={() => manageItemClick(text)} />
            {/each}
        </div>
    </div>

    <div class="admin-container">
        {#if $adminNavbarActiveItem === 'Général'}
            <PageGeneralPanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Examen'}
            <PageExamenPanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Avancement'}
            <PageAvancementPanel />
        {/if}

        {#if $adminNavbarActiveItem === 'Autres'}
            <PageAutresPanel />
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
        width: 100%;
        color: white;
}
</style>
