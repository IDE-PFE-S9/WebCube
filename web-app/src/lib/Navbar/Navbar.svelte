<script>
	import NavbarItem from "./NavbarItem.svelte";
    import ParametersIcon from "/src/lib/assets/NavbarIcons/ParametersIcon.svelte";
    import FilesIcon from "/src/lib/assets/NavbarIcons/FilesIcon.svelte";
    import GitIcon from "/src/lib/assets/NavbarIcons/GitIcon.svelte";
    import ExecutionIcon from "/src/lib/assets/NavbarIcons/ExecutionIcon.svelte";
    import UmlIcon from "/src/lib/assets/NavbarIcons/UmlIcon.svelte";
    import UserIcon from "/src/lib/assets/NavbarIcons/UserIcon.svelte";

    let activeItem = "Explorer";
    const navbarItems = [
        { text: "Explorer", icon: FilesIcon },
        { text: "Fichiers", icon: GitIcon },
        { text: "Exécution", icon: ExecutionIcon },
        { text: "Uml", icon: UmlIcon },
        { text: "Utilisateur", icon: UserIcon },
        { text: "Paramètres", icon: ParametersIcon },
    ];

    function manageItemClick(itemText, link) {
        if (link) {
            window.open(link, "_blank");
        } else {
            activeItem = itemText;
        }
    }
</script>

<div class="navbar">
    {#each navbarItems as { text, icon, link }}
        <NavbarItem
            text={text}
            icon={icon}
            onClick={() => manageItemClick(text, link)}
            activeItem={activeItem}
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