<script>
    import GlobalAdvancement from './GlobalAdvancement.svelte';
    import StudentAdvancement from './StudentAdvancement.svelte';
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
        <GlobalAdvancement name={"Groupe A"} nbTestStudent={1000} nbTestTp={1000}></GlobalAdvancement>

        <GlobalAdvancement name={"Groupe B"} nbTestStudent={500} nbTestTp={1000}></GlobalAdvancement>

        <GlobalAdvancement name={"Global"} nbTestStudent={1500} nbTestTp={2000}></GlobalAdvancement>
    </div>

    <div class="avancement-individuel">
        <h1 class="individuel-title">Avancement Individuel</h1>
        <StudentAdvancement></StudentAdvancement>
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