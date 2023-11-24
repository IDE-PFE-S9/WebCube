<script>
    import '@carbon/charts-svelte/styles.css'
    import { GaugeChart } from '@carbon/charts-svelte'

    export let name;
    export let completions;

    let color = "rgb(52,120,198)";
    let data = []

    let options = {}

    $: {
        let somme = 0;
        for (let i = 0; i < completions.length; i++) {
            somme += completions[i];
        }
        let percentage = somme / completions.length;

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