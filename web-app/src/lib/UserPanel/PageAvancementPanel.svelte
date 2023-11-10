<script>

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

    <div class="graph-avancement">
        <div class="graph-groupe-A">
            <h2> Avancement du Groupe A</h2>
            <div class="progress-bar">
                <span class="title">Nombre de test: 350 / 1200</span>
                <div class="bar" style="width: 100%;">
                    <div class="percentage-bar" style="width: 30%;"></div>
                    <span class="percentage">30%</span>
                </div>
            </div>
        </div>

        <div class="graph-groupe-B">
            <h2> Avancement du Groupe B</h2>
            <div class="progress-bar">
                <span class="title">Nombre de test: 550 / 1200</span>
                <div class="bar" style="width: 100%;">
                    <div class="percentage-bar" style="width: 70%;"></div>
                    <span class="percentage">70%</span>
                </div>
            </div>
        </div>

        <div class="graph-complet">
            <h2> Avancement de la Promotion</h2>
            <div class="progress-bar">
                <span class="title">Nombre de test: 900 / 2400</span>
                <div class="bar" style="width: 100%;">
                    <div class="percentage-bar" style="width: 50%;"></div>
                    <span class="percentage">50%</span>
                </div>
            </div>
        </div>
    </div>

    <div class="avancement-individuel">
        <h1>Avancement Individuel</h1>
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
        justify-content: space-between;
    }

    .title{
        justify-self: center;
    }

    .tp-selector {
        display: flex;
        justify-self: flex-end; 

        label {
            margin-right: 0.5rem;
        }
    }

    .graph-avancement{
        width: 100%;
        display: flex;
        flex-direction: row;
        justify-content: space-between;

        .graph-groupe-A,
        .graph-groupe-B,
        .graph-complet {
            padding-left: 3rem;
            padding-right: 3rem;
        }
    }

    .progress-bar {
        margin-bottom: 0.5rem;

        .title {
            display: block;
            margin-bottom: 0.2rem;
        }

        .bar {
            height: 20px; 
            position: relative;
            border: 2px solid white;
            border-radius: 20px;

            .percentage-bar {
                background-color: rgb(52,120,198);
                border-radius: 8px;
                height: 100%;
                position: absolute;
            }

            .percentage {
                position: absolute;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
                color: white;
            }
        }
    }
</style>