<script>
    import '@carbon/charts-svelte/styles.css'
    import { GaugeChart } from '@carbon/charts-svelte'

    export let name;
    export let completions;
    export let groups;

    let color = "rgb(52,120,198)";
    let data = []

    let options = {}

    $: {
        let somme = 0;
        let percentage = 0;
        let occurence = 0;

        if(name == "Global"){
            for (let i = 0; i < completions.length; i++) {
                somme += completions[i];
            }
            percentage = somme / completions.length;
        } 
        
        else if (name == "Groupe A") {
            for (let i = 0; i < completions.length; i++) {
                if (groups[i].includes("ROLE_GROUP_A")) {
                    somme += completions[i];
                    occurence++;
                }
                percentage = somme / occurence;
            }
        } 
        
        else if (name == "Groupe B") {
            for (let i = 0; i < completions.length; i++) {
                if (groups[i].includes("ROLE_GROUP_B")) {
                    somme += completions[i];
                    occurence++;
                }
                percentage = somme / occurence;
            }
        }

        color = "rgb(52,120,198)";
        if (percentage === 100) {
            color = "rgb(218,153,36)";
        }

        data = [{"group": "value","value": percentage}]
        options = {
            "resizable": true,
            "height": "100px",
            "width":"200px",
            "gauge": {
                "type": "semi",
                "status": "danger",
            },
            "toolbar":{"enabled":false},
            "theme": "g100",
            "color": {
                "scale": {
                    "value": color
                }
            }
        }
    }
</script>

<div class="individuel">
    <h4 class="name">{name}</h4>
    <GaugeChart {data} {options} />
</div>

<style lang="scss">
     .individuel {
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        margin-bottom: 1rem;
        width: 20rem;
    }
    .name {
        margin: 0.5rem;
    }
</style>