<script>
	import NavbarItem from "./NavbarItem.svelte";



</script>


<div class="navbar">
    <NavbarItem href="/" text="Home" />
    <NavbarItem href="/about" text="About" />
    <NavbarItem href="/contact" text="Contact" />
</div>


<style lang="scss">
    .navbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background-color: #333;
        color: white;
    }
</style>