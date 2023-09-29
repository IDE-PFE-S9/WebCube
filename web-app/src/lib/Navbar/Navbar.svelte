<script>
	import NavbarItem from "./NavbarItem.svelte";
    import parameter_icon from "/src/lib/assets/navbarIcon/parameters_icon.svelte";
    import file_icon from "/src/lib/assets/navbarIcon/files_icon.svelte";
    import git_icon from "/src/lib/assets/navbarIcon/git_icon.svelte";
    import exec_icon from "/src/lib/assets/navbarIcon/exec_icon.svelte";
    import uml_icon from "/src/lib/assets/navbarIcon/uml_icon.svelte";

    function manageExplorerClick() {
    }

    function manageFilesClick() {
    }

    function manageExecutionClick() { 
    }

    function manageUmlClick() {  
    }

    function manageParametersClick() {  
    }
</script>


<div class="navbar">
    <NavbarItem text="Explorer" icon={file_icon} onClick={manageExplorerClick}/>
    <NavbarItem text="Fichiers" icon={git_icon} onClick={manageFilesClick}/>
    <NavbarItem text="Execution" icon={exec_icon} onClick={manageExecutionClick}/>
    <NavbarItem text="Uml" icon={uml_icon} onClick={manageUmlClick}/>
    <NavbarItem text="Paramètres" icon={parameter_icon} onClick={manageParametersClick}/>
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