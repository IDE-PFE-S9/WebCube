<script>
	import NavbarItem from "./NavbarItem.svelte";
    import FilesIcon from "/src/lib/assets/NavbarIcons/FilesIcon.svelte";
    import GitIcon from "/src/lib/assets/NavbarIcons/GitIcon.svelte";
    import UmlIcon from "/src/lib/assets/NavbarIcons/UmlIcon.svelte";
    import UserIcon from "/src/lib/assets/NavbarIcons/UserIcon.svelte";

    const navbarItems = [
        { text: "Explorer", icon: FilesIcon },
        { text: "Archive", icon: GitIcon },
        { text: "Uml", icon: UmlIcon },
        { text: "Utilisateur", icon: UserIcon },
    ];
</script>

<div class="navbar">
    {#each navbarItems as { text, icon, link }}
        <NavbarItem
            text={text}
            icon={icon}
        />
    {/each}
</div>


<style lang="scss">
    .navbar {
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
        background-color: rgb(51,51,51);
        color: white;
        min-width: 4rem;
        height: 100%;
    }
</style>