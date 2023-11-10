<script>
    import { onMount } from 'svelte';
    import { login, getUserDetails } from '$lib/auth.js'; // Assuming the MSAL code is in auth.js

</script>

<button on:click={login}>Login with Azure AD</button>
<button on:click={getUserDetails}>User Details</button>

  