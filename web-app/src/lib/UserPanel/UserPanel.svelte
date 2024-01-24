<script>
    import UserPanelNavItem from './UserPanelNavItem.svelte';
    import { adminNavbarActiveItem, terminalOutput, archiveMode } from '$lib/stores.js';

    import Cookies from 'js-cookie';
    import { onMount } from 'svelte';
	
    import PageGeneralPanel from './PageGeneralPanel.svelte';
    import PageExamenPanel from './PageExamenPanel.svelte';
    import PageAvancementTeacherPanel from './PageAvancementTeacherPanel.svelte';
    import PageAvancementStudentPanel from './PageAvancementStudentPanel.svelte';
    import PageImportPanel from './PageImportPanel.svelte';

    const navbarItems = [{ text: 'Général' }, { text: 'Examen' }, { text: 'Avancement' }, { text: 'Import' }];
    let apiUrl = process.env.API_URL;
    let isTeacher = false;

    function manageItemClick(itemText) {
		adminNavbarActiveItem.set(itemText);
	}

    onMount(async () => {
		const userResponse = await fetch(`${apiUrl}/api/user`, {
			method: 'GET',
			headers: {
				'Authorization-API': 'Bearer ' + Cookies.get('apiJWT')
			}
		});
		let userJson = await userResponse.json();
        
        userJson.roles.forEach(roles => {
            if(roles.role == 'ROLE_TEACHER'){
                isTeacher = true;
            }
        });
        
	});
</script>

<div class="container">
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
            {#if isTeacher}
                <PageAvancementTeacherPanel />
            {:else}
                <PageAvancementStudentPanel />
            {/if}
        {/if}

        {#if $adminNavbarActiveItem === 'Import'}
            <PageImportPanel />
        {/if}
    </div>
</div>

<style lang="scss">
    .container{
        overflow: scroll;
        width: 100%;
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
		}
                // Hide the horizontal scrollbar on hover
        &::-webkit-scrollbar:horizontal {
            display: none;
        }
    }

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
