<script>
    import GlobalAdvancement from './GlobalAdvancement.svelte';
    import StudentAdvancement from './StudentAdvancement.svelte';
    import Cookies from 'js-cookie';
    import { onMount } from 'svelte';

    let completions = [];
    let names = [];

    async function getAdvancementTp(tpId) {
        const response = await fetch('http://localhost:4444/api/tp/completion/etudiants/'+tpId, {
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

        console.log('Noms:', names);
        console.log('Completions:', completions);

        return {names, completions};
    }

    onMount(async () => {
        try {
            await getAdvancementTp(2);
        } catch (error) {
            console.error('Une erreur est survenue:', error);
        }
    });
</script>

<div class="avancement-container">
    <div class="header-container">
        <h1 class="title">Dashboard Avancement</h1>

        <div class="tp-selector">
            <label for="tpDropdown">Sélectionner un TP :</label>
            <select id="tpDropdown">
                <option value="tp1">TP 1</option>
                <option value="tp2">TP 2</option>
                <option value="tp3">TP 3</option>
            </select>
        </div>
    </div>

    <div class="graph-container">
        <GlobalAdvancement name={"Groupe A"} completions={completions}></GlobalAdvancement>

        <GlobalAdvancement name={"Groupe B"} completions={completions}></GlobalAdvancement>

        <GlobalAdvancement name={"Global"} completions={completions}></GlobalAdvancement>
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
        margin-left: auto;
        label {
            margin-right: 0.5rem;
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