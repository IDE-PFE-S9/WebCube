<script>
	import NavbarItem from "./NavbarItem.svelte";
    import parameter_icon from "/src/lib/assets/navbarIcon/parameters_icon.svelte";
    import file_icon from "/src/lib/assets/navbarIcon/files_icon.svelte";
    import git_icon from "/src/lib/assets/navbarIcon/git_icon.svelte";
    import exec_icon from "/src/lib/assets/navbarIcon/exec_icon.svelte";
    import uml_icon from "/src/lib/assets/navbarIcon/uml_icon.svelte";
    import gitlab_icon from "/src/lib/assets/navbarIcon/gitlab_icon.svelte";
    import user_icon from "/src/lib/assets/navbarIcon/user_icon.svelte";

    let activeItem = "Explorer";

    function manageExplorerClick() {
        activeItem = "Explorer";
    }

    function manageFilesClick() {
        activeItem = "Fichiers";
    }

    function manageExecutionClick() {
        activeItem = "Execution"; 
    }

    function manageUmlClick() {  
        activeItem = "Uml";
    }

    function manageGitlabClick() {  
        window.open("https://172.24.7.8/", "_blank");
    }

    function manageUserClick() {  
        activeItem = "Utilisateurs";
    }
    
    function manageParametersClick() {  
        activeItem = "Paramètres";
    }
</script>


<div class="navbar">
    <NavbarItem text="Explorer" icon={file_icon} onClick={manageExplorerClick} activeItem={activeItem}/>
    <NavbarItem text="Fichiers" icon={git_icon} onClick={manageFilesClick} activeItem={activeItem}/>
    <NavbarItem text="Execution" icon={exec_icon} onClick={manageExecutionClick} activeItem={activeItem}/>
    <NavbarItem text="Uml" icon={uml_icon} onClick={manageUmlClick} activeItem={activeItem}/>
    <NavbarItem text="Gitlab" icon={gitlab_icon} onClick={manageGitlabClick} activeItem={activeItem}/>
    <NavbarItem text="Utilisateur" icon={user_icon} onClick={manageUserClick} activeItem={activeItem}/>
    <NavbarItem text="Paramètres" icon={parameter_icon} onClick={manageParametersClick} activeItem={activeItem}/>
</div>


<style lang="scss">
    .navbar {
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
        background-color: rgb(51,51,51);
        color: white;
        width: 4rem;
        height: 100vh;
    }
</style>