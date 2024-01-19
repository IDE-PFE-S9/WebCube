<script>
    import { onMount } from 'svelte';
    import { login, getUserDetails } from '$lib/auth.js'; // Assuming the MSAL code is in auth.js
    import Cookies from 'js-cookie';

    onMount(() => {
        // Check if the user is already logged in
        if (Cookies.get('azureJWT') === null) {
            login()
        } else {
            console.log('Already logged in')
        }
    });
</script>

<button on:click={login}>Login with Azure AD</button>
<button on:click={getUserDetails}>User Details</button>