<script>
    import '@carbon/charts-svelte/styles.css'
    import { GaugeChart } from '@carbon/charts-svelte'

    export let name;
    export let nbTestStudent;
    export let nbTestTp;

    let percentage = Math.round((nbTestStudent / nbTestTp) * 100);
    let data = [{"group": "value","value": percentage}]

    let color = "rgb(52,120,198)"; 
    if (percentage === 100) {
        color = "green"; 
    }

    let options = {
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