<script>
    import GlobalAdvancement from './GlobalAdvancement.svelte';
    import StudentAdvancement from './StudentAdvancement.svelte';
    import Cookies from 'js-cookie';

    let apiUrl = process.env.API_URL;
    

    let completions = [];
    let names = [];
    let groups = [];
    let selectedTp = 1; 
    let tpJson = [];

    onMount(async () => {
		const tpResponse = await fetch(`${apiUrl}/api/tp`, {
			method: 'GET',
		});
		tpJson = await tpResponse.json(); 
        tpJson.sort((a, b) => a.name.localeCompare(b.name));
	});

    async function getAdvancementTp(tpId) {
        const response = await fetch(`${apiUrl}/api/tp/completion/etudiants/${tpId}`, {
            headers: {
                'Authorization-Azure': 'Bearer ' + Cookies.get("azureJWT"),
                'Authorization-API': 'Bearer ' + Cookies.get("apiJWT")
            }
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.log(errorData);
            alert('Une erreur est survenue lors de la requête API : ' + errorData.error);
            return null;
        }

        const dataReturned = await response.json();

        names = dataReturned.map(student => `${student.firstname} ${student.surname}`);
        completions = dataReturned.map(student => student.completion);
        groups = dataReturned.map(student => student.roles);
        return {names, completions, groups};
    }

    $:{
        getAdvancementTp(selectedTp);
    }
</script>

<div class="avancement-container">
    <div class="header-container">
        <h1 class="title">Dashboard Avancement</h1>

        <div class="tp-selector">
            <label for="tpDropdown">Sélectionner un TP :</label>
            <select id="tpDropdown" bind:value={selectedTp}>
                {#each tpJson as tp (tp.id)}
                    <option value={tp.id}>{tp.name}</option>
                {/each}
            </select>
        </div>
    </div>

    <div class="graph-container">
        <GlobalAdvancement name={"Groupe A"} completions={completions} groups={groups}></GlobalAdvancement>

        <GlobalAdvancement name={"Groupe B"} completions={completions} groups={groups}></GlobalAdvancement>

        <GlobalAdvancement name={"Global"} completions={completions} groups={groups}></GlobalAdvancement>
    </div>

    <div class="avancement-individuel">
        <h1 class="individuel-title">Avancement Individuel</h1>
        <StudentAdvancement names={names} completions={completions}></StudentAdvancement>
    </div>

</div>

<style lang="scss">
    .avancement-container{
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }

    .header-container {
        width:100%;
        display: flex;
        align-items: center; 
    }

    .title{
        text-align: center;
        flex-grow: 1;
    }

    .tp-selector {
        display: flex;
        align-items: center;

        label {
            margin-right: 0.5rem;
            font-size: 0.8rem;
        }

        select {
            padding: 0.4rem;
            max-width: 150px;
            font-size: 0.8rem;
            border: 2.5px solid;
            border-radius: 20px;
            outline: none;
            cursor: pointer;
            transition: border-color 0.3s ease;
            white-space: nowrap; 
            overflow: hidden;
            text-overflow: ellipsis;

            &:hover,
            &:focus {
                border-color: #3498db;
            }
        
            appearance: none;   
            padding-right: 2rem; 
            background-color: white; 
            background-image: url('data:image/svg+xml,\
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="%233498db" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>'); // Ajoute une petite flèche à droite
            background-repeat: no-repeat;
            background-position: right 0.5rem center;
        }
    }
    .graph-container{
        display: flex;
        flex-wrap: wrap;
    }

    .individuel-title{
        text-align: center;
    }
</style>