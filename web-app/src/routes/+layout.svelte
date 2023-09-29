<script>
    import NavbarItem from "$lib/Navbar/Navbar.svelte";
	import Navbar from "$lib/Navbar/Navbar.svelte";
</script>

<div class="main">
    <Navbar />
    <slot />
</div>

<style lang="scss">
    *{
        box-sizing: border-box;
        padding:0;
        margin:0;
    }

    .main{      
        position:absolute;
        left:0;
        top:0;
    }
</style>
